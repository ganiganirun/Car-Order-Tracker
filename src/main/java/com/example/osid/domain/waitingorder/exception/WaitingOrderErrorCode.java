package com.example.osid.domain.waitingorder.exception;

import org.springframework.http.HttpStatus;

import com.example.osid.common.exception.BaseCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum WaitingOrderErrorCode implements BaseCode {

	WAITING_ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "WAITING_001", "해당 대기열을 찾을 수 없습니다.");

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;
}
