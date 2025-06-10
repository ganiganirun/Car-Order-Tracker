package com.example.osid.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

	// 주문 완료 Queue
	public static final String EXCHANGE = "order.exchange";
	public static final String ORDER_COMPLETE_QUEUE = "order.completed.queue";
	public static final String ROUTING_KEY = "order.completed";
	// DLQ Queue (실패 처리용)
	public static final String DLQ_QUEUE = "order.completed.dlq.queue";
	public static final String DLQ_ROUTING_KEY = "order.completed.dlq";
	public static final String DLX_EXCHANGE = "dlx.exchange";

	@Bean
	public TopicExchange orderExchange() {
		return new TopicExchange(EXCHANGE);
	}

	// DLQ Exchange (실패 처리용)
	@Bean
	public TopicExchange dlxExchange() {
		return new TopicExchange(DLX_EXCHANGE);
	}

	// 주문 완료 처리용
	@Bean
	public Queue orderCompletedQueue() {
		return QueueBuilder.durable(ORDER_COMPLETE_QUEUE)
			// 메시지 처리 실패 시
			.withArgument("x-dead-letter-exchange", DLX_EXCHANGE)
			.withArgument("x-dead-letter-routing-key", DLQ_ROUTING_KEY)
			.build();
	}

	// DLQ 메시지를 저장(재처리 용도)
	@Bean
	public Queue orderCompletedDlqQueue() {
		return QueueBuilder.durable(DLQ_QUEUE).build();
	}

	// 메인 큐 - 메인 익스체인지 연결 (order.completed)
	@Bean
	public Binding orderCompletedBinding() {
		return BindingBuilder
			.bind(orderCompletedQueue())
			.to(orderExchange())
			.with(ROUTING_KEY);
	}

	//  DLQ 큐 - DLX 익스체인지 연결 (order.completed.dlq)
	@Bean
	public Binding dlqBinding() {
		return BindingBuilder
			.bind(orderCompletedDlqQueue())
			.to(dlxExchange())
			.with(DLQ_ROUTING_KEY);
	}
}
