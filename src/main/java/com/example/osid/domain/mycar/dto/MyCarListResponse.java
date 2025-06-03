package com.example.osid.domain.mycar.dto;

import com.example.osid.domain.mycar.entity.Mycar;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MyCarListResponse {

	private final String myCarName;

	public static MyCarListResponse from(Mycar mycar) {
		return new MyCarListResponse(
			mycar.getOrders().getModel().getName()
		);
	}
}
