package com.example.osid.domain.mycar.exception;

import org.springframework.http.HttpStatus;

import com.example.osid.common.exception.BaseCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MyCarlErrorCode implements BaseCode {
	MY_CAR_NOT_FOUND(HttpStatus.NOT_FOUND, "MC001", "존재하지 않는 차량입니다."),
	CAR_ALREADY_OWNED(HttpStatus.CONFLICT, "MC002", "이미 등록된 차량입니다");
	private final HttpStatus httpStatus;
	private final String code;
	private final String message;
}
