package com.example.osid.domain.payment.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.osid.domain.payment.entity.Payments;
import com.example.osid.domain.payment.enums.PaymentStatus;

public interface PaymentRepository extends JpaRepository<Payments, Long> {

	Optional<Payments> findByImpUid(String impUid);

	boolean existsByImpUidAndPaymentStatus(String impUid, PaymentStatus paymentStatus);

	Page<Payments> findByPaymentStatus(Pageable pageable, PaymentStatus paymentStatus);

}
