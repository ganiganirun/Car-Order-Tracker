package com.example.osid.domain.order.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

// @Getter
// @RequiredArgsConstructor
// public class OrderPaidEvent {
//
// 	private final Long orderId;
// 	private final List<Long> optionIds;
//
// 	public OrderPaidEvent(Orders orders) {
// 		this.orderId = orders.getId();
// 		this.optionIds = orders.getOrderOptions().stream()
// 			.map(option -> option.getId()).toList();
//
// 	}

@Getter
@RequiredArgsConstructor
public class OrderPaidEvent {

	private final Long orderId;

}
