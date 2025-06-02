package com.example.osid.common.exception;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode implements BaseCode {

	INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "C001", "유효하지 않은 입력 값입니다."),
	METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "C002", "허용되지 않은 요청 방식입니다."),
	ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, "C003", "요청한 엔티티를 찾을 수 없습니다."),
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C004", "내부 서버 오류가 발생했습니다."),
	INVALID_TYPE_VALUE(HttpStatus.BAD_REQUEST, "C005", "유효하지 않은 타입의 값입니다."),
	USER_NOT_FOUND(HttpStatus.NOT_FOUND, "C006", "사용자가 없습니다."),
	INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "C007", "유효하지 않는 토큰입니다."),
	FORBIDDEN(HttpStatus.FORBIDDEN, "C008", "접근 권한이 없습니다.");

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;

}
