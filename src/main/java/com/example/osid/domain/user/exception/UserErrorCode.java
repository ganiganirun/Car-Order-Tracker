package com.example.osid.domain.user.exception;

import org.springframework.http.HttpStatus;

import com.example.osid.common.exception.BaseCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements BaseCode {
	EMAIL_ALREADY_EXISTS(HttpStatus.UNAUTHORIZED, "U001", "이미 가입된 이메일 입니다."),
	USER_INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "U002", "사용자 비밀번호가 일치하지 않습니다."),
	USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U003", "사용자를 찾을 수 없습니다.");

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;
}
