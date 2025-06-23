package com.example.osid.domain.payment.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.osid.common.response.CommonResponse;
import com.example.osid.domain.payment.dto.PaymentRequestDto;
import com.example.osid.domain.payment.service.PaymentService;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("api/payment")
public class PaymentController {

	@Value("${IMP_API_KEY}")
	private String apiKey;

	@Value("${imp.api.secretkey}")
	private String secretKey;

	private IamportClient iamportClient;

	private final PaymentService paymentService;

	@PostConstruct
	public void init() {
		this.iamportClient = new IamportClient(apiKey, secretKey);
	}

	@PostMapping("/{imp_uid}")
	public IamportResponse<Payment> validateIamport(@PathVariable("imp_uid") String impUid,
		@RequestBody PaymentRequestDto.Paid request) throws IamportResponseException, IOException {

		IamportResponse<Payment> payment = iamportClient.paymentByImpUid(impUid);

		log.info("결제 요청 응답. 결제 내역 - 주문 번호: {}", payment.getResponse().getMerchantUid());

		// 결제 완료 처리 시, merchantUid로 주문 조회해서 연관관계 연결 가능
		paymentService.processPaymentDone(request, payment);

		return payment;
	}

	@PostMapping("/cancel")
	public CommonResponse<Void> createPayment(@RequestBody PaymentRequestDto.Cancel cancelReq) throws
		IamportResponseException,
		IOException {
		paymentService.cancelReservation(cancelReq);
		return CommonResponse.ok();
	}

	// @GetMapping("/manualreview")
	// public CommonResponse<PaymentResponseDto.ManualReview> findManualReview(
	// 	@PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
	// ) {
	// 	paymentService.findManaulReview(pageable);
	// }

}

