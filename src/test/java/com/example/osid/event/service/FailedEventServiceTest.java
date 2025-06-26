package com.example.osid.event.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.example.osid.config.RabbitMQConfig;
import com.example.osid.event.OrderCompletedEmailEvent;
import com.example.osid.event.dto.FailedEventResponse;
import com.example.osid.event.entity.FailedEvent;
import com.example.osid.event.enums.FailedEventType;
import com.example.osid.event.exception.FailedEventException;
import com.example.osid.event.repository.FailedEventRepository;

@ExtendWith(MockitoExtension.class)
class FailedEventServiceTest {

	@Mock
	private FailedEventRepository failedEventRepository;

	@Mock
	private RabbitTemplate rabbitTemplate;

	@InjectMocks
	private FailedEventService failedEventService;
	private FailedEvent failedEvent;

	@BeforeEach
	void setUp() {

		failedEvent = new FailedEvent(1L, 0, "ErrorMessage", FailedEventType.MY_CAR);
	}

	@Test
	@DisplayName("실패 이벤트 전체 조회 테스트")
	void findAllFailedEvent() {

		PageRequest pageable = PageRequest.of(0, 10);
		Page<FailedEvent> page = new PageImpl<>(Collections.singletonList(failedEvent));
		given(failedEventRepository.findAll(pageable)).willReturn(page);

		Page<FailedEventResponse> result = failedEventService.findAllFailedEvent(pageable);

		assertEquals(1, result.getTotalElements());
		assertEquals(failedEvent.getOrderId(), result.getContent().get(0).getOrderId());
	}

	@Test
	@DisplayName("실패 이벤트 재전송 성공 테스트")
	void retryFailedEvent() {

		Long failedEventId = 1L;

		given(failedEventRepository.findById(failedEventId)).willReturn(Optional.of(failedEvent));

		String result = failedEventService.retryFailedEvent(failedEventId);

		verify(rabbitTemplate, times(1))
			.convertAndSend(eq(RabbitMQConfig.EXCHANGE), eq(RabbitMQConfig.MY_CAR_ROUTING_KEY),
				any(OrderCompletedEmailEvent.class));

		assertEquals("OK", result);
	}

	@Test
	@DisplayName("실패 이벤트 재전송 시 존재하지 않는 경우 예외 발생")
	void retryFailedEvent_NotFound() {

		Long failedEventId = 2L;
		given(failedEventRepository.findById(failedEventId)).willReturn(Optional.empty());

		assertThrows(FailedEventException.class, () -> failedEventService.retryFailedEvent(failedEventId));
	}
}