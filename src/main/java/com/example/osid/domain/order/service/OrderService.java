package com.example.osid.domain.order.service;

import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.osid.common.auth.CustomUserDetails;
import com.example.osid.common.entity.enums.Role;
import com.example.osid.common.exception.CustomException;
import com.example.osid.common.exception.ErrorCode;
import com.example.osid.domain.dealer.entity.Dealer;
import com.example.osid.domain.dealer.repository.DealerRepository;
import com.example.osid.domain.model.entity.Model;
import com.example.osid.domain.model.repository.ModelRepository;
import com.example.osid.domain.option.entity.Option;
import com.example.osid.domain.option.repository.OptionRepository;
import com.example.osid.domain.order.dto.request.OrderRequestDto;
import com.example.osid.domain.order.dto.response.OrderResponseDto;
import com.example.osid.domain.order.entity.OrderOption;
import com.example.osid.domain.order.entity.Orders;
import com.example.osid.domain.order.enums.OrderStatus;
import com.example.osid.domain.order.repository.OrderRepository;
import com.example.osid.domain.order.repository.OrderSearch;
import com.example.osid.domain.user.entity.User;
import com.example.osid.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService {

	private final OrderRepository orderRepository;
	private final OptionRepository optionRepository;
	private final ModelRepository modelRepository;
	private final UserRepository userRepository;
	private final DealerRepository dealerRepository;
	private final OrderSearch orderSearch;

	// 주문 생성
	public OrderResponseDto.Add createOrder(CustomUserDetails customUserDetails, OrderRequestDto.Add requestDto) {
		/*
		 * dealerId로 딜러 가져오기
		 * 유저 이메일로 유저 가져오기
		 * 옵션 id 리스트로 옵션에서 가져오기
		 * 모델 id로 모델 객체 가져오기
		 * 옵션리스트로 옵션가격 계산
		 * 차량 고유번호 생성
		 * 총 가격 계산
		 * order 객체 생성
		 * orderoption 생성
		 * order 객체 저장
		 * */

		Dealer dealer = dealerRepository.findById(customUserDetails.getId())
			.orElseThrow(() -> new CustomException(ErrorCode.INVALID_INPUT_VALUE));

		User user = userRepository.findByEmail(requestDto.getUserEmail())
			.orElseThrow(() -> new CustomException(ErrorCode.INVALID_INPUT_VALUE));

		Model model = modelRepository.findById(requestDto.getModelId())
			.orElseThrow(() -> new CustomException(ErrorCode.INVALID_INPUT_VALUE));

		List<Option> options = optionRepository.findByIdIn(requestDto.getOption());

		// 총 금액 계산
		Long totalPrice = options.stream().mapToLong(Option::getPrice).sum() + model.getPrice();

		Orders orders = Orders.builder()
			.address(requestDto.getAddress())
			.totalPrice(totalPrice)
			.orderStatus(OrderStatus.ORDERED)
			.user(user)
			.dealer(dealer)
			.model(model)
			.build();

		List<OrderOption> orderOptions = options
			.stream()
			.map(option -> new OrderOption(orders, option))
			.toList();

		orders.setOrderOptions(orderOptions);

		orderRepository.save(orders);

		// Option 이름만 리스트화
		List<String> optionNames = options
			.stream()
			.map(Option::getName)
			.toList();

		return OrderResponseDto.Add.builder()
			.id(orders.getId())
			.model(orders.getModel().getName())
			.userId(orders.getUser().getId())
			.dealerName(orders.getDealer().getName())
			.orderOptions(optionNames)
			.merchantUid(orders.getMerchantUid())
			.address(orders.getAddress())
			.totalPrice(orders.getTotalPrice())
			.orderStatus(orders.getOrderStatus())
			.createdAt(orders.getCreatedAt())
			.build();

	}

	// 주문 수정
	@Transactional
	public OrderResponseDto.Update updateOrder(CustomUserDetails customUserDetails, Long orderId,
		OrderRequestDto.Update requestDto) {

		Orders orders = orderRepository.findById(orderId)
			.orElseThrow(() -> new CustomException(ErrorCode.INVALID_INPUT_VALUE));

		// 검증
		validateOrderOwner(orders, customUserDetails, extractRole(customUserDetails));

		// 유동적으로 바꾸기 위해서 if문으로 관리
		// 추후 다른 방법이 생기면 바뀔 예정

		// 주소 수정
		if (requestDto.getAddress() != null) {
			orders.setAddress(requestDto.getAddress());
		}

		// 주문 상태 수정
		if (requestDto.getOrderStatus() != null) {
			orders.setOrderStatus(requestDto.getOrderStatus());
		}

		// 예상 출고일 수정
		if (requestDto.getExpectedDeliveryAt() != null) {
			orders.setExpectedDeliveryAt(requestDto.getExpectedDeliveryAt());
		}

		// 실제 출고일 수정
		if (requestDto.getActualDeliveryAt() != null) {
			orders.setActualDeliveryAt(requestDto.getActualDeliveryAt());
		}

		// List<Option> -> List<String>
		List<String> optionNames = changeOptions(orders);

		return OrderResponseDto.Update.builder()
			.id(orders.getId())
			.userName(orders.getUser().getName())
			.model(orders.getModel().getName())
			.dealerName(orders.getDealer().getName())
			.orderOptions(optionNames)
			.address(orders.getAddress())
			.totalPrice(orders.getTotalPrice())
			.orderStatus(orders.getOrderStatus())
			.expectedDeliveryAt(orders.getExpectedDeliveryAt())
			.actualDeliveryAt(orders.getActualDeliveryAt())
			.createdAt(orders.getCreatedAt())
			.build();

	}

	public Object findOrder(CustomUserDetails customUserDetails, Long orderId) {

		Orders orders = orderRepository.findById(orderId)
			.orElseThrow(() -> new CustomException(ErrorCode.INVALID_INPUT_VALUE));

		// List<Option> -> List<String>
		List<String> optionNames = changeOptions(orders);

		// role값 가져오기
		Role role = extractRole(customUserDetails);

		// 검증
		validateOrderOwner(orders, customUserDetails, role);

		// role 에 따라 다른 값 return
		switch (role) {
			case USER -> {
				return OrderResponseDto.UserView.builder()
					.id(orders.getId())
					.userName(orders.getUser().getName())
					.dealerName(orders.getDealer().getName())
					.model(orders.getModel().getName())
					.orderOptions(optionNames)
					.address(orders.getAddress())
					.totalPrice(orders.getTotalPrice())
					.merchantUid(orders.getMerchantUid())
					.orderStatus(orders.getOrderStatus())
					.expectedDeliveryAt(orders.getExpectedDeliveryAt())
					.actualDeliveryAt(orders.getActualDeliveryAt())
					.createdAt(orders.getCreatedAt())
					.build();
			}
			case DEALER -> {
				return OrderResponseDto.AdminView.builder()
					.id(orders.getId())
					.userName(orders.getUser().getName())
					.dealerName(orders.getDealer().getName())
					.model(orders.getModel().getName())
					.orderOptions(optionNames)
					.address(orders.getAddress())
					.totalPrice(orders.getTotalPrice())
					.merchantUid(orders.getMerchantUid())
					.orderStatus(orders.getOrderStatus())
					.expectedDeliveryAt(orders.getExpectedDeliveryAt())
					.actualDeliveryAt(orders.getActualDeliveryAt())
					.createdAt(orders.getCreatedAt())
					.build();
			}
			case MASTER -> {
				// 마스터는 모든 주문에 대해서 조회 가능
				return OrderResponseDto.AdminView.builder()
					.id(orders.getId())
					.model(orders.getModel().getName())
					.userName(orders.getUser().getName())
					.dealerName(orders.getDealer().getName())
					.orderOptions(optionNames)
					.address(orders.getAddress())
					.totalPrice(orders.getTotalPrice())
					.merchantUid(orders.getMerchantUid())
					.orderStatus(orders.getOrderStatus())
					.expectedDeliveryAt(orders.getExpectedDeliveryAt())
					.actualDeliveryAt(orders.getActualDeliveryAt())
					.createdAt(orders.getCreatedAt())
					.build();
			}

		}

		return null;
	}

	// 주문 전체 조회
	public Page<OrderResponseDto.FindAll> findAllOrder(CustomUserDetails customUserDetails, Pageable pageable) {

		// role 값 가져오기
		Role role = extractRole(customUserDetails);

		Page<Orders> orders = orderSearch.findOrderAll(role, pageable, customUserDetails.getId());

		return orders.map(
			order -> new OrderResponseDto.FindAll(order.getId(), order.getUser().getName(), order.getDealer().getName(),
				order.getModel().getName()));
	}

	// 주문 삭제
	public void deleteOrder(CustomUserDetails customUserDetails, Long orderId) {

		Orders orders = orderRepository.findById(orderId)
			.orElseThrow(() -> new CustomException(ErrorCode.INVALID_INPUT_VALUE));

		// 검증
		validateOrderOwner(orders, customUserDetails, extractRole(customUserDetails));

		orderRepository.delete(orders);

	}

	// role 가져오기
	private Role extractRole(CustomUserDetails customUserDetails) {

		Collection<? extends GrantedAuthority> grantedAuthorities = customUserDetails.getAuthorities();

		String authorityString = grantedAuthorities.stream()
			.findFirst()
			.map(GrantedAuthority::getAuthority)
			.orElseThrow(() -> new CustomException(ErrorCode.INVALID_INPUT_VALUE));

		Role role = Role.valueOf(authorityString.replace("ROLE_", ""));

		return role;
	}

	// 검증
	private void validateOrderOwner(Orders orders, CustomUserDetails userDetails, Role role) {
		Long userId = userDetails.getId();

		switch (role) {
			case USER -> {
				if (!orders.getUser().getId().equals(userId)) {
					throw new CustomException(ErrorCode.FORBIDDEN);
				}
			}

			case DEALER -> {
				if (!orders.getDealer().getId().equals(userId)) {
					throw new CustomException(ErrorCode.FORBIDDEN);
				}
			}

			case MASTER -> {
				// MASTER는 모든 주문에 접근 가능
			}

			default -> throw new CustomException(ErrorCode.FORBIDDEN);

		}
	}

	private List<String> changeOptions(Orders orders) {

		return orders.getOrderOptions().stream()
			.map(OrderOption::getOption)
			.map(Option::getName)
			.toList();

	}

}
