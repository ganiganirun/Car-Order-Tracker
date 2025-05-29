package com.example.osid.domain.order.entity;

import java.time.LocalDate;

import com.example.osid.common.entity.BaseEntity;
import com.example.osid.domain.dealer.entity.Dealer;
import com.example.osid.domain.model.entity.Model;
import com.example.osid.domain.order.enums.OrderStatus;
import com.example.osid.domain.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
public class Order extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String address; // 배송지

	@Column(nullable = false, unique = true)
	private String bodyNumber; // 차 고유 번호

	@Column(nullable = false)
	private Long totalPrice; // 총가격

	@Enumerated(EnumType.STRING)
	private OrderStatus orderStatus; // 주문 상태

	@Column(name = "expected_delivery_date")
	private LocalDate expectedDeliveryAt; // 예상 출고일

	@Column(name = "actual_delivery_date")
	private LocalDate actualDeliveryAt; // 실제 출고일

	@ManyToOne
	private User user; // 유저 정보

	@ManyToOne
	private Dealer dealer; // 딜러정보

	@ManyToOne
	private Model model; // 차량 모델 정보

}
