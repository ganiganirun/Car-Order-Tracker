// package com.example.osid.domain.order.dto.response;
//
// import java.time.LocalDate;
// import java.util.List;
//
// import com.example.osid.domain.order.entity.Orders;
// import com.example.osid.domain.order.enums.OrderStatus;
// import com.example.osid.domain.user.entity.User;
//
// import lombok.AllArgsConstructor;
// import lombok.Builder;
// import lombok.Getter;
//
// @Getter
// @AllArgsConstructor
// @Builder
// public class OrderDetailResponse {
//
// 	private Header header;        // 상단 타임라인
// 	private OrderBlock order;     // 주문(좌측)
// 	private UserBlock user;       // 고객(우측)
// 	private PaymentBlock payment; // 결제/가격
//
// 	/* ------------------ 정적 팩터리 ------------------ */
// 	public static OrderDetailResponse of(Orders orders,
// 		List<ProcessStep> steps,
// 		String payStatus) {
//
// 		return OrderDetailResponse.builder()
// 			.header(new Header(orders.getId(), steps))
// 			.order(OrderBlock.from(orders))
// 			.user(UserBlock.from(orders.getUser()))
// 			.payment(PaymentBlock.from(orders, payStatus))
// 			.build();
// 	}
//
// 	/* ------------ 내부 static 클래스들 ------------ */
//
// 	@Getter
// 	@AllArgsConstructor
// 	public static class Header {
// 		private Long id;
// 		private List<ProcessStep> steps;   // 타임라인
// 	}
//
// 	@Getter
// 	@AllArgsConstructor
// 	public static class ProcessStep {
// 		private String name;
// 		private LocalDate date;
// 	}
//
// 	@Getter
// 	@AllArgsConstructor
// 	public static class OrderBlock {
// 		private String orderNumber;
// 		private String dealer;
// 		private String model;
// 		private List<String> options;
// 		private LocalDate orderDate;
// 		private OrderStatus orderStatus;
// 		private LocalDate expectedDelivery;
// 		private LocalDate actualDelivery;
//
// 		public static OrderBlock from(Orders orders) {
// 			return new OrderBlock(
// 				orders.getBodyNumber(),
// 				orders.getDealer().getName(),
// 				orders.getModel().getName(),
// 				orders.getOrderOptions().stream()
// 					.map(order -> order.getOption().getName())
// 					.toList(),
// 				orders.getCreatedAt().toLocalDate(),
// 				orders.getOrderStatus(),
// 				orders.getExpectedDeliveryAt(),
// 				orders.getActualDeliveryAt()
// 			);
// 		}
// 	}
//
// 	@Getter
// 	@AllArgsConstructor
// 	public static class UserBlock {
// 		private String name;
// 		private String phone;
// 		private String email;
//
// 		public static UserBlock from(User user) {
// 			return new UserBlock(user.getName(), user.getPhoneNumber(), user.getEmail());
// 		}
// 	}
//
// 	@Getter
// 	@AllArgsConstructor
// 	public static class PaymentBlock {
// 		private String payStatus;
// 		private String gateway;
// 		private Long totalPrice;
//
// 		public static PaymentBlock from(Orders orders, String payStatus) {
// 			return new PaymentBlock(
// 				payStatus,
// 				orders.getTotalPrice()
// 			);
// 		}
// 	}
// }
