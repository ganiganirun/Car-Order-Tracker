package com.example.osid.domain.order.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.osid.common.entity.enums.Role;
import com.example.osid.common.response.CommonResponse;
import com.example.osid.domain.order.dto.request.OrderRequestDto;
import com.example.osid.domain.order.dto.response.OrderResponseDto;
import com.example.osid.domain.order.service.OrderService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class OrderController {

	private final OrderService orderService;

	// 주문 생성
	@PostMapping("/api/dealers/order")
	public ResponseEntity<CommonResponse> createOrder(
		@RequestBody OrderRequestDto.Add requestDto
	) {
		Long dealerId = 1L;

		OrderResponseDto.Add order = orderService.createOrder(dealerId, requestDto);

		return new ResponseEntity<>(CommonResponse.created(order), HttpStatus.CREATED);

	}

	// 주문 수정
	@PatchMapping("/api/dealers/order/{orderId}")
	public ResponseEntity<CommonResponse> updateOrder(
		@PathVariable Long orderId,
		@RequestBody OrderRequestDto.Update requestDto
	) {

		OrderResponseDto.Update order = orderService.updateOrder(orderId, requestDto);

		return new ResponseEntity<>(CommonResponse.ok(order), HttpStatus.OK);

	}

	// 주문 단건 조회
	@GetMapping("/api/order/{orderId}")
	public ResponseEntity<CommonResponse> findOrder(
		@PathVariable Long orderId
	) {

		Role role = Role.USER;
		Long id = 1L;
		Object order = orderService.findOrder(role, id, orderId);

		return new ResponseEntity<>(CommonResponse.ok(order), HttpStatus.OK);
	}

	// 주문 전체 조회
	@GetMapping("/api/order")
	public ResponseEntity<CommonResponse> findOrder(
		@PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
	) {

		Role role = Role.USER;
		Long id = 1L;
		Page<OrderResponseDto.FindAll> order = orderService.findAllOrder(role, id, pageable);

		return new ResponseEntity<>(CommonResponse.ok(order), HttpStatus.OK);
	}

	// 주문 삭제
	@DeleteMapping("/api/dealers/order/{orderId}")
	public ResponseEntity<CommonResponse> deleteOrder(
		@PathVariable Long orderId
	) {
		orderService.deleteOrder(orderId);

		return new ResponseEntity<>(CommonResponse.ok("주문이 삭제 되었습니다."), HttpStatus.OK);
	}

}
