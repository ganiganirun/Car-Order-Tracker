package com.example.osid.domain.email.service;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.example.osid.domain.dealer.entity.Dealer;
import com.example.osid.domain.email.EmailTemplateProcessor;
import com.example.osid.domain.order.entity.Orders;
import com.example.osid.domain.order.exception.OrderErrorCode;
import com.example.osid.domain.order.exception.OrderException;
import com.example.osid.domain.order.repository.OrderRepository;
import com.example.osid.domain.user.entity.User;
import com.example.osid.domain.counsel.entity.Counsel;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

	@Value("${EMAIL_ADDRESS}")
	private String fromAddress;

	private final JavaMailSender mailSender;
	private final OrderRepository orderRepository;

	public void sendOrderCompletedEmail(Long orderId) {

		Orders order = orderRepository.findWithOptionsById(orderId)
			.orElseThrow(() -> new OrderException(OrderErrorCode.ORDER_NOT_FOUND));

		sendToUser(order);
		sendToDealer(order);

	}

	private void sendToUser(Orders order) {
		User user = order.getUser();

		Map<String, String> vars = new HashMap<>();
		vars.put("고객이름", user.getName());
		vars.put("bodyNumber", order.getBodyNumber());
		vars.put("modelName", order.getModel().getName());
		vars.put("registeredAt", order.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

		String html = EmailTemplateProcessor.loadTemplate("mailTemplate/OrderCompleteMailForUser.html", vars);
		String subject = "[OSID] 차량 등록이 완료되었습니다";

		sendHtmlEmail(user.getEmail(), subject, html);
	}

	private void sendToDealer(Orders order) {
		Dealer dealer = order.getDealer();
		User user = order.getUser();

		Map<String, String> vars = new HashMap<>();
		vars.put("딜러이름", dealer.getName());
		vars.put("고객이름", user.getName());
		vars.put("bodyNumber", order.getBodyNumber());
		vars.put("modelName", order.getModel().getName());
		vars.put("registeredAt", order.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

		String html = EmailTemplateProcessor.loadTemplate("mailTemplate/OrderCompleteMailForDealer.html", vars);
		String subject = "[OSID] 담당 고객 차량이 등록되었습니다";

		sendHtmlEmail(dealer.getEmail(), subject, html);
	}

	// 딜러에게 상담 신청 알림 메일 발송
	public void sendCounselNotificationToDealer(Counsel counsel) {
		// 딜러가 지정되지 않은 경우 메일 발송하지 않음
		if (counsel.getDealer() == null) {
			log.info("딜러가 지정되지 않아 상담 신청 알림 메일을 발송하지 않습니다. 상담 ID: {}", counsel.getId());
			return;
		}

		Dealer dealer = counsel.getDealer();
		User user = counsel.getUser();

		Map<String, String> vars = new HashMap<>();
		vars.put("딜러이름", dealer.getName());
		vars.put("고객이름", user.getName());
		vars.put("상담제목", counsel.getTitle());
		vars.put("상담내용", counsel.getUserContent());
		vars.put("신청일시", counsel.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

		String html = EmailTemplateProcessor.loadTemplate("mailTemplate/CounselNotificationForDealer.html", vars);
		String subject = "[OSID] 새로운 상담 신청이 접수되었습니다";

		log.info("딜러에게 상담 신청 알림 메일 발송: 딜러={}, 상담ID={}", dealer.getName(), counsel.getId());
		sendHtmlEmail(dealer.getEmail(), subject, html);
	}

	private void sendHtmlEmail(String to, String subject, String htmlContent) {
		try {
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

			helper.setTo(to);
			helper.setSubject(subject);
			helper.setText(htmlContent, true);
			helper.setFrom(fromAddress);
			mailSender.send(message);
		} catch (MessagingException e) {
			log.error("메일 전송 실패: {}", e.getMessage(), e);
			throw new RuntimeException("메일 전송 실패", e);
		}
	}
}
