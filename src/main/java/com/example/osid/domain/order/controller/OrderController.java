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
import com.example.osid.domain.order.dto.request.OrderRequestDto;
import com.example.osid.domain.order.dto.response.OrderResponseDto;
import com.example.osid.domain.order.service.OrderService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
// @RequestMapping("/api/order")
public class OrderController {

	private final OrderService orderService;

	// 주문 생성
	@PostMapping("/api/dealer/order")
	public ResponseEntity<OrderResponseDto.Add> createOrder(
		@RequestBody OrderRequestDto.Add requestDto
	) {
		Long dealerId = 1L;

		OrderResponseDto.Add order = orderService.createOrder(dealerId, requestDto);

		return ResponseEntity.status(HttpStatus.CREATED).body(order);
	}

	// 주문 수정
	@PatchMapping("/api/dealer/order/{orderId}")
	public ResponseEntity<OrderResponseDto.Update> updateOrder(
		@PathVariable Long orderId,
		@RequestBody OrderRequestDto.Update requestDto
	) {

		OrderResponseDto.Update order = orderService.updateOrder(orderId, requestDto);

		return ResponseEntity.status(HttpStatus.OK).body(order);

	}

	// 주문 단건 조회
	@GetMapping("/api/order/{orderId}")
	public ResponseEntity<Object> findOrder(
		@PathVariable Long orderId
	) {

		Role role = Role.USER;
		Long id = 1L;
		Object order = orderService.findOrder(role, id, orderId);

		return ResponseEntity.status(HttpStatus.OK).body(order);
	}

	// 주문 전체 조회
	@GetMapping("/api/order")
	public ResponseEntity<Page<OrderResponseDto.FindAll>> findOrder(
		@PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
	) {

		Role role = Role.USER;
		Long id = 1L;
		Page<OrderResponseDto.FindAll> order = orderService.findAllOrder(role, id, pageable);

		return ResponseEntity.status(HttpStatus.OK).body(order);
	}

	// 주문 삭제
	@DeleteMapping("/api/dealer/order/{orderId}")
	public ResponseEntity<String> deleteOrder(
		@PathVariable Long orderId
	) {
		orderService.deleteOrder(orderId);

		return ResponseEntity.status(HttpStatus.NO_CONTENT).body("주문이 삭제 되었습니다.");
	}

}
