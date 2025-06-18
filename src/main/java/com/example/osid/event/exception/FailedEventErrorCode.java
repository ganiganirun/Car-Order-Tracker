package com.example.osid.event.exception;

import org.springframework.http.HttpStatus;

import com.example.osid.common.exception.BaseCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FailedEventErrorCode implements BaseCode {
	EVENT_NOT_FOUND(HttpStatus.NOT_FOUND, "EVENT_001", "이벤트를 찾을 수 없습니다."),
	EVENT_TYPE_NOT_EXIST(HttpStatus.NOT_FOUND, "EVENT_002", "존재하지 않는 이벤트 타입입니다.");

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;
}
