package com.example.osid.event.listener.dlq;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.example.osid.config.RabbitMQConfig;
import com.example.osid.event.OrderCompletedMyCarEvent;
import com.example.osid.event.entity.FailedEvent;
import com.example.osid.event.repository.FailedEventRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "mq.enabled", havingValue = "true", matchIfMissing = false)
public class MyCarDlqListener {

	private final FailedEventRepository failedEventRepository;

	@RabbitListener(queues = RabbitMQConfig.MY_CAR_DLQ)
	public void handleDlq(OrderCompletedMyCarEvent event) {
		log.warn("MY_CAR_DLQ 메시지 수신: orderId={}, retryCount={}, error={}",
			event.getOrderId(), event.getRetryCount(), event.getErrorMessage());

		// DB 저장
		FailedEvent failedEvent = new FailedEvent(event);
		failedEventRepository.save(failedEvent);
	}

}
