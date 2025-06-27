package com.example.osid.domain.model.exception;

import org.springframework.http.HttpStatus;

import com.example.osid.common.exception.BaseCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ModelErrorCode implements BaseCode {
	MODEL_NOT_FOUND(HttpStatus.NOT_FOUND, "MODEL_001", "모델이 없거나 삭제된 상태입니다."),
	MODEL_ALREADY_DELETED(HttpStatus.NOT_FOUND, "MODEL_002", "이미 삭제된 모델입니다."),
	INVALID_MODEL_CATEGORY(HttpStatus.BAD_REQUEST, "MODEL_003", "유효하지 않은 모델 카테고리입니다.");

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;
}
