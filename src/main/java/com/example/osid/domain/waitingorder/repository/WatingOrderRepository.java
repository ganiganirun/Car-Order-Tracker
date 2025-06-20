package com.example.osid.domain.waitingorder.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.osid.domain.waitingorder.entity.WaitingOrders;

public interface WatingOrderRepository extends JpaRepository<WaitingOrders, Long> {

}
