package com.example.osid.event.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.osid.config.RabbitMQConfig;
import com.example.osid.event.OrderCompletedEmailEvent;
import com.example.osid.event.OrderCompletedMyCarEvent;
import com.example.osid.event.dto.FailedEventResponse;
import com.example.osid.event.entity.FailedEvent;
import com.example.osid.event.exception.FailedEventErrorCode;
import com.example.osid.event.exception.FailedEventException;
import com.example.osid.event.repository.FailedEventRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class FailedEventService {

	private final FailedEventRepository failedEventRepository;
	private final RabbitTemplate rabbitTemplate;

	@Transactional(readOnly = true)
	@PreAuthorize("hasRole('MASTER')")
	public Page<FailedEventResponse> findAllFailedEvent(Pageable pageable) {

		Page<FailedEvent> failedEvents = failedEventRepository.findAll(pageable);

		return failedEvents.map(FailedEventResponse::from);
	}

	@Transactional
	@PreAuthorize("hasRole('MASTER')")
	public String retryFailedEvent(Long failedEventId) {
		FailedEvent failedEvent = failedEventRepository.findById(failedEventId)
			.orElseThrow(() -> new FailedEventException(FailedEventErrorCode.EVENT_NOT_FOUND));

		// 이벤트 재구성 (retryCount 초기화)
		switch (failedEvent.getEventType()) {
			case MY_CAR -> resendMyCarEvent(failedEvent);
			case EMAIL -> resendEmailEvent(failedEvent);
			default -> throw new FailedEventException(FailedEventErrorCode.EVENT_TYPE_NOT_EXIST);
		}

		// 재처리 성공 시 실패 이벤트 삭제
		failedEventRepository.delete(failedEvent);
		return "OK";

	}

	private void resendMyCarEvent(FailedEvent failedEvent) {
		OrderCompletedMyCarEvent retryEvent = new OrderCompletedMyCarEvent(
			failedEvent.getOrderId(),
			null,
			0
		);
		rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, RabbitMQConfig.MY_CAR_ROUTING_KEY, retryEvent);
		log.info("MyCar 재처리 전송: orderId={}", failedEvent.getOrderId());
	}

	private void resendEmailEvent(FailedEvent failedEvent) {
		OrderCompletedEmailEvent retryEvent = new OrderCompletedEmailEvent(
			failedEvent.getOrderId(),
			null,
			0
		);
		rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, RabbitMQConfig.EMAIL_ROUTING_KEY, retryEvent);
		log.info("Email 재처리 전송: orderId={}", failedEvent.getOrderId());
	}

}