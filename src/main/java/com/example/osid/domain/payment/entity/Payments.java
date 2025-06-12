package com.example.osid.domain.payment.entity;

import java.time.LocalDate;

import com.example.osid.common.entity.BaseEntity;
import com.example.osid.domain.payment.enums.PaymentStatus;
import com.example.osid.domain.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "payments")
public class Payments extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String impUid;

	@Enumerated(EnumType.STRING)
	private PaymentStatus paymentStatus; // 결제 상태

	@Column(nullable = false)
	private LocalDate paidAt; // 결제일

	@Column(nullable = false)
	private Long amount; // 지불액

	@ManyToOne(fetch = FetchType.LAZY)
	private User user;

	public Payments(User user, Long amount, String impUid, PaymentStatus paymentStatus,
		LocalDate paidAt) {
		this.user = user;
		this.amount = amount;
		this.impUid = impUid;
		this.paymentStatus = paymentStatus;
		this.paidAt = paidAt;
	}

	public void changePaymentBySuccess(PaymentStatus paymentStatus) {
		this.paymentStatus = paymentStatus;
	}
}
