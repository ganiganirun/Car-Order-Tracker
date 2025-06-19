package com.example.osid.config.batch;

import java.util.Map;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.example.osid.domain.order.entity.Orders;
import com.example.osid.domain.order.entity.WaitingOrders;
import com.example.osid.domain.order.enums.OrderStatus;
import com.example.osid.domain.order.enums.WaitingStatus;

import jakarta.persistence.EntityManagerFactory;
import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableBatchProcessing
@Slf4j
public class BatchConfig {

	JobRepository jobRepository;
	PlatformTransactionManager txManager;
	JpaClearListener jpaClearListener;

	public BatchConfig(JobRepository jobRepository, PlatformTransactionManager txManager) {
		this.jobRepository = jobRepository;
		this.txManager = txManager;
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
	public JpaPagingItemReader<WaitingOrders> orderReader(EntityManagerFactory emf) {
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
	@JobScope
	public Step step1(JpaPagingItemReader<WaitingOrders> orderReader,
		ItemProcessor<WaitingOrders, WaitingOrders> processor,
		JpaItemWriter<WaitingOrders> writer) {

		System.out.println("orderProcessor = " + processor);
		System.out.println("historyWriter = " + writer);
		System.out.println("orderReader = " + orderReader);

		var name = "step1";
		var builder = new StepBuilder(name, jobRepository);
		// return builder.tasklet(customTasklet, txManager).build();
		// 💡 chunk(1000): 1000건 단위로 반복 처리 (원하는 chunk size로 조정)
		return builder
			.<WaitingOrders, WaitingOrders>chunk(1000, txManager)
			.reader(orderReader)
			.processor(processor)
			.writer(writer)
			.listener(jpaClearListener)
			.build();
	}

	@Bean
	public Job customJob(Step step1) {

		var name = "customJob";
		var builder = new JobBuilder(name, jobRepository);
		return builder.start(step1).build();
	}
}
