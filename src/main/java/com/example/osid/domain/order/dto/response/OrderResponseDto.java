package com.example.osid.domain.order.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.example.osid.domain.order.enums.OrderStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class OrderResponseDto {

	@Builder
	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Add {

		private Long id;

		private Long userId;

		private String dealerName;

		private List<String> orderOptions;

		private String model;

		private String address;

		private Long totalPrice;

		private String merchantUid;

		private OrderStatus orderStatus; // 주문 상태

		private LocalDateTime createdAt;

	}

	@Builder
	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Update {

		private Long id;

		private String userName;

		private String dealerName;

		private List<String> orderOptions;

		private String model;

		private String address;

		private Long totalPrice;

		private OrderStatus orderStatus; // 주문 상태

		private LocalDateTime expectedDeliveryAt; // 예상 출고일

		private LocalDateTime actualDeliveryAt; // 실제 출고일

		private LocalDateTime createdAt;

	}

	@Builder
	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class FindAll {

		private Long id;

		private String userName;

		private String dealerName;

		private String model;

		// private Long totalPrice;
		//
		// private OrderStatus orderStatus; // 주문 상태
		//
		// private LocalDate expectedDeliveryAt; // 예상 출고일
		//
		// private LocalDate actualDeliveryAt; // 실제 출고일

	}
}
