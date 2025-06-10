package com.example.osid.domain.order.service;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
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
import com.example.osid.domain.dealer.exception.DealerErrorCode;
import com.example.osid.domain.dealer.exception.DealerException;
import com.example.osid.domain.dealer.repository.DealerRepository;
import com.example.osid.domain.master.entity.Master;
import com.example.osid.domain.master.exception.MasterErrorCode;
import com.example.osid.domain.master.exception.MasterException;
import com.example.osid.domain.master.repository.MasterRepository;
import com.example.osid.domain.model.entity.Model;
import com.example.osid.domain.model.exception.ModelErrorCode;
import com.example.osid.domain.model.exception.ModelException;
import com.example.osid.domain.model.repository.ModelRepository;
import com.example.osid.domain.option.entity.Option;
import com.example.osid.domain.option.repository.OptionRepository;
import com.example.osid.domain.order.dto.request.OrderRequestDto;
import com.example.osid.domain.order.dto.response.OrderResponseDto;
import com.example.osid.domain.order.entity.OrderOption;
import com.example.osid.domain.order.entity.Orders;
import com.example.osid.domain.order.enums.OrderStatus;
import com.example.osid.domain.order.exception.OrderErrorCode;
import com.example.osid.domain.order.exception.OrderException;
import com.example.osid.domain.order.repository.OrderRepository;
import com.example.osid.domain.order.repository.OrderSearch;
import com.example.osid.domain.user.entity.User;
import com.example.osid.domain.user.exception.UserErrorCode;
import com.example.osid.domain.user.exception.UserException;
import com.example.osid.domain.user.repository.UserRepository;
import com.example.osid.event.OrderCompletedEvent;

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
	private final MasterRepository masterRepository;
	private final RabbitTemplate rabbitTemplate;

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

		// 예외처리 refactor
		Dealer dealer = dealerRepository.findById(customUserDetails.getId())
			.orElseThrow(() -> new DealerException(DealerErrorCode.DEALER_NOT_FOUND));

		User user = userRepository.findByEmail(requestDto.getUserEmail())
			.orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

		Model model = modelRepository.findById(requestDto.getModelId())
			.orElseThrow(() -> new ModelException(ModelErrorCode.MODEL_NOT_FOUND));

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

		// 예외처리 refactor
		Orders orders = extractOrder(orderId);

		// 검증
		validateOrderOwner(orders, customUserDetails, extractRole(customUserDetails));

		// 유동적으로 바꾸기 위해서 if문으로 관리
		// 추후 다른 방법이 생기면 바뀔 예정

		// 주소 수정
		// if (requestDto.getAddress() != null) {
		// 	orders.setAddress(requestDto.getAddress());
		// }
		//
		// // 주문 상태 수정
		// if (requestDto.getOrderStatus() != null) {
		// 	orders.setOrderStatus(requestDto.getOrderStatus());
		// }
		//
		// // 예상 출고일 수정
		// if (requestDto.getExpectedDeliveryAt() != null) {
		// 	orders.setExpectedDeliveryAt(requestDto.getExpectedDeliveryAt());
		// }
		//
		// // 실제 출고일 수정
		// if (requestDto.getActualDeliveryAt() != null) {
		// 	orders.setActualDeliveryAt(requestDto.getActualDeliveryAt());
		// }

		if (requestDto.getAddress().isPresent()) {
			orders.setAddress(requestDto.getAddress().get());
		}

		// 주문 상태 수정
		if (requestDto.getOrderStatus() != null) {
			orders.setOrderStatus(requestDto.getOrderStatus());
		}

		// 예상 출고일 수정
		if (requestDto.getExpectedDeliveryAt().isPresent()) {
			orders.setExpectedDeliveryAt(requestDto.getExpectedDeliveryAt().get());
		}

		// 실제 출고일 수정
		if (requestDto.getActualDeliveryAt().isPresent()) {
			orders.setActualDeliveryAt(requestDto.getActualDeliveryAt().get());
		}

		if (Objects.equals(requestDto.getOrderStatus(), OrderStatus.COMPLETED)) {
			// 주문 완료 이벤트 메시지 생성
			OrderCompletedEvent event = new OrderCompletedEvent(orderId);
			// 메시지 큐로 전송
			rabbitTemplate.convertAndSend("order.exchange", "order.completed", event);
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

		// 예외처리 refactor
		Orders orders = extractOrder(orderId);

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
	public Page<OrderResponseDto.FindAll> findAllOrder(
		CustomUserDetails customUserDetails, Pageable pageable) {

		// role 값 가져오기
		Role role = extractRole(customUserDetails);

		if (role.equals(Role.MASTER)) {

			// 자기가 관리하는 딜러의 주문건만 조회 가능하도록 수정
			Master master = masterRepository.findById(customUserDetails.getId())
				.orElseThrow(() -> new MasterException(MasterErrorCode.MASTER_NOT_FOUND));

			List<Long> dealerIds = dealerRepository.findByMasterAndIsDeletedFalse(master)
				.stream()
				.map(dealer -> dealer.getId())
				.toList();

			return orderSearch.findOrderAllForMaster(role, pageable, dealerIds).map(
				order -> new OrderResponseDto.FindAll(
					order.getId(),
					order.getUser().getName(),
					order.getDealer().getName(),
					order.getModel().getName())
			);

		} else {
			// 유저 딜러의 전체 주문 조회
			return orderSearch.findOrderAllForUserOrDealer(
					role, pageable, customUserDetails.getId())
				.map(order -> new OrderResponseDto.FindAll(
					order.getId(),
					order.getUser().getName(),
					order.getDealer().getName(),
					order.getModel().getName())
				);

		}

	}

	// 주문 삭제
	public void deleteOrder(CustomUserDetails customUserDetails, Long orderId) {

		// 예외처리 refactor
		Orders orders = extractOrder(orderId);

		// 검증
		validateOrderOwner(orders, customUserDetails, extractRole(customUserDetails));

		orderRepository.delete(orders);

	}

	// role 가져오기
	private Role extractRole(CustomUserDetails customUserDetails) {

		Collection<? extends GrantedAuthority> grantedAuthorities = customUserDetails.getAuthorities();

		// 예외처리 refactor
		String authorityString = grantedAuthorities.stream()
			.findFirst()
			.map(GrantedAuthority::getAuthority)
			.orElseThrow(() -> new CustomException(ErrorCode.AUTHORITY_NOT_FOUND));

		Role role = Role.valueOf(authorityString.replace("ROLE_", ""));

		return role;
	}

	// 검증
	private void validateOrderOwner(Orders orders, CustomUserDetails userDetails, Role role) {
		Long id = userDetails.getId();

		// 예외처리 refactor
		switch (role) {
			case USER -> {
				if (!orders.getUser().getId().equals(id)) {
					throw new OrderException(OrderErrorCode.ORDER_ACCESS_DENIED);
				}
			}

			case DEALER -> {
				if (!orders.getDealer().getId().equals(id)) {
					throw new OrderException(OrderErrorCode.ORDER_ACCESS_DENIED);
				}
			}

			case MASTER -> {
				if (!orders.getDealer().getMaster().getId().equals(id)) {
					throw new OrderException(OrderErrorCode.ORDER_ACCESS_DENIED);
				}
			}

			default -> throw new CustomException(ErrorCode.FORBIDDEN);

		}
	}

	// 예외처리 및 order 객체 반환 메소드화(Refactor)
	private Orders extractOrder(Long orderId) {

		Orders orders = orderRepository.findById(orderId)
			.orElseThrow(() -> new OrderException(OrderErrorCode.ORDER_NOT_FOUND));

		return orders;
	}

	// List<Option> -> List<String>
	private List<String> changeOptions(Orders orders) {

		return orders.getOrderOptions().stream()
			.map(OrderOption::getOption)
			.map(Option::getName)
			.toList();

	}

}

