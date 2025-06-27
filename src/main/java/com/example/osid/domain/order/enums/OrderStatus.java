package com.example.osid.domain.order.enums;

public enum OrderStatus {
	ORDERED, // 주문 접수
	CANCELLED, // 주문 취소
	REFUNDED, // 환불 완료
	COMPLETED, // 주문 완료
	FAILED, // 주문 처리 실패
	IN_PRODUCTION,  // 생산중
	SHIPPED,         // 출고완료
	RECEIVED        // 수령 완료
}
