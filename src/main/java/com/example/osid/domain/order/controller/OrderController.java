package com.example.osid.domain.order.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.osid.domain.order.service.OrderService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/order")
public class OrderController {

	private final OrderService orderService;

	// @PostMapping
	// public ResponseEntity<CommonResponse> createOrder(
	// 	@RequestBody OrderRequestDto.Add requestDto
	//
	// ) {
	//
	// 	Long dealerId = 0L;
	//
	// 	orderService.createOrder(dealerId, requestDto);
	// }

}
