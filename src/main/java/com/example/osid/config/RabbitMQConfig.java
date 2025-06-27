package com.example.osid.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "mq.enabled", havingValue = "true", matchIfMissing = false)
public class RabbitMQConfig {

	// Main Exchange
	public static final String EXCHANGE = "order.exchange";
	// public static final String ROUTING_KEY = "order.completed";

	// MyCar & DLQ
	public static final String MY_CAR_QUEUE = "order.completed.myCar.queue";
	public static final String MY_CAR_ROUTING_KEY = "order.completed.myCar";
	public static final String MY_CAR_DLQ = "order.completed.myCar.dlq.queue";
	public static final String MY_CAR_DLX = "dlx.myCar.exchange";
	public static final String MY_CAR_DLQ_ROUTING_KEY = "order.completed.myCar.dlq";

	// Email & DLQ
	public static final String EMAIL_QUEUE = "order.completed.email.queue";
	public static final String EMAIL_ROUTING_KEY = "order.completed.email";
	public static final String EMAIL_DLQ = "order.completed.email.dlq.queue";
	public static final String EMAIL_DLX = "dlx.email.exchange";
	public static final String EMAIL_DLQ_ROUTING_KEY = "order.completed.email.dlq";

	// 메일 지연 큐 (실패시 재시도 용도)
	public static final String EMAIL_DELAY_QUEUE = "order.completed.email.retry.queue";
	public static final String EMAIL_DELAY_ROUTING_KEY = "order.completed.email.retry";

	@Bean
	public DirectExchange orderExchange() {
		return new DirectExchange(EXCHANGE);
	}

	@Bean
	public DirectExchange myCarDlxExchange() {
		return new DirectExchange(MY_CAR_DLX);
	}

	@Bean
	public DirectExchange emailDlxExchange() {
		return new DirectExchange(EMAIL_DLX);
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

	// 출고 메일 발송 실패시 재시도
	@Bean
	public Queue emailRetryQueue() {
		return QueueBuilder.durable(EMAIL_DELAY_QUEUE)
			.withArgument("x-dead-letter-exchange", EXCHANGE)
			.withArgument("x-dead-letter-routing-key", EMAIL_ROUTING_KEY)
			.withArgument("x-message-ttl", 5000) // 5초 delay
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
			.with(MY_CAR_ROUTING_KEY);
	}

	@Bean
	public Binding emailBinding() {
		return BindingBuilder
			.bind(emailQueue())
			.to(orderExchange())
			.with(EMAIL_ROUTING_KEY);
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

	@Bean
	public Binding emailDelayBinding() {
		return BindingBuilder
			.bind(emailRetryQueue())
			.to(orderExchange())
			.with(EMAIL_DELAY_ROUTING_KEY);
	}
}
