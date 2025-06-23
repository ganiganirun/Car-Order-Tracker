package com.example.osid.domain.payment.enums;

public enum PaymentStatus {
	READY,
	PAID, // 결제 완료
	FAILED, // 결제 실패
	CANCELLED, // 결제 취소
	REFUNDED, // 환불 완료
	MANUAL_REVIEW
}
