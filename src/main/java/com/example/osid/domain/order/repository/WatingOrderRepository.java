package com.example.osid.domain.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.osid.domain.order.entity.WaitingOrders;

public interface WatingOrderRepository extends JpaRepository<WaitingOrders, Long> {
	
}
