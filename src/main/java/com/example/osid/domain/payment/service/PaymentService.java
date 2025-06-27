package com.example.osid.domain.payment.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.osid.domain.order.dto.OrderPaidEvent;
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
import com.example.osid.domain.waitingorder.entity.WaitingOrders;
import com.example.osid.domain.waitingorder.enums.WaitingStatus;
import com.example.osid.domain.waitingorder.exception.WaitingOrderErrorCode;
import com.example.osid.domain.waitingorder.exception.WaitingOrderException;
import com.example.osid.domain.waitingorder.repository.WaitingOrderRepository;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.CancelData;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

	private final OrderRepository orderRepository;
	private final PaymentRepository paymentRepository;
	private final UserRepository userRepository;
	private final ApplicationEventPublisher eventPublisher;
	private final WaitingOrderRepository waitingOrderRepository;

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

	// 결제 성공 처리
	@Transactional("dataTransactionManager")
	public void processPaymentDone(PaymentRequestDto.Paid request, IamportResponse<Payment> payment) throws
		IamportResponseException,
		IOException {

		Payment iamportPayment = payment.getResponse();
		String impUid = request.getImpUid();
		String merchantUid = request.getMerchantUid();
		Long userId = request.getUserId();
		Long totalPrice = request.getAmount();

		// 이미 Paid 처리된 impUid 경우 바로 리턴(멱등성 체크)
		if (paymentRepository.existsByImpUidAndPaymentStatus(request.getImpUid(), PaymentStatus.PAID)) {
			log.info("duplicate callback ignored: {}", request.getImpUid());
			return;
		}

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

		eventPublisher.publishEvent(new OrderPaidEvent(currentOrder.getId()));

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

			// iamportClient.cancelPaymentByImpUid(cancelData);

			safelyCancelPayment(payment, FULL_REFUND, savePayments);

			throw new PaymentException(PaymentErrorCode.PAYMENT_AMOUNT_MISMATCH);
		}
	}

	// 결제 환불
	@Transactional("dataTransactionManager")
	public void cancelReservation(PaymentRequestDto.Cancel cancelReq) throws IamportResponseException, IOException {

		Orders currentOrder = orderRepository.findByMerchantUid(cancelReq.getMerchantUid())
			.orElseThrow(() -> new OrderException(OrderErrorCode.ORDER_NOT_FOUND));

		WaitingOrders waitingOrders = waitingOrderRepository.findByOrders(currentOrder)
			.orElseThrow(() -> new WaitingOrderException(WaitingOrderErrorCode.WAITING_ORDER_NOT_FOUND));

		// 대기열의 상태가 Waiting이 아니면 주문 취소 불가능
		if (!waitingOrders.getWaitingStatus().equals(WaitingStatus.WAITING)) {
			throw new OrderException(OrderErrorCode.ORDER_CANCELLATION_NOT_ALLOWED);
		}

		IamportResponse<Payment> response = iamportClient.paymentByImpUid(currentOrder.getPayments().getImpUid());

		Payment iamportPayment = response.getResponse();

		// 두 값을 비교해서 앞이 크면 1 같으면 0 작으면 -1 반환
		// 환불 금액이 결제 금액보다 큰 값이 들어오는 것을 방지하기 위함
		if (BigDecimal.valueOf(cancelReq.getRefundAmount())
			.compareTo(iamportPayment.getAmount()) > 0) {
			throw new PaymentException(PaymentErrorCode.PAYMENT_REFUND_TOO_LARGE);
		}

		//cancelData 생성
		CancelData cancelData = createCancelData(response, cancelReq.getRefundAmount());

		Payments payments = paymentRepository.findByImpUid(currentOrder.getPayments().getImpUid())
			.orElseThrow(() -> new PaymentException(PaymentErrorCode.PAYMENT_NOT_FOUND));

		//결제 취소
		// iamportClient.cancelPaymentByImpUid(cancelData);
		safelyCancelPayment(response, cancelReq.getRefundAmount(), payments);

		payments.changePaymentBySuccess(PaymentStatus.REFUNDED);

		currentOrder.setOrderStatus(OrderStatus.REFUNDED);

		waitingOrderRepository.delete(waitingOrders);

	}

	// 취소 데이터 생성
	private CancelData createCancelData(IamportResponse<Payment> payment, int refundAmount) {
		if (refundAmount == 0) { //전액 환불일 경우
			return new CancelData(payment.getResponse().getImpUid(), true);
		}
		//부분 환불일 경우 checksum을 입력해 준다.
		return new CancelData(payment.getResponse().getImpUid(), true, new BigDecimal(refundAmount));

	}

	// 환불 실패 대비
	private void safelyCancelPayment(IamportResponse<Payment> payment, int refundAmount, Payments payments) {
		try {
			iamportClient.cancelPaymentByImpUid(createCancelData(payment, refundAmount));
		} catch (IamportResponseException | IOException ex) {
			log.error("아임포트 취소 실패 impUid={}, refund={}, msg={}",
				payment, refundAmount, ex.getMessage(), ex);

			// 실패 시 결제내역에 수동처리 플래그 기록(운영자 개입 필요)
			payments.changePaymentBySuccess(PaymentStatus.MANUAL_REVIEW);
			return;
		}
	}

	// public Page<PaymentResponseDto.ManualReview> findManaulReview(Pageable pageable) {
	//
	// 	Page<Payments> payments = paymentRepository.findbyPaymentStatus(pageable, PaymentStatus.MANUAL_REVIEW);
	//
	// 	return payments
	// 		.stream()
	// 		.map(payment ->
	// 			new PaymentResponseDto(
	// 				payment.getUser().getId(),
	// 				payment.getAmount(),
	// 				orderRepository.findByPayments(payment)
	// 					.orElseThrow(() -> new OrderException(OrderErrorCode.ORDER_NOT_FOUND))
	// 			));
	// }

}
