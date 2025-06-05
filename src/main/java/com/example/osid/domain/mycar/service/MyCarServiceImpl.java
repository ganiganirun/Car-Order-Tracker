package com.example.osid.domain.mycar.service;

import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.osid.common.auth.CustomUserDetails;
import com.example.osid.domain.mycar.dto.MyCarListResponse;
import com.example.osid.domain.mycar.dto.MyCarResponse;
import com.example.osid.domain.mycar.entity.Mycar;
import com.example.osid.domain.mycar.exception.MyCarException;
import com.example.osid.domain.mycar.exception.MyCarlErrorCode;
import com.example.osid.domain.mycar.repository.MycarRepository;
import com.example.osid.domain.order.entity.Orders;
import com.example.osid.domain.order.repository.OrderRepository;
import com.example.osid.domain.user.entity.User;
import com.example.osid.domain.user.exception.UserErrorCode;
import com.example.osid.domain.user.exception.UserException;
import com.example.osid.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MyCarServiceImpl implements MyCarService {

	private final MycarRepository mycarRepository;
	private final OrderRepository orderRepository;
	private final UserRepository userRepository;

	//myCar 단일 조회 (모델명, body number, 주문 옵션 list)
	@Override
	@Transactional(readOnly = true)
	public MyCarResponse findMyCar(CustomUserDetails customUserDetails, Long myCarId) {
		Mycar mycar = findMyCarOrElseThrow(myCarId);
		Long userId = customUserDetails.getId();
		validateMyCarOwner(userId, mycar.getUser().getId());
		return new MyCarResponse(mycar.getOrders());
	}

	//myCar 전체조회 (mycar 모델명)
	@Override
	@Transactional(readOnly = true)
	public Page<MyCarListResponse> findAllMyCar(CustomUserDetails customUserDetails, int page, int size) {

		Pageable pageable = PageRequest.of(page - 1, size);
		Long userId = customUserDetails.getId();
		Page<Mycar> myCarList = mycarRepository.findAllByUserIdAndDeletedAtIsNull(userId, pageable);
		return myCarList.map(MyCarListResponse::from);
	}

	//myCar 삭제(soft deleted)
	@Override
	@Transactional
	public void deleteMyCar(CustomUserDetails customUserDetails, Long myCarId) {
		Long userId = customUserDetails.getId();
		Mycar mycar = findMyCarOrElseThrow(myCarId);
		validateMyCarOwner(userId, mycar.getUser().getId());
		mycar.setDeletedAt();
	}

	// 매개변수 userId, orderId -> 후에 orders 로 변경?
	@Override
	@Transactional
	public MyCarResponse saveMyCar(Long userId, Long ordersId) {

		User user = userRepository.findById(userId).orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

		// 이미 등록된 차량인 경우
		boolean existsMyCar = mycarRepository.existsByOrdersId(ordersId);
		if (existsMyCar) {
			throw new MyCarException(MyCarlErrorCode.CAR_ALREADY_OWNED);
		}

		Orders orders = orderRepository.findById(ordersId)
			.orElseThrow(() -> new MyCarException(MyCarlErrorCode.MY_CAR_NOT_FOUND));
		Mycar mycar = new Mycar(user, orders);
		mycarRepository.save(mycar);
		return new MyCarResponse(orders);
	}

	// myCar 조회시 없으면 예외출력
	private Mycar findMyCarOrElseThrow(Long myCarId) {
		return mycarRepository.findByIdAndDeletedAtIsNull(myCarId)
			.orElseThrow(() -> new MyCarException(MyCarlErrorCode.MY_CAR_NOT_FOUND));
	}

	// 로그인한 유저와 myCar 의 유저가 일지하는지 확인
	private void validateMyCarOwner(Long userId, Long myCarUserId) {
		if (!Objects.equals(myCarUserId, userId)) {
			throw new MyCarException(MyCarlErrorCode.MY_CAR_NOT_OWED);
		}
	}
}
