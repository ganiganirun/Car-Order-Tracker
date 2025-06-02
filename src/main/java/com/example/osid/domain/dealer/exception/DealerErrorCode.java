package com.example.osid.domain.dealer.exception;

import org.springframework.http.HttpStatus;

import com.example.osid.common.exception.BaseCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DealerErrorCode implements BaseCode {
	EMAIL_ALREADY_EXISTS(HttpStatus.UNAUTHORIZED, "D001", "이미 가입된 이메일 입니다."),
	DEALER_INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "D002", "사용자 비밀번호가 일치하지 않습니다."),
	DEALER_NOT_FOUND(HttpStatus.NOT_FOUND, "D003", "딜러를 찾을 수 없습니다.");

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;
}
