package com.example.osid.domain.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.osid.domain.order.entity.OrderOption;

public interface OrderOptionRepository extends JpaRepository<OrderOption, Long> {

}
