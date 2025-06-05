package com.example.osid.domain.master.exception;

import org.springframework.http.HttpStatus;

import com.example.osid.common.exception.BaseCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MasterErrorCode implements BaseCode {
	MASTER_INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "MASTER_001", "사용자 비밀번호가 일치하지 않습니다."),
	MASTER_NOT_FOUND(HttpStatus.NOT_FOUND, "MASTER_002", "마스터를 찾을 수 없습니다.");

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;
}
