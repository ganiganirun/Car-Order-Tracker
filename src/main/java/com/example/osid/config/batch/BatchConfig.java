package com.example.osid.config.batch;

import java.util.Map;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.example.osid.domain.order.entity.Orders;
import com.example.osid.domain.order.enums.OrderStatus;
import com.example.osid.domain.order.repository.OrderRepository;
import com.example.osid.domain.waitingorder.entity.WaitingOrders;
import com.example.osid.domain.waitingorder.enums.WaitingStatus;
import com.example.osid.domain.waitingorder.repository.WatingOrderRepository;

import jakarta.persistence.EntityManagerFactory;
import lombok.extern.slf4j.Slf4j;

@Configuration
// @EnableBatchProcessing
@Slf4j
public class BatchConfig {

	private final JobRepository jobRepository;
	private final PlatformTransactionManager txManager;
	private final JpaClearListener jpaClearListener;

	private final WatingOrderRepository watingOrderRepository;
	private final OrderRepository orderRepository;

	public BatchConfig(JobRepository jobRepository, PlatformTransactionManager txManager,
		JpaClearListener jpaClearListener, WatingOrderRepository watingOrderRepository,
		OrderRepository orderRepository) {
		this.jobRepository = jobRepository;
		this.txManager = txManager;
		this.jpaClearListener = jpaClearListener;
		this.watingOrderRepository = watingOrderRepository;
		this.orderRepository = orderRepository;
	}

	// @Bean
	// public Step step1(ItemReader<WaitingOrders> orderReader,
	// 	ItemProcessor<WaitingOrders, History> orderProcessor,
	// 	ItemWriter<History> historyWriter) {
	//
	// 	var name = "step1";
	// 	var builder = new StepBuilder(name, jobRepository);
	// 	// return builder.tasklet(customTasklet, txManager).build();
	// 	// 💡 chunk(1000): 1000건 단위로 반복 처리 (원하는 chunk size로 조정)
	// 	return builder.<WaitingOrders, History>chunk(1000, txManager)
	// 		.reader(orderReader)
	// 		.processor(orderProcessor)
	// 		.writer(historyWriter)
	// 		.build();
	// }

	@Bean
	public JpaPagingItemReader<WaitingOrders> orderReader(@Qualifier("dataEntityManager") EntityManagerFactory emf) {
		log.info("reader 성공");
		return new JpaPagingItemReaderBuilder<WaitingOrders>()
			.name("orderReader")
			.entityManagerFactory(emf)
			.queryString("SELECT o FROM WaitingOrders o WHERE o.status = :status ORDER BY o.createdAt ASC, o.id ASC")
			.parameterValues(Map.of("status", WaitingStatus.WAITING))
			.pageSize(2) // 👉 한 번에 1000건만 DB에서 읽음!
			.maxItemCount(10) // 👉 배치 전체 실행 시 10만 건까지만 반복
			.build();
	}

	// @Bean
	// public RepositoryItemReader<WaitingOrders> orderReader() {
	// 	log.info("reader 성공");
	// 	Map<String, Sort.Direction> sorts = new HashMap<>();
	// 	sorts.put("createdAt", Sort.Direction.ASC);
	// 	sorts.put("id", Sort.Direction.ASC); // 중복 방지용 단조 정렬
	//
	// 	return new RepositoryItemReaderBuilder<WaitingOrders>()
	// 		.name("orderReader")
	// 		.repository(watingOrderRepository)
	// 		.methodName("findAllByStatus")
	// 		.arguments(List.of(WaitingStatus.WAITING)) // 메서드 파라미터 전달
	// 		.sorts(sorts)
	// 		.pageSize(10)
	// 		.maxItemCount(50) // 선택
	// 		.build();
	// }

	@Bean
	public ItemProcessor<WaitingOrders, WaitingOrders> processor() {
		return item -> {
			item.setWatingStatus(WaitingStatus.COMPLETED);
			Orders orders = item.getOrders();
			orders.setOrderStatus(OrderStatus.IN_PRODUCTION);
			return item;
		};
	}

	@Bean
	public JpaItemWriter<WaitingOrders> writer(EntityManagerFactory emf) {
		JpaItemWriter<WaitingOrders> writer = new JpaItemWriter<>();
		writer.setEntityManagerFactory(emf);
		return writer;
	}

	@Bean
	public Job customJob(Step customStep) {

		var name = "customJob";
		var builder = new JobBuilder(name, jobRepository);
		return builder.start(customStep).build();
	}

	@Bean
	@StepScope
	public Step customStep(JpaPagingItemReader<WaitingOrders> orderReader,
		ItemProcessor<WaitingOrders, WaitingOrders> processor,
		JpaItemWriter<WaitingOrders> writer) {

		System.out.println("orderProcessor = " + processor);
		System.out.println("historyWriter = " + writer);
		System.out.println("orderReader = " + orderReader);

		var name = "customStep";
		var builder = new StepBuilder(name, jobRepository);
		// return builder.tasklet(customTasklet, txManager).build();
		// 💡 chunk(1000): 1000건 단위로 반복 처리 (원하는 chunk size로 조정)
		return builder
			.<WaitingOrders, WaitingOrders>chunk(100, txManager)
			.reader(orderReader)
			.processor(processor)
			.writer(writer)
			.faultTolerant()
			.retry(Exception.class)
			.retryLimit(3)
			.listener(jpaClearListener)
			.build();
	}
}