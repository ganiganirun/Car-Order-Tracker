package com.example.osid.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "mq.enabled", havingValue = "true", matchIfMissing = false)
public class RabbitMQConfig {

	// Main Exchange
	public static final String EXCHANGE = "order.exchange";
	public static final String ROUTING_KEY = "order.completed";

	// MyCar & DLQ
	public static final String MY_CAR_QUEUE = "order.completed.mycar.queue";
	public static final String MY_CAR_DLQ = "order.completed.mycar.dlq.queue";
	public static final String MY_CAR_DLX = "dlx.mycar.exchange";
	public static final String MY_CAR_DLQ_ROUTING_KEY = "order.completed.mycar.dlq";

	// Email & DLQ
	public static final String EMAIL_QUEUE = "order.completed.email.queue";
	public static final String EMAIL_DLQ = "order.completed.email.dlq.queue";
	public static final String EMAIL_DLX = "dlx.email.exchange";
	public static final String EMAIL_DLQ_ROUTING_KEY = "order.completed.email.dlq";

	@Bean
	public TopicExchange orderExchange() {
		return new TopicExchange(EXCHANGE);
	}

	@Bean
	public TopicExchange myCarDlxExchange() {
		return new TopicExchange(MY_CAR_DLX);
	}

	@Bean
	public TopicExchange emailDlxExchange() {
		return new TopicExchange(EMAIL_DLX);
	}

	// 주문 완료 처리용
	//내 차 생성
	@Bean
	public Queue mycarQueue() {
		return QueueBuilder.durable(MY_CAR_QUEUE)
			.withArgument("x-dead-letter-exchange", MY_CAR_DLX)
			.withArgument("x-dead-letter-routing-key", MY_CAR_DLQ_ROUTING_KEY)
			.build();
	}

	// 출고 메일 발송
	@Bean
	public Queue emailQueue() {
		return QueueBuilder.durable(EMAIL_QUEUE)
			.withArgument("x-dead-letter-exchange", EMAIL_DLX)
			.withArgument("x-dead-letter-routing-key", EMAIL_DLQ_ROUTING_KEY)
			.build();
	}

	// DLQ
	@Bean
	public Queue mycarDlqQueue() {
		return QueueBuilder.durable(MY_CAR_DLQ).build();
	}

	@Bean
	public Queue emailDlqQueue() {
		return QueueBuilder.durable(EMAIL_DLQ).build();
	}

	@Bean
	public Binding mycarBinding() {
		return BindingBuilder
			.bind(mycarQueue())
			.to(orderExchange())
			.with(ROUTING_KEY);
	}

	@Bean
	public Binding emailBinding() {
		return BindingBuilder
			.bind(emailQueue())
			.to(orderExchange())
			.with(ROUTING_KEY);
	}

	@Bean
	public Binding mycarDlqBinding() {
		return BindingBuilder
			.bind(mycarDlqQueue())
			.to(myCarDlxExchange())
			.with(MY_CAR_DLQ_ROUTING_KEY);
	}

	@Bean
	public Binding emailDlqBinding() {
		return BindingBuilder
			.bind(emailDlqQueue())
			.to(emailDlxExchange())
			.with(EMAIL_DLQ_ROUTING_KEY);
	}
}
