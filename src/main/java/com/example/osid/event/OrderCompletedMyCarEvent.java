package com.example.osid.event;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderCompletedMyCarEvent implements Serializable {

	// orderId, (실패했을 경우)에러 메세지, 재시도 횟수
	private Long orderId;
	private String errorMessage;
	private int retryCount;

	public OrderCompletedMyCarEvent(Long orderId) {
		this.orderId = orderId;
	}

}