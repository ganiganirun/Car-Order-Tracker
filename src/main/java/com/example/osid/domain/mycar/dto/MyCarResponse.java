package com.example.osid.domain.mycar.dto;

import java.util.List;
import java.util.stream.Collectors;

import com.example.osid.domain.order.entity.Orders;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MyCarResponse {

	private final String bodyNumber;

	private final String modelName;

	private final List<String> optionName;

	public MyCarResponse(Orders orders) {
		this.bodyNumber = orders.getBodyNumber();
		this.modelName = orders.getModel().getName();
		this.optionName = orders.getOrderOptions()
			.stream()
			.map(orderOption -> orderOption.getOption().getName())
			.collect(Collectors.toList());
	}
}
