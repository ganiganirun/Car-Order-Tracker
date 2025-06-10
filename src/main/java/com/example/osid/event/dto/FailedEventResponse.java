package com.example.osid.event.dto;

import java.time.LocalDateTime;

import com.example.osid.event.entity.FailedEvent;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FailedEventResponse {

	private final Long orderId;
	private final int retryCount;
	private final String errorMessage;
	private final LocalDateTime createdAt;
	private final LocalDateTime updatedAt;

	public static FailedEventResponse from(FailedEvent failedEvent) {
		return new FailedEventResponse(
			failedEvent.getOrderId(),
			failedEvent.getRetryCount(),
			failedEvent.getErrorMessage(),
			failedEvent.getCreatedAt(),
			failedEvent.getUpdatedAt()
		);
	}
}
