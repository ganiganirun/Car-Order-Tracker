package com.example.osid.event.dto;

import java.time.LocalDateTime;

import com.example.osid.event.entity.FailedEvent;
import com.example.osid.event.enums.FailedEventType;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FailedEventResponse {

	private final Long id;
	private final Long orderId;
	private final int retryCount;
	private final String errorMessage;
	private final FailedEventType failedEventType;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd-HH:mm:ss")
	private final LocalDateTime createdAt;

	public static FailedEventResponse from(FailedEvent failedEvent) {
		return new FailedEventResponse(
			failedEvent.getId(),
			failedEvent.getOrderId(),
			failedEvent.getRetryCount(),
			failedEvent.getErrorMessage(),
			failedEvent.getEventType(),
			failedEvent.getCreatedAt()
		);
	}
}
