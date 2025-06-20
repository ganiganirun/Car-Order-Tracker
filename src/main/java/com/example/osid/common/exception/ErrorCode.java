package com.example.osid.common.exception;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode implements BaseCode {

	INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "COMMON_001", "유효하지 않은 입력 값입니다."),
	METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "COMMON_002", "허용되지 않은 요청 방식입니다."),
	ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, "COMMON_003", "요청한 엔티티를 찾을 수 없습니다."),
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON_004", "내부 서버 오류가 발생했습니다."),
	INVALID_TYPE_VALUE(HttpStatus.BAD_REQUEST, "COMMON_005", "유효하지 않은 타입의 값입니다."),
	USER_NOT_FOUND(HttpStatus.NOT_FOUND, "COMMON_006", "사용자가 없습니다."),
	INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "COMMON_007", "유효하지 않는 토큰입니다."),
	FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON_008", "접근 권한이 없습니다."),
	EMAIL_ALREADY_EXISTS(HttpStatus.UNAUTHORIZED, "COMMON_009", "이미 가입된 이메일 입니다."),
	AUTHORITY_NOT_FOUND(HttpStatus.FORBIDDEN, "COMMON_010", "권한 정보가 없습니다."),
	INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "COMMON_011", "사용자 비밀번호가 일치하지 않습니다."),
	INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "COMMON_012", "리프레시 토큰이 유효하지 않습니다."),
	REFRESH_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "COMMON_013", "리프레시 토큰이 존재하지 않습니다."),
	REFRESH_TOKEN_MISMATCH(HttpStatus.FORBIDDEN, "COMMON_014", "리프레시 토큰이 일치하지 않습니다.");

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;

}
