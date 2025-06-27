package com.example.osid.domain.option.exception;

import org.springframework.http.HttpStatus;

import com.example.osid.common.exception.BaseCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OptionErrorCode implements BaseCode {
	OPTION_NOT_FOUND(HttpStatus.NOT_FOUND, "OPTION_001", "옵션이 없거나 삭제된 상태입니다."),
	OPTION_ALREADY_DELETED(HttpStatus.CONFLICT, "OPTION_002", "이미 삭제된 옵션입니다."),
	INVALID_OPTION_CATEGORY(HttpStatus.BAD_REQUEST, "OPTION_003", "유효하지 않은 옵션 카테고리입니다.");

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;
}
