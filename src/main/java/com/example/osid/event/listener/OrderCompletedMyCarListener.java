package com.example.osid.event.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.example.osid.config.RabbitMQConfig;
import com.example.osid.domain.mycar.exception.MyCarException;
import com.example.osid.domain.mycar.service.MyCarService;
import com.example.osid.domain.order.exception.OrderException;
import com.example.osid.event.OrderCompletedEmailEvent;
import com.example.osid.event.OrderCompletedMyCarEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "mq.enabled", havingValue = "true", matchIfMissing = false)
public class OrderCompletedMyCarListener {

	// 최대 재시도 횟수
	private static final int MAX_RETRY = 3;

	private final MyCarService myCarService;
	private final RabbitTemplate rabbitTemplate;

	// myCar 생성 -> email 발행
	@RabbitListener(queues = RabbitMQConfig.MY_CAR_QUEUE)
	public void handleOrderCompleted(OrderCompletedMyCarEvent event) {
		int retryCount = event.getRetryCount();
		log.info("myCar 생성 이벤트 수신: orderId={}, retryCount={}", event.getOrderId(), retryCount);

		try {
			myCarService.saveMyCar(event.getOrderId());
			log.info("MyCar 저장 성공, orderId={}", event.getOrderId());
			publishEmailEvent(event.getOrderId());

		} catch (MyCarException | OrderException e) {
			// 비즈니스 로직 예외는 바로 DLQ 전송 (MyCarException, OrderException)
			log.warn("재처리 불가능한 예외: {} -> DLQ 이동", e.getMessage());
			event.setErrorMessage(e.getMessage());
			event.setRetryCount(event.getRetryCount() + 1);
			sendToDlq(event);
		} catch (Exception e) {
			// 재시도 가능한 일반 예외(3회 실패시 DLQ 이동)
			int nextRetry = event.getRetryCount() + 1;
			if (nextRetry > MAX_RETRY) {
				event.setErrorMessage(e.getMessage());
				event.setRetryCount(nextRetry);
				sendToDlq(event);  // DLQ 전송용 메서드
			} else {
				event.setRetryCount(nextRetry);
				resendToQueue(event); // 재시도
			}
		}
	}

	private void resendToQueue(OrderCompletedMyCarEvent event) {
		log.info("재시도 메시지 전송: orderId={}, retryCount={}", event.getOrderId(), event.getRetryCount());
		rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, RabbitMQConfig.MY_CAR_ROUTING_KEY, event);
	}

	private void sendToDlq(OrderCompletedMyCarEvent event) {
		log.error("DLQ 전송 : orderId={}, error={}", event.getOrderId(), event.getErrorMessage());
		OrderCompletedMyCarEvent toSend = new OrderCompletedMyCarEvent(
			event.getOrderId(),
			event.getErrorMessage(),
			event.getRetryCount()
		);
		rabbitTemplate.convertAndSend(RabbitMQConfig.MY_CAR_DLX, RabbitMQConfig.MY_CAR_DLQ_ROUTING_KEY, toSend);
	}

	private void publishEmailEvent(Long orderId) {
		OrderCompletedEmailEvent emailEvent = new OrderCompletedEmailEvent(orderId);
		rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, RabbitMQConfig.EMAIL_ROUTING_KEY, emailEvent);
		log.info("이메일 이벤트 발행 : orderId={}", orderId);
	}
}
