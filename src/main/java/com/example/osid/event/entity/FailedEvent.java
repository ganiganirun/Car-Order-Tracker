package com.example.osid.event.entity;

import com.example.osid.common.entity.BaseEntity;
import com.example.osid.event.OrderCompletedEvent;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

	public FailedEvent(OrderCompletedEvent event) {
		this.orderId = event.getOrderId();
		this.retryCount = event.getRetryCount();
		this.errorMessage = event.getErrorMessage();
	}

}
