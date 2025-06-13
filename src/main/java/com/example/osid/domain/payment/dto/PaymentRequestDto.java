package com.example.osid.domain.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class PaymentRequestDto {

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Paid {

		private String impUid; // 아임포트 결제 고유 번호

		private String merchantUid; // 주문 고유 번호 (Order.merchantUid)

		private Long amount; // 결제 금액

		private Long userId;

	}

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Cancel {

		private String merchantUid; // 주문 고유 번호 (Order.merchantUid)

		private int refundAmount; // 결제 금액

	}

}
