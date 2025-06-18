package com.example.osid.event;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.example.osid.config.RabbitMQConfig;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrderEventPublisher {

	private final RabbitTemplate rabbitTemplate;

	// myCar 생성
	public void publishOrderCompletedMyCar(OrderCompletedMyCarEvent event) {
		// 트랜잭션 완료 후 실행
		if (TransactionSynchronizationManager.isActualTransactionActive()) {
			TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
				@Override
				public void afterCommit() {
					rabbitTemplate.convertAndSend(
						RabbitMQConfig.EXCHANGE,
						RabbitMQConfig.MY_CAR_ROUTING_KEY,
						event
					);
				}
			});
		}
	}

}
