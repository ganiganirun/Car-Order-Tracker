package com.example.osid.domain.order.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.osid.common.auth.CustomUserDetails;
import com.example.osid.common.response.CommonResponse;
import com.example.osid.domain.order.dto.request.OrderRequestDto;
import com.example.osid.domain.order.dto.response.OrderDetailResponse;
import com.example.osid.domain.order.dto.response.OrderResponseDto;
import com.example.osid.domain.order.service.OrderService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class OrderController {

	private final OrderService orderService;

	// 주문 생성
	@PostMapping("/api/dealers/order")
	@ResponseStatus(HttpStatus.CREATED)
	public CommonResponse<OrderResponseDto.Add> createOrder(
		@RequestBody OrderRequestDto.Add requestDto,
		@AuthenticationPrincipal CustomUserDetails customUserDetails
	) {

		OrderResponseDto.Add order = orderService.createOrder(customUserDetails, requestDto);

		return CommonResponse.created(order);

	}

	// 주문 수정
	@PatchMapping("/api/dealers/order/{orderId}")
	@ResponseStatus(HttpStatus.OK)
	public CommonResponse<OrderResponseDto.Update> updateOrder(
		@PathVariable Long orderId,
		@RequestBody OrderRequestDto.Update requestDto,
		@AuthenticationPrincipal CustomUserDetails customUserDetails
	) {

		OrderResponseDto.Update order = orderService.updateOrder(customUserDetails, orderId, requestDto);

		return CommonResponse.ok(order);

	}

	// 주문 취소(결제 미완료 등) 요청 API
	@PostMapping("/api/dealers/order/{orderId}/fail")
	public CommonResponse<Void> cancelOrder(@PathVariable Long orderId) {
		orderService.cancelOrder(orderId);
		return CommonResponse.ok();
	}

	// 주문 단건 조회
	@GetMapping("/api/order/{orderId}")
	@ResponseStatus(HttpStatus.OK)
	public CommonResponse<OrderDetailResponse> findOrder(
		@PathVariable Long orderId,
		@AuthenticationPrincipal CustomUserDetails customUserDetails
	) {

		OrderDetailResponse order = orderService.findOrder(customUserDetails, orderId);

		return CommonResponse.ok(order);
	}

	// 주문 전체 조회
	@GetMapping("/api/order")
	@ResponseStatus(HttpStatus.OK)
	public CommonResponse<Page<OrderResponseDto.FindAll>> findAllOrder(
		@AuthenticationPrincipal CustomUserDetails customUserDetails,
		@PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
	) {

		Page<OrderResponseDto.FindAll> order = orderService.findAllOrder(customUserDetails, pageable);

		return CommonResponse.ok(order);
	}

	// 주문 삭제
	@DeleteMapping("/api/dealers/order/{orderId}")
	@ResponseStatus(HttpStatus.OK)
	public CommonResponse<Void> deleteOrder(
		@PathVariable Long orderId,
		@AuthenticationPrincipal CustomUserDetails customUserDetails
	) {
		orderService.deleteOrder(customUserDetails, orderId);

		return CommonResponse.ok();
	}

	// 차량 출고 완료
	@PostMapping("/api/order/shipped/{orderId}")
	@ResponseStatus(HttpStatus.OK)
	public CommonResponse<Void> changeShipped(
		@AuthenticationPrincipal CustomUserDetails customUserDetails,
		@PathVariable Long orderId
	) {
		orderService.changeShipped(customUserDetails, orderId);
		return CommonResponse.ok();
	}

	// 차량 수령 완료
	@PostMapping("/api/order/received/{orderId}")
	@ResponseStatus(HttpStatus.OK)
	public CommonResponse<Void> changeReceived(
		@AuthenticationPrincipal CustomUserDetails customUserDetails,
		@PathVariable Long orderId
	) {
		orderService.changeReceived(customUserDetails, orderId);
		return CommonResponse.ok();
	}

}
