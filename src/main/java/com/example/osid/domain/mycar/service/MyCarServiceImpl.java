package com.example.osid.domain.mycar.service;

import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.osid.common.auth.CustomUserDetails;
import com.example.osid.domain.mycar.dto.MyCarListResponse;
import com.example.osid.domain.mycar.dto.MyCarResponse;
import com.example.osid.domain.mycar.entity.Mycar;
import com.example.osid.domain.mycar.exception.MyCarErrorCode;
import com.example.osid.domain.mycar.exception.MyCarException;
import com.example.osid.domain.mycar.repository.MycarRepository;
import com.example.osid.domain.order.entity.Orders;
import com.example.osid.domain.order.exception.OrderErrorCode;
import com.example.osid.domain.order.exception.OrderException;
import com.example.osid.domain.order.repository.OrderRepository;
import com.example.osid.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MyCarServiceImpl implements MyCarService {

	private final MycarRepository mycarRepository;
	private final OrderRepository orderRepository;
	private final UserRepository userRepository;

	//myCar 단일 조회 (모델명, body number, 주문 옵션 list)
	@Override
	@Transactional(value = "dataTransactionManager", readOnly = true)
	public MyCarResponse findMyCar(CustomUserDetails customUserDetails, Long myCarId) {
		Mycar mycar = findMyCarOrElseThrow(myCarId);
		Long userId = customUserDetails.getId();
		validateMyCarOwner(userId, mycar.getUser().getId());
		return new MyCarResponse(mycar.getOrders());
	}

	//myCar 전체조회 (mycar 모델명)
	@Override
	@Transactional(value = "dataTransactionManager", readOnly = true)
	public Page<MyCarListResponse> findAllMyCar(CustomUserDetails customUserDetails, Pageable pageable) {
		Long userId = customUserDetails.getId();
		Page<Mycar> myCarList = mycarRepository.findAllByUserIdAndDeletedAtIsNull(userId, pageable);
		return myCarList.map(MyCarListResponse::from);
	}

	//myCar 삭제(soft deleted)
	@Override
	@Transactional(value = "dataTransactionManager")
	public void deleteMyCar(CustomUserDetails customUserDetails, Long myCarId) {
		Long userId = customUserDetails.getId();
		Mycar mycar = findMyCarOrElseThrow(myCarId);
		validateMyCarOwner(userId, mycar.getUser().getId());
		mycar.setDeletedAt();
	}

	@Override
	@Transactional(value = "dataTransactionManager")
	public MyCarResponse saveMyCar(Long ordersId) {

		Orders orders = orderRepository.findWithOptionsById(ordersId)
			.orElseThrow(() -> new OrderException(OrderErrorCode.ORDER_NOT_FOUND));

		// 이미 등록된 차량인 경우
		boolean existsMyCar = mycarRepository.existsByOrdersId(ordersId);
		if (existsMyCar) {
			throw new MyCarException(MyCarErrorCode.CAR_ALREADY_OWNED);
		}

		Mycar mycar = new Mycar(orders);
		mycarRepository.save(mycar);
		return new MyCarResponse(orders);
	}

	// myCar 조회시 없으면 예외출력
	private Mycar findMyCarOrElseThrow(Long myCarId) {
		return mycarRepository.findByIdAndDeletedAtIsNull(myCarId)
			.orElseThrow(() -> new MyCarException(MyCarErrorCode.MY_CAR_NOT_FOUND));
	}

	// 로그인한 유저와 myCar 의 유저가 일지하는지 확인
	private void validateMyCarOwner(Long userId, Long myCarUserId) {
		if (!Objects.equals(myCarUserId, userId)) {
			throw new MyCarException(MyCarErrorCode.MY_CAR_NOT_OWED);
		}
	}
}
