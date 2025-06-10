package com.example.osid.domain.license.exception;

import org.springframework.http.HttpStatus;

import com.example.osid.common.exception.BaseCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LicenseErrorCode implements BaseCode {

	// 사용자가 입력한 키가 DB에 존재하지 않을 때
	KEY_NOT_FOUND(HttpStatus.BAD_REQUEST, "LICENSE_001", "유효하지 않은 라이선스 키입니다."),
	// 이미 다른 주체에게 할당되어 더 이상 사용 불가할 때
	KEY_ALREADY_ASSIGNED(HttpStatus.CONFLICT, "LICENSE_002", "이미 발급된 라이선스 키입니다."),
	// 풀에 사용 가능한 키 자체가 없을 때
	NO_AVAILABLE_KEYS(HttpStatus.SERVICE_UNAVAILABLE, "LICENSE_003", "사용 가능한 라이선스 키가 없습니다."),
	// 이미 취소(revoked)된 키를 다시 사용하려 할 때
	KEY_REVOKED(HttpStatus.BAD_REQUEST, "LICENSE_004", "취소된 라이선스 키입니다.");

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;
}
