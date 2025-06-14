package com.example.osid.domain.payment.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.osid.domain.order.entity.Orders;
import com.example.osid.domain.order.enums.OrderStatus;
import com.example.osid.domain.order.exception.OrderErrorCode;
import com.example.osid.domain.order.exception.OrderException;
import com.example.osid.domain.order.repository.OrderRepository;
import com.example.osid.domain.payment.dto.PaymentRequestDto;
import com.example.osid.domain.payment.entity.Payments;
import com.example.osid.domain.payment.enums.PaymentStatus;
import com.example.osid.domain.payment.exception.PaymentErrorCode;
import com.example.osid.domain.payment.exception.PaymentException;
import com.example.osid.domain.payment.repository.PaymentRepository;
import com.example.osid.domain.user.entity.User;
import com.example.osid.domain.user.exception.UserErrorCode;
import com.example.osid.domain.user.exception.UserException;
import com.example.osid.domain.user.repository.UserRepository;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.CancelData;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentService {

	private final OrderRepository orderRepository;
	private final PaymentRepository paymentRepository;
	private final UserRepository userRepository;

	@Value("${IMP_API_KEY}")
	private String apiKey;

	@Value("${imp.api.secretkey}")
	private String secretKey;

	private IamportClient iamportClient;

	@PostConstruct
	public void init() {
		this.iamportClient = new IamportClient(apiKey, secretKey);
	}

	private static final int FULL_REFUND = 0;

	@Transactional
	public void processPaymentDone(PaymentRequestDto.Paid request, IamportResponse<Payment> payment) throws
		IamportResponseException,
		IOException {

		Payment iamportPayment = payment.getResponse();
		String impUid = request.getImpUid();
		String merchantUid = request.getMerchantUid();
		Long userId = request.getUserId();
		Long totalPrice = request.getAmount();

		// orders 테이블에서 오더 상태 변화
		Orders currentOrder = orderRepository.findByMerchantUid(merchantUid)
			.orElseThrow(() -> new OrderException(OrderErrorCode.ORDER_NOT_FOUND));

		// Payment 테이블에 저장할 User 객체
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

		// 주문한 상품들에 대해 각각 결제내역 저장
		Payments savePayments = createPaymentHistory(user, totalPrice, PaymentStatus.READY, impUid);
		currentOrder.setPayments(savePayments);

		// 1차 검증
		validatedAmount(totalPrice, iamportPayment.getAmount().longValue(), payment, savePayments);

		// 2차 검증
		validatedAmount(totalPrice, currentOrder.getTotalPrice(), payment, savePayments);

		currentOrder.setOrderStatus(OrderStatus.COMPLETED);

		savePayments.changePaymentBySuccess(PaymentStatus.PAID);

	}

	// 결제내역 테이블 저장하는 메서드
	private Payments createPaymentHistory(User user, Long totalPrice, PaymentStatus paymentStatus,
		String impUid) {
		Payments payments = new Payments(user, totalPrice, impUid, paymentStatus, LocalDate.now());
		Payments savePayments = paymentRepository.save(payments);

		return savePayments;
	}

	//
	private void validatedAmount(Long totalPrice, Long amount, IamportResponse<Payment> payment,
		Payments savePayments) throws IamportResponseException, IOException {
		if (!totalPrice.equals(amount)) {
			savePayments.changePaymentBySuccess(PaymentStatus.FAILED);

			CancelData cancelData = createCancelData(payment, FULL_REFUND);

			iamportClient.cancelPaymentByImpUid(cancelData);

			throw new PaymentException(PaymentErrorCode.PAYMENT_AMOUNT_MISMATCH);
		}
	}

	@Transactional
	public void cancelReservation(PaymentRequestDto.Cancel cancelReq) throws IamportResponseException, IOException {

		Orders currentOrder = orderRepository.findByMerchantUid(cancelReq.getMerchantUid())
			.orElseThrow(() -> new OrderException(OrderErrorCode.ORDER_NOT_FOUND));

		IamportResponse<Payment> response = iamportClient.paymentByImpUid(currentOrder.getPayments().getImpUid());

		//cancelData 생성
		CancelData cancelData = createCancelData(response, cancelReq.getRefundAmount());

		Payments payments = paymentRepository.findByImpUid(currentOrder.getPayments().getImpUid())
			.orElseThrow(() -> new PaymentException(PaymentErrorCode.PAYMENT_NOT_FOUND));

		//결제 취소
		iamportClient.cancelPaymentByImpUid(cancelData);

		payments.changePaymentBySuccess(PaymentStatus.REFUNDED);

		currentOrder.setOrderStatus(OrderStatus.REFUNDED);

	}

	private CancelData createCancelData(IamportResponse<Payment> payment, int refundAmount) {
		if (refundAmount == 0) { //전액 환불일 경우
			return new CancelData(payment.getResponse().getImpUid(), true);
		}
		//부분 환불일 경우 checksum을 입력해 준다.
		return new CancelData(payment.getResponse().getImpUid(), true, new BigDecimal(refundAmount));

	}

}
