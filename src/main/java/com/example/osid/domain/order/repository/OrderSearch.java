package com.example.osid.domain.order.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.osid.common.entity.enums.Role;
import com.example.osid.domain.order.entity.Orders;

public interface OrderSearch {
	Page<Orders> findOrderAllForUserOrDealer(Role role, Pageable pageable, Long id);

	Page<Orders> findOrderAllForMaster(Role role, Pageable pageable, List<Long> dealerIds);
}
