package com.example.osid.domain.history.exception;

import org.springframework.http.HttpStatus;

import com.example.osid.common.exception.BaseCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum HistoryErrorCode implements BaseCode {

	HISTORY_NOT_FOUND(HttpStatus.NOT_FOUND, "HISTORY_001", "해당 주문을 찾을 수 없습니다.");

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;

}
