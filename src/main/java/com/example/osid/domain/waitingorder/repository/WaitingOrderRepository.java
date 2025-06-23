package com.example.osid.domain.waitingorder.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.osid.domain.order.entity.Orders;
import com.example.osid.domain.waitingorder.entity.WaitingOrders;

public interface WaitingOrderRepository extends JpaRepository<WaitingOrders, Long> {

	// Page<WaitingOrders> findAllByWaitingStatus(WaitingStatus status, Pageable pageable);

	Optional<WaitingOrders> findByOrders(Orders orders);

}
