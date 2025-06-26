package com.example.osid.domain.order.exception;

import org.springframework.http.HttpStatus;

import com.example.osid.common.exception.BaseCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderErrorCode implements BaseCode {

	ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "ORDER_001", "해당 주문을 찾을 수 없습니다."),
	ORDER_ACCESS_DENIED(HttpStatus.FORBIDDEN, "ORDER_002", "해당 주문에 대한 접근 권한이 없습니다."),
	ORDER_CANCELLATION_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "ORDER_003", "주문은 현재 상태에서 취소할 수 없습니다.");

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;
}
