package com.example.osid.event.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.osid.config.RabbitMQConfig;
import com.example.osid.event.OrderCompletedEvent;
import com.example.osid.event.dto.FailedEventResponse;
import com.example.osid.event.entity.FailedEvent;
import com.example.osid.event.exception.FailedEventException;
import com.example.osid.event.exception.FaliedEventErrorCode;
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
		FailedEvent failed = failedEventRepository.findById(failedEventId)
			.orElseThrow(() -> new FailedEventException(FaliedEventErrorCode.EVENT_NOT_FOUND));

		// 이벤트 재구성 (retryCount 초기화)
		OrderCompletedEvent retryEvent = new OrderCompletedEvent(
			failed.getOrderId(),
			null,
			0
		);

		// MQ 재전송
		rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, RabbitMQConfig.ROUTING_KEY, retryEvent);

		return "OK";
	}

}