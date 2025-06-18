package com.example.osid.event.entity;

import com.example.osid.common.entity.BaseEntity;
import com.example.osid.event.OrderCompletedEmailEvent;
import com.example.osid.event.OrderCompletedMyCarEvent;
import com.example.osid.event.enums.FailedEventType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "failed_event")
public class FailedEvent extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long orderId;
	private int retryCount;
	@Column(name = "error_message", length = 5000)
	private String errorMessage;

	@Enumerated(EnumType.STRING)
	private FailedEventType eventType;

	// myCar failedEvent
	public FailedEvent(OrderCompletedMyCarEvent event) {
		this.orderId = event.getOrderId();
		this.retryCount = event.getRetryCount();
		this.errorMessage = event.getErrorMessage();
		this.eventType = FailedEventType.MY_CAR;
	}

	// 이메일 failedEvent
	public FailedEvent(OrderCompletedEmailEvent event) {
		this.orderId = event.getOrderId();
		this.retryCount = event.getRetryCount();
		this.errorMessage = event.getErrorMessage();
		this.eventType = FailedEventType.EMAIL;
	}

	public FailedEvent(Long orderId, int retryCount, String errorMessage, FailedEventType failedEventType) {
		this.orderId = orderId;
		this.retryCount = retryCount;
		this.errorMessage = errorMessage;
		this.eventType = failedEventType;
	}
}
