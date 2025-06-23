package com.example.osid.domain.payment.exception;

import org.springframework.http.HttpStatus;

import com.example.osid.common.exception.BaseCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PaymentErrorCode implements BaseCode {
	PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "PAYMENT_001", "해당 결제를 찾을 수 없습니다."),
	PAYMENT_AMOUNT_MISMATCH(HttpStatus.BAD_REQUEST, "PAYMENT_002", "결제 금액이 일치하지 않습니다."),
	PAYMENT_REFUND_TOO_LARGE(HttpStatus.BAD_REQUEST, "PAYMENT_003", "환불 금액이 결제 금액을 초과합니다.");

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;
}
