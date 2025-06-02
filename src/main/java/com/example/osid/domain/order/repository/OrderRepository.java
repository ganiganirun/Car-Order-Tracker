package com.example.osid.domain.order.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.osid.domain.order.entity.Orders;

public interface OrderRepository extends JpaRepository<Orders, Long> {

	Optional<Orders> findByMerchantUid(String merchantUid);
}
