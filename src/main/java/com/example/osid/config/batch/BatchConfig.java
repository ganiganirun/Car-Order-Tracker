package com.example.osid.config.batch;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.osid.domain.history.dto.PredictRequest;
import com.example.osid.domain.history.dto.PredictResponse;
import com.example.osid.domain.history.entity.History;
import com.example.osid.domain.order.entity.Orders;
import com.example.osid.domain.order.enums.OrderStatus;
import com.example.osid.domain.waitingorder.entity.WaitingOrders;
import com.example.osid.domain.waitingorder.enums.WaitingStatus;

import io.netty.channel.ChannelOption;
import jakarta.persistence.EntityManagerFactory;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

@Configuration
// @EnableBatchProcessing
@Slf4j
public class BatchConfig {

	private final JobRepository jobRepository;
	private final PlatformTransactionManager txManager;
	private final JpaClearListener jpaClearListener;
	private final HttpClient httpClient;
	private final WebClient webClient;

	public BatchConfig(JobRepository jobRepository,
		@Qualifier("dataTransactionManager") PlatformTransactionManager txManager,
		JpaClearListener jpaClearListener) {
		this.jobRepository = jobRepository;
		this.txManager = txManager;
		this.jpaClearListener = jpaClearListener;

		// 외부 API 장애, 네트워크 이슈로 인한 배치 지연/무한대기 방지 목적
		this.httpClient = HttpClient.create()
			.responseTimeout(Duration.ofSeconds(25)) // 서버 응답 최대 대기시간 25초
			.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000); // TCP 연결 시도 최대 5초
		this.webClient = WebClient.builder()
			.baseUrl("https://mlp-learning-test.onrender.com")
			.clientConnector(new ReactorClientHttpConnector(httpClient))
			.build();
	}

	/**
	 * 대기 주문(`WaitingOrders`) 엔티티를 커서 방식으로 순차적으로 읽어오는 Reader
	 * - 조건: waitingStatus가 WAITING인 데이터
	 * - 정렬: createdAt, id 기준 (단조성 보장)
	 */
	@Bean
	public JpaCursorItemReader<WaitingOrders> waitingOrderReader(
		@Qualifier("dataEntityManager") EntityManagerFactory emf) {
		JpaCursorItemReader<WaitingOrders> reader = new JpaCursorItemReader<>();
		reader.setName("orderCursorReader");
		reader.setEntityManagerFactory(emf);
		reader.setQueryString(
			"SELECT o FROM WaitingOrders o WHERE o.waitingStatus = :status ORDER BY o.createdAt ASC, o.id ASC");
		reader.setParameterValues(Map.of("status", WaitingStatus.WAITING));
		reader.setMaxItemCount(10);
		return reader;
	}

	/**
	 * 예측 결과(`History`)를 저장하기 위해 상태가 PROCESSING인 `WaitingOrders`를 읽어오는 Reader
	 * - 조건: waitingStatus가 PROCESSING
	 * - 정렬: createdAt, id 기준 (단조성 보장)
	 */
	@Bean
	public JpaCursorItemReader<WaitingOrders> saveHistoriesReader(
		@Qualifier("dataEntityManager") EntityManagerFactory emf) {
		JpaCursorItemReader<WaitingOrders> reader = new JpaCursorItemReader<>();
		reader.setName("orderCursorReader");
		reader.setEntityManagerFactory(emf);
		reader.setQueryString(
			"SELECT o FROM WaitingOrders o WHERE o.waitingStatus = :status ORDER BY o.createdAt ASC, o.id ASC");
		reader.setParameterValues(Map.of("status", WaitingStatus.PROCESSING));
		reader.setMaxItemCount(10);
		return reader;
	}

	/**
	 * 대기 주문의 상태를 PROCESSING으로 변경하고 연관된 주문 상태를 IN_PRODUCTION으로 업데이트하는 Processor
	 */
	@Bean
	public ItemProcessor<WaitingOrders, WaitingOrders> waitingOrderProcessor() {
		return item -> {

			item.setWaitingStatus(WaitingStatus.PROCESSING);
			Orders orders = item.getOrders();
			orders.setOrderStatus(OrderStatus.IN_PRODUCTION);
			return item;
		};
	}

	/**
	 * WebClient를 통해 ML API를 호출하고 예측 결과를 기반으로 History 객체 생성
	 * - waitingStatus는 COMPLETED로 업데이트
	 * - History에 각 공정별 예상 소요시간 및 총 소요시간 저장
	 */
	@Bean
	public ItemProcessor<WaitingOrders, History> historyProcessor() {
		return item -> {
			Orders orders = item.getOrders();
			PredictRequest predictRequest = new PredictRequest(
				orders.getModel().getCategory().toString(),
				orders.getOrderOptions());

			History history = null;
			try {
				PredictResponse response = webClient.post()
					.uri("/predict_all")
					.bodyValue(predictRequest)
					.retrieve()
					.onStatus(HttpStatusCode::is4xxClientError, clientResponse ->
						clientResponse.bodyToMono(String.class)
							.defaultIfEmpty("")
							.map(body -> new RuntimeException("ML API 4xx 오류: " + body))
							.flatMap(Mono::error)
					)
					.onStatus(HttpStatusCode::is5xxServerError, clientResponse ->
						clientResponse.bodyToMono(String.class)
							.defaultIfEmpty("")
							.map(body -> new RuntimeException("ML API 5xx 오류: " + body))
							.flatMap(Mono::error)
					)
					.bodyToMono(PredictResponse.class)
					.block(Duration.ofSeconds(30));

				// 응답값 검증 (null, stage 개수 등)
				if (response == null || response.getStages() == null || response.getStages().size() < 5) {
					log.error("ML 예측 응답값 부족: input={}, response={}", predictRequest, response);
					throw new IllegalStateException("예측 API 응답 값이 부족함: " + response);
				}

				history = new History();
				history.setBodyNumber(orders.getBodyNumber());
				List<PredictResponse.Stage> stages = response.getStages();
				history.setStage1(stages.get(0).getTotalWithDelay());
				history.setStage2(stages.get(1).getTotalWithDelay());
				history.setStage3(stages.get(2).getTotalWithDelay());
				history.setStage4(stages.get(3).getTotalWithDelay());
				history.setStage5(stages.get(4).getTotalWithDelay());
				history.setTotalDuration(response.getTotalDuration());
			} catch (Exception e) {
				log.error("ML API 호출 실패: 주문 정보={}, 요청데이터={}, 에러={}", orders, predictRequest, e.getMessage(), e);
				throw e;
			}
			// log.info("{}, pd: {}, twd: {}, tdh: {}",
			// 	stages.get(0).getStage(),
			// 	stages.get(0).getPredDuration(),
			// 	stages.get(0).getTotalWithDelay(),
			// 	stages.get(0).getTransportDelay()
			// );
			// log.info("{}, pd: {}, twd: {}, tdh: {}",
			// 	stages.get(1).getStage(),
			// 	stages.get(1).getPredDuration(),
			// 	stages.get(1).getTotalWithDelay(),
			// 	stages.get(1).getTransportDelay()
			// );
			// log.info("{}, pd: {}, twd: {}, tdh: {}",
			// 	stages.get(2).getStage(),
			// 	stages.get(2).getPredDuration(),
			// 	stages.get(2).getTotalWithDelay(),
			// 	stages.get(2).getTransportDelay()
			// );
			// log.info("{}, pd: {}, twd: {}, tdh: {}",
			// 	stages.get(3).getStage(),
			// 	stages.get(3).getPredDuration(),
			// 	stages.get(3).getTotalWithDelay(),
			// 	stages.get(3).getTransportDelay()
			// );
			// log.info("{},pd: {}, twd: {}, tdh: {}",
			// 	stages.get(4).getStage(),
			// 	stages.get(4).getPredDuration(),
			// 	stages.get(4).getTotalWithDelay(),
			// 	stages.get(4).getTransportDelay()
			// );
			// log.info("모델: {}, 옵션: {}, 바디넘버: {}, s1: {}, s2: {}, s3: {}, s4: {}, s5: {}, TotalDuration: {}",
			// 	predictRequest.getModelType(),
			// 	predictRequest.getOptionList(),
			// 	history.getBodyNumber(),
			// 	history.getStage1(),
			// 	history.getStage2(),
			// 	history.getStage3(),
			// 	history.getStage4(),
			// 	history.getStage5(),
			// 	history.getTotalDuration()
			// );
			return history;

		};
	}

	/**
	 * 상태가 변경된 `WaitingOrders`를 데이터베이스에 저장하는 Writer
	 * - 연관된 Orders는 Cascade 설정이 되어 있어야 함께 반영됨
	 */
	@Bean
	public JpaItemWriter<WaitingOrders> waitingOrderWriter(@Qualifier("dataEntityManager") EntityManagerFactory emf) {
		JpaItemWriter<WaitingOrders> writer = new JpaItemWriter<>();
		writer.setEntityManagerFactory(emf);
		return writer;
	}

	/**
	 * 예측 결과(`History`)를 데이터베이스에 저장하는 Writer
	 */
	@Bean
	public JpaItemWriter<History> historyWriter(@Qualifier("dataEntityManager") EntityManagerFactory emf) {
		JpaItemWriter<History> writer = new JpaItemWriter<>();
		writer.setEntityManagerFactory(emf);
		return writer;
	}

	/**
	 * Job 구성: 두 개의 Step을 순차적으로 실행 (대기 주문 처리 → 예측 결과 저장)
	 */
	@Bean
	public Job customJob(Step waitingOrderStep, Step historyStep) {

		var name = "customJob";
		var builder = new JobBuilder(name, jobRepository);
		return builder
			.start(waitingOrderStep)
			.next(historyStep)
			.build();
	}

	/**
	 * 대기 주문 상태 업데이트 Step 정의
	 * - Chunk 기반 처리 (chunk size: 10)
	 * - 실패 시 최대 3회 재시도
	 * - Step 완료 후 1차 캐시 clear를 위해 listener 포함
	 */
	@Bean
	public Step waitingOrderStep(JpaCursorItemReader<WaitingOrders> waitingOrderReader,
		ItemProcessor<WaitingOrders, WaitingOrders> waitingOrderProcessor,
		JpaItemWriter<WaitingOrders> waitingOrderWriter) {

		var name = "waitingOrderStep";
		var builder = new StepBuilder(name, jobRepository);
		// return builder.tasklet(customTasklet, txManager).build();
		// 💡 chunk(1000): 1000건 단위로 반복 처리 (원하는 chunk size로 조정)
		return builder
			.<WaitingOrders, WaitingOrders>chunk(10, txManager)
			.reader(waitingOrderReader)
			.processor(waitingOrderProcessor)
			.writer(waitingOrderWriter)
			.faultTolerant()
			.retry(Exception.class)
			.retryLimit(3)
			.listener(jpaClearListener)
			.build();
	}

	/**
	 * 예측 결과 저장 Step 정의
	 * - Chunk 기반 처리 (chunk size: 10)
	 * - 실패 시 최대 3회 재시도
	 * - Step 완료 후 1차 캐시 clear를 위해 listener 포함
	 */
	@Bean
	public Step historyStep(JpaCursorItemReader<WaitingOrders> saveHistoriesReader,
		ItemProcessor<WaitingOrders, History> historyProcessor,
		JpaItemWriter<History> historyWriter,
		HistoryWriteListener historyWriteListener) {

		var name = "historyStep";
		var builder = new StepBuilder(name, jobRepository);
		// return builder.tasklet(customTasklet, txManager).build();
		// 💡 chunk(1000): 1000건 단위로 반복 처리 (원하는 chunk size로 조정)
		return builder
			.<WaitingOrders, History>chunk(10, txManager)
			.reader(saveHistoriesReader)
			.processor(historyProcessor)
			.writer(historyWriter)
			.listener(historyWriteListener) // 후처리 리스너 연결
			.faultTolerant()
			.retry(Exception.class)
			.retryLimit(3)
			.listener(jpaClearListener)
			.build();
	}

}