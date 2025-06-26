package com.example.osid.domain.order.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.osid.domain.order.entity.Orders;

public interface OrderRepository extends JpaRepository<Orders, Long> {

	Optional<Orders> findByMerchantUid(String merchantUid);

	Optional<Orders> findByBodyNumber(String bodyNumber);
	
	@Query("SELECT o FROM Orders o JOIN FETCH o.orderOptions WHERE o.id = :id")
	Optional<Orders> findWithOptionsById(Long id);
}
