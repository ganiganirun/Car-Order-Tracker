package com.example.osid.domain.dealer.exception;

import org.springframework.http.HttpStatus;

import com.example.osid.common.exception.BaseCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DealerErrorCode implements BaseCode {
	DEALER_INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "DEALER_001", "사용자 비밀번호가 일치하지 않습니다."),
	DEALER_NOT_FOUND(HttpStatus.NOT_FOUND, "DEALER_002", "딜러를 찾을 수 없습니다."),
	DEALER_NOT_BELONG_TO_MASTER(HttpStatus.FORBIDDEN, "DEALER_003", "해당 딜러는 이 마스터 소속이 아닙니다."),
	INVALID_ROLE(HttpStatus.BAD_REQUEST, "DEALER_004", "유효하지 않은 role 값입니다.");

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;
}
