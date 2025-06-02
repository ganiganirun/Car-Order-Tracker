package com.example.osid.domain.payment.service;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

import com.example.osid.common.exception.CustomException;
import com.example.osid.common.exception.ErrorCode;
import com.example.osid.domain.order.entity.Orders;
import com.example.osid.domain.order.enums.OrderStatus;
import com.example.osid.domain.order.repository.OrderRepository;
import com.example.osid.domain.payment.dto.PaymentRequestDto;
import com.example.osid.domain.payment.entity.Payments;
import com.example.osid.domain.payment.enums.PaymentStatus;
import com.example.osid.domain.payment.repository.PaymentRepository;
import com.example.osid.domain.user.entity.User;
import com.example.osid.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentService {

	private final OrderRepository orderRepository;
	private final PaymentRepository paymentRepository;
	private final UserRepository userRepository;

	public void processPaymentDone(PaymentRequestDto request, String impUid) {

		String merchantUid = request.getMerchantUid();
		Long userId = request.getUserId();

		// verifyUserIdMatch(userId); // 로그인 된 사용자와 요청 사용자 비교

		Long totalPrice = request.getAmount();

		//orders 테이블에서 오더 상태 변화
		Orders currentOrder = orderRepository.findByMerchantUid(merchantUid)
			.orElseThrow(() -> new CustomException(ErrorCode.INVALID_INPUT_VALUE));

		currentOrder.setOrderStatus(OrderStatus.COMPLETED);

		// Payment 테이블에 저장할 User 객체
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new CustomException(ErrorCode.INVALID_INPUT_VALUE));

		// 주문한 상품들에 대해 각각 결제내역 저장
		createPaymentHistory(currentOrder, user, totalPrice, impUid);
	}

	// 결제내역 테이블 저장하는 메서드
	private void createPaymentHistory(Orders order, User user, Long totalPrice, String impUid) {
		Payments payments = new Payments(user, order, totalPrice, impUid, PaymentStatus.PAID, LocalDate.now());
		paymentRepository.save(payments);
	}

}
