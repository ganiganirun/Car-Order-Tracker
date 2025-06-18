package com.example.osid.domain.email.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.osid.common.auth.CustomUserDetails;
import com.example.osid.domain.email.service.EmailService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/mail")
@AllArgsConstructor
public class EmailController {

	private final EmailService emailService;

	@GetMapping("/test")
	public String sendTestMail(@AuthenticationPrincipal CustomUserDetails customUserDetails) {

		String email = customUserDetails.getEmail();
		emailService.sendOrderCompletedEmail(1L);
		return "메일 전송 완료";
	}
}
