package com.example.osid.domain.email.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.example.osid.domain.order.entity.Orders;
import com.example.osid.domain.order.exception.OrderErrorCode;
import com.example.osid.domain.order.exception.OrderException;
import com.example.osid.domain.order.repository.OrderRepository;
import com.example.osid.domain.user.entity.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {

	private final JavaMailSender mailSender;
	private final OrderRepository orderRepository;

	public void sendOrderCompletedEmail(Long orderId) {
		SimpleMailMessage message = new SimpleMailMessage();
		Orders order = orderRepository.findById(orderId)
			.orElseThrow(() -> new OrderException(OrderErrorCode.ORDER_NOT_FOUND));
		User user = order.getUser();

		// 테스트용 임시 메일 제목&내용
		String subject = "[OSID] 고객님의 차량이 출고 되었습니다.";
		String body = "고객님의 차량이 출고 되었습니다.";

		message.setTo(user.getEmail());
		message.setSubject(subject);
		message.setText(body);
		message.setFrom("pr924133@gmail.com"); //테스트용 이메일(필수 아님)
		mailSender.send(message);
	}
}
