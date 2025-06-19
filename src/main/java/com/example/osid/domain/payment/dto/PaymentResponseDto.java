package com.example.osid.domain.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class PaymentResponseDto {

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ManualReview {

		private String merchantUid; // 주문 고유 번호 (Order.merchantUid)

		private Long amount; // 결제 금액

		private Long userId;

	}
}
