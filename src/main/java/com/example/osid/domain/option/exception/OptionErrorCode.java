package com.example.osid.domain.option.exception;

import org.springframework.http.HttpStatus;

import com.example.osid.common.exception.BaseCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OptionErrorCode implements BaseCode {
	OPTION_NOT_FOUND(HttpStatus.NOT_FOUND, "O001", "옵션이 없거나 삭제된 상태입니다.");

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;
}
