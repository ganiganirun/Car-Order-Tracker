package com.example.osid.domain.order.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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
import com.example.osid.domain.history.entity.History;
import com.example.osid.domain.history.repository.HistoryRepository;
import com.example.osid.domain.master.entity.Master;
import com.example.osid.domain.master.exception.MasterErrorCode;
import com.example.osid.domain.master.exception.MasterException;
import com.example.osid.domain.master.repository.MasterRepository;
import com.example.osid.domain.model.entity.Model;
import com.example.osid.domain.model.exception.ModelErrorCode;
import com.example.osid.domain.model.exception.ModelException;
import com.example.osid.domain.model.repository.ModelRepository;
import com.example.osid.domain.option.entity.Option;
import com.example.osid.domain.option.exception.OptionErrorCode;
import com.example.osid.domain.option.exception.OptionException;
import com.example.osid.domain.option.repository.OptionRepository;
import com.example.osid.domain.order.dto.request.OrderRequestDto;
import com.example.osid.domain.order.dto.response.OrderDetailResponse;
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
import com.example.osid.domain.waitingorder.entity.WaitingOrders;
import com.example.osid.domain.waitingorder.enums.WaitingStatus;
import com.example.osid.domain.waitingorder.exception.WaitingOrderErrorCode;
import com.example.osid.domain.waitingorder.exception.WaitingOrderException;
import com.example.osid.domain.waitingorder.repository.WaitingOrderRepository;
import com.example.osid.event.OrderCompletedMyCarEvent;
import com.example.osid.event.OrderEventPublisher;
import com.example.osid.event.entity.FailedEvent;
import com.example.osid.event.enums.FailedEventType;
import com.example.osid.event.repository.FailedEventRepository;

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
	private final FailedEventRepository failedEventRepository;
	private final OrderEventPublisher orderEventPublisher;
	private final HistoryRepository historyRepository;
	private final WaitingOrderRepository waitingOrderRepository;

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
		Dealer dealer = dealerRepository.findByEmailAndIsDeletedFalse(customUserDetails.getEmail())
			.orElseThrow(() -> new DealerException(DealerErrorCode.DEALER_NOT_FOUND));

		User user = userRepository.findByEmailAndIsDeletedFalse(requestDto.getUserEmail())
			.orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

		Model model = modelRepository.findById(requestDto.getModelId())
			.orElseThrow(() -> new ModelException(ModelErrorCode.MODEL_NOT_FOUND));

		List<Option> options = optionRepository.findByIdIn(requestDto.getOption());

		if (options.size() != requestDto.getOption().size()) {
			throw new OptionException(OptionErrorCode.OPTION_NOT_FOUND);
		}

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

		Orders saveorder = orderRepository.save(orders);

		// Option 이름만 리스트화
		// List<String> optionNames = options
		// 	.stream()
		// 	.map(Option::getName)
		// 	.toList();

		List<String> optionNames = changeOptions(saveorder);

		return OrderResponseDto.Add.builder()
			.id(saveorder.getId())
			.model(saveorder.getModel().getName())
			.userId(saveorder.getUser().getId())
			.dealerName(saveorder.getDealer().getName())
			.orderOptions(optionNames)
			.merchantUid(saveorder.getMerchantUid())
			.address(saveorder.getAddress())
			.totalPrice(saveorder.getTotalPrice())
			.orderStatus(saveorder.getOrderStatus())
			.createdAt(saveorder.getCreatedAt())
			.build();

	}

	// 주문 수정
	@Transactional("dataTransactionManager")
	public OrderResponseDto.Update updateOrder(CustomUserDetails customUserDetails, Long orderId,
		OrderRequestDto.Update requestDto) {

		// 예외처리 refactor
		Orders orders = extractOrder(orderId);

		// 검증
		validateOrderOwner(orders, customUserDetails, extractRole(customUserDetails));

		if (requestDto.getAddress().isPresent()) {
			orders.setAddress(requestDto.getAddress().get());
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

	// 주문 취소 -> 결제가 제대로 진행되지 않았을 경우에만
	@Transactional("dataTransactionManager")
	public void cancelOrder(Long orderId) {

		Orders order = extractOrder(orderId);

		WaitingOrders waitingOrders = waitingOrderRepository.findByOrders(order)
			.orElseThrow(() -> new WaitingOrderException(WaitingOrderErrorCode.WAITING_ORDER_NOT_FOUND));

		// 대기열의 상태가 Waiting이 아니면 주문 취소 불가능
		if (!waitingOrders.getWaitingStatus().equals(WaitingStatus.WAITING)) {
			throw new OrderException(OrderErrorCode.ORDER_CANCELLATION_NOT_ALLOWED);
		}

		order.setOrderStatus(OrderStatus.FAILED);
	}

	// 주문 단건조회
	public OrderDetailResponse findOrder(CustomUserDetails customUserDetails, Long orderId) {

		// 예외처리 refactor
		Orders orders = extractOrder(orderId);

		// List<Option> -> List<String>
		List<String> optionNames = changeOptions(orders);

		// role값 가져오기
		Role role = extractRole(customUserDetails);

		// 검증
		validateOrderOwner(orders, customUserDetails, role);

		// List<OrderDetailResponse.ProcessStep> processSteps = buildProcessSteps(role, orders);

		// 생산에 들어가지 않을 경우 processStep은 빈 리스트 반환
		List<OrderDetailResponse.ProcessStep> processSteps = historyRepository.findByBodyNumber(orders.getBodyNumber())
			.map(h -> buildProcessSteps(role, orders, h))
			.orElse(Collections.emptyList());

		return OrderDetailResponse.of(orders, processSteps);

	}

	// 주문 전체 조회
	public Page<OrderResponseDto.FindAll> findAllOrder(
		CustomUserDetails customUserDetails, Pageable pageable) {

		// role 값 가져오기
		Role role = extractRole(customUserDetails);

		if (role.equals(Role.MASTER)) {

			// 자기가 관리하는 딜러의 주문건만 조회 가능하도록 수정
			Master master = masterRepository.findByEmailAndIsDeletedFalse(customUserDetails.getEmail())
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
	@Transactional("dataTransactionManager")
	public void deleteOrder(CustomUserDetails customUserDetails, Long orderId) {

		// 예외처리 refactor
		Orders orders = extractOrder(orderId);

		WaitingOrders waitingOrders = waitingOrderRepository.findByOrders(orders)
			.orElseThrow(() -> new WaitingOrderException(WaitingOrderErrorCode.WAITING_ORDER_NOT_FOUND));

		// 대기열의 상태가 Waiting이 아니면 주문 취소 불가능
		if (!waitingOrders.getWaitingStatus().equals(WaitingStatus.WAITING)) {
			throw new OrderException(OrderErrorCode.ORDER_CANCELLATION_NOT_ALLOWED);
		}

		// 검증
		validateOrderOwner(orders, customUserDetails, extractRole(customUserDetails));

		orderRepository.delete(orders);

	}

	// 출고완료 상태 변경 및 출고완료일 날짜 생성
	@Transactional("dataTransactionManager")
	public void changeShipped(CustomUserDetails customUserDetails, Long orderId) {

		Orders orders = extractOrder(orderId);

		validateOrderOwner(orders, customUserDetails, extractRole(customUserDetails));

		orders.setOrderStatus(OrderStatus.SHIPPED);

		orders.setActualDeliveryAt(LocalDateTime.now());

		// orderRepository.save(orders);

		// 주문 완료 이벤트 메시지 생성
		OrderCompletedMyCarEvent event = new OrderCompletedMyCarEvent(orderId);
		try {
			//메시지 큐로 전송
			orderEventPublisher.publishOrderCompletedMyCar(event);
		} catch (Exception e) {
			// 실패 이벤트 저장
			failedEventRepository.save(
				new FailedEvent(event.getOrderId(),
					0,
					e.getMessage(),
					FailedEventType.MY_CAR));
		}

	}

	// 수령 완료 상태 변경
	@Transactional("dataTransactionManager")
	public void changeReceived(CustomUserDetails customUserDetails, Long orderId) {

		Orders orders = extractOrder(orderId);

		validateOrderOwner(orders, customUserDetails, extractRole(customUserDetails));

		orders.setOrderStatus(OrderStatus.RECEIVED);

		orders.setReceivedAt(LocalDateTime.now());

		// orderRepository.save(orders);

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

	// processStep을 빌드하는 메소드
	private List<OrderDetailResponse.ProcessStep> buildProcessSteps(Role role, Orders orders, History history) {
		WaitingOrders waitingOrders = waitingOrderRepository.findByOrders(orders)
			.orElseThrow(() -> new WaitingOrderException(WaitingOrderErrorCode.WAITING_ORDER_NOT_FOUND));

		// History history = historyRepository.findByBodyNumber(orders.getBodyNumber())
		// 	.orElseThrow(() -> new HistoryException(HistoryErrorCode.HISTORY_NOT_FOUND));

		List<OrderDetailResponse.ProcessStep> steps = new ArrayList<>();

		LocalDateTime base = waitingOrders.getUpdatedAt(); // 기준 시작일

		steps.add(OrderDetailResponse.ProcessStep.from("주문", orders.getCreatedAt(),
			waitingOrders.getCreatedAt()));
		steps.add(OrderDetailResponse.ProcessStep.from("생산대기", waitingOrders.getCreatedAt(),
			waitingOrders.getUpdatedAt()));

		double total = history.getTotalDuration().doubleValue();
		List<Integer> time = hourMin(total);
		LocalDateTime expectedAt = base.plusHours(time.get(0)).plusMinutes(time.get(1)).plusDays(8);

		if (role.equals(Role.USER)) {
			steps.add(OrderDetailResponse.ProcessStep.from("생산중", waitingOrders.getUpdatedAt(),
				expectedAt));
			// 출고 단계
			steps.add(OrderDetailResponse.ProcessStep.from(
				"출고준비",
				expectedAt,
				expectedAt.plusDays(8)
			));
			steps.add(OrderDetailResponse.ProcessStep.from(
				"출고 완료",
				orders.getActualDeliveryAt(),
				orders.getActualDeliveryAt()
			));
			steps.add(OrderDetailResponse.ProcessStep.from(
				"수령 완료",
				orders.getReceivedAt(),
				orders.getReceivedAt()
			));

			return steps;

		}

		// 관리자용 공정단계
		Map<String, Double> stageDurationMap = Map.of(
			"프레스", history.getStage1().doubleValue(),
			"차체", history.getStage2().doubleValue(),
			"도장", history.getStage3().doubleValue(),
			"의장", history.getStage4().doubleValue(),
			"검사", history.getStage5().doubleValue()
		);

		LocalDateTime current = base;

		for (Map.Entry<String, Double> entry : stageDurationMap.entrySet()) {

			List<Integer> admintime = hourMin(entry.getValue());

			LocalDateTime end = current.plusHours(admintime.get(0)).plusMinutes(admintime.get(1));

			if (entry.getKey().equals("프레스")) {
				end = end.plusDays(8);
			}

			steps.add(OrderDetailResponse.ProcessStep.from(
				entry.getKey(),
				current,
				end
			));

			current = end;
		}
		// 출고 단계
		steps.add(OrderDetailResponse.ProcessStep.from(
			"출고준비",
			current,
			current.plusDays(8)
		));
		steps.add(OrderDetailResponse.ProcessStep.from(
			"출고 완료",
			orders.getActualDeliveryAt(),
			orders.getActualDeliveryAt()
		));
		steps.add(OrderDetailResponse.ProcessStep.from(
			"수령 완료",
			orders.getReceivedAt(),
			orders.getReceivedAt()
		));

		return steps;
	}

	private List<Integer> hourMin(double duration) {
		int hours = (int)duration;
		int minutes = (int)((duration - hours) * 60);

		List<Integer> hours1 = List.of(hours, minutes);

		return hours1;

	}

}


