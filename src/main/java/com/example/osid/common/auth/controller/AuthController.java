package com.example.osid.common.auth.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.osid.common.auth.dto.LoginRequestDto;
import com.example.osid.common.auth.service.AuthService;
import com.example.osid.common.response.CommonResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;

	// 로그인 엔드포인트
	@PostMapping("/login")
	public CommonResponse<String> login(@RequestBody @Valid LoginRequestDto loginRequestDto) {
		// 로그인 처리 후 JWT 토큰을 생성하여 반환
		String token = authService.login(loginRequestDto);

		// CommonResponse로 결과 반환
		return CommonResponse.ok(token);
	}

}
