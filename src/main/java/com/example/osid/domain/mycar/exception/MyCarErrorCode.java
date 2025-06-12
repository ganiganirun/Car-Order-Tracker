package com.example.osid.domain.mycar.exception;

import org.springframework.http.HttpStatus;

import com.example.osid.common.exception.BaseCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MyCarErrorCode implements BaseCode {
	MY_CAR_NOT_FOUND(HttpStatus.NOT_FOUND, "MY_CAR_C001", "존재하지 않는 차량입니다."),
	CAR_ALREADY_OWNED(HttpStatus.CONFLICT, "MY_CAR_002", "이미 등록된 차량입니다"),
	MY_CAR_NOT_OWED(HttpStatus.FORBIDDEN, "MY_CAR_003", "접근 권한이 없습니다."),
	ORDER_NOT_COMPLETED(HttpStatus.CONFLICT, "MY_CAR_004", "생산이 완료되지 않은 차량입니다.");
	private final HttpStatus httpStatus;
	private final String code;
	private final String message;
}
