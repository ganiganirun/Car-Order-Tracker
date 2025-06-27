package com.example.osid.domain.order.entity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.example.osid.common.entity.BaseEntity;
import com.example.osid.domain.dealer.entity.Dealer;
import com.example.osid.domain.model.entity.Model;
import com.example.osid.domain.order.enums.OrderStatus;
import com.example.osid.domain.payment.entity.Payments;
import com.example.osid.domain.user.entity.User;
import com.example.osid.domain.waitingorder.entity.WaitingOrders;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Setter
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
public class Orders extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = true)
	private String address; // 배송지

	private String merchantUid;  // 주문 고유 식별자 (결제용)

	@Column(nullable = false, unique = true)
	private String bodyNumber; // 차 고유 번호

	@Column(nullable = false)
	private Long totalPrice; // 총가격

	@Enumerated(EnumType.STRING)
	private OrderStatus orderStatus; // 주문 상태

	@Column(name = "expected_delivery_date")
	private LocalDateTime expectedDeliveryAt; // 예상 출고일

	@Column(name = "actual_delivery_date")
	private LocalDateTime actualDeliveryAt; // 실제 출고일

	@Column(name = "received_at")
	private LocalDateTime receivedAt; // 차량 수령일

	@ManyToOne
	private User user; // 유저 정보

	@ManyToOne
	private Dealer dealer; // 딜러정보

	@ManyToOne
	private Model model; // 차량 모델 정보

	@OneToMany(mappedBy = "orders", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference
	private List<OrderOption> orderOptions; // 옵션 정보

	// 1:1 WaitingOrders (양방향)
	@OneToOne(
		mappedBy = "orders",
		cascade = CascadeType.ALL,
		orphanRemoval = true,
		fetch = FetchType.LAZY
	)
	private WaitingOrders waitingOrders;

	@OneToOne(
		cascade = CascadeType.MERGE,
		orphanRemoval = true,
		fetch = FetchType.LAZY
	)
	@JoinColumn(name = "payment_id")
	private Payments payments;

	// 주문 저장 직전에 자동 생성
	@PrePersist
	public void prePersist() {
		if (this.merchantUid == null) {
			this.merchantUid = "order_" + UUID.randomUUID().toString().substring(0, 8);
		}
		if (this.bodyNumber == null) {
			this.bodyNumber = "car_" + UUID.randomUUID().toString();
		}
	}

}
