package com.example.osid.event.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.example.osid.config.RabbitMQConfig;
import com.example.osid.domain.email.service.EmailService;
import com.example.osid.event.OrderCompletedEmailEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "mq.enabled", havingValue = "true", matchIfMissing = false)
public class OrderCompletedEmailListener {

	// 최대 재시도 횟수
	private static final int MAX_RETRY = 3;

	private final EmailService emailService;
	private final RabbitTemplate rabbitTemplate;

	@RabbitListener(queues = RabbitMQConfig.EMAIL_QUEUE)
	public void handleOrderCompletedEmail(OrderCompletedEmailEvent event) {
		int retryCount = event.getRetryCount();
		log.info("이메일 이벤트 수신: orderId={}, retryCount={}", event.getOrderId(), retryCount);

		try {
			emailService.sendOrderCompletedEmail(event.getOrderId());
			log.info("이메일 전송 성공: orderId={}", event.getOrderId());

		} catch (Exception e) {
			// 재시도 가능한 예외
			int nextRetry = retryCount + 1;
			if (nextRetry > MAX_RETRY) {
				event.setErrorMessage(e.getMessage());
				event.setRetryCount(nextRetry);
				sendToDlq(event);
			} else {
				event.setRetryCount(nextRetry);
				sendToDelayQueue(event);
			}
		}
	}

	// 실패한 이메일 즉시 재시도
	// private void resendToQueue(OrderCompletedEmailEvent event) {
	// 	log.info("이메일 재시도 메시지 전송: orderId={}, retryCount={}", event.getOrderId(), event.getRetryCount());
	// 	rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, RabbitMQConfig.EMAIL_ROUTING_KEY, event);
	// }

	// 실패한 이메일 retry queue로 전송
	private void sendToDelayQueue(OrderCompletedEmailEvent event) {
		log.warn("이메일 재시도 메시지 전송: orderId={}, retryCount={}", event.getOrderId(), event.getRetryCount());
		rabbitTemplate.convertAndSend(
			RabbitMQConfig.EXCHANGE,  // 지연 큐 전용 익스체인지
			RabbitMQConfig.EMAIL_DELAY_ROUTING_KEY,
			event
		);
	}

	private void sendToDlq(OrderCompletedEmailEvent event) {
		log.error("이메일 DLQ 전송: orderId={}, error={}", event.getOrderId(), event.getErrorMessage());
		OrderCompletedEmailEvent toSend = new OrderCompletedEmailEvent(
			event.getOrderId(),
			event.getErrorMessage(),
			event.getRetryCount()
		);
		rabbitTemplate.convertAndSend(RabbitMQConfig.EMAIL_DLX, RabbitMQConfig.EMAIL_DLQ_ROUTING_KEY, toSend);
	}
}
