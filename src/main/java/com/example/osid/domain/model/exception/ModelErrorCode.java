package com.example.osid.domain.model.exception;

import org.springframework.http.HttpStatus;

import com.example.osid.common.exception.BaseCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ModelErrorCode implements BaseCode {
	MODEL_NOT_FOUND(HttpStatus.NOT_FOUND, "M001", "모델이 없거나 삭제된 상태입니다.");

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;
}
