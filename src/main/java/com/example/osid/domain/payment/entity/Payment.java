package com.example.osid.domain.payment.entity;

import com.example.osid.common.entity.BaseEntity;
import com.example.osid.domain.payment.enums.PaymentStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "payments")
public class Payment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 이건 조금 더 고민
//    @Column(nullable = false)
//    private PaymentMethod payMentMethod; // 결제 방식

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus; // 결제 상태

    @Column(nullable = false)
    private LocalDate paidAt; // 결제일

    @Column(nullable = false)
    private Long amount; // 지불액

}
