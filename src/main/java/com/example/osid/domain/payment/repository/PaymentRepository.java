package com.example.osid.domain.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.osid.domain.payment.entity.Payments;

public interface PaymentRepository extends JpaRepository<Payments, Long> {

}
