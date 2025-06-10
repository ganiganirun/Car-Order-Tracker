package com.example.osid.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.example.osid.common.auth.LicenseValidationInterceptor;
import com.example.osid.common.logging.ApiLoggingInterceptor;

import lombok.RequiredArgsConstructor;

@Configuration  // 스프링 설정 클래스로 등록
@RequiredArgsConstructor  // final 필드를 생성자 주입하도록 롬복 처리
public class WebConfig implements WebMvcConfigurer {

	private final ApiLoggingInterceptor apiLoggingInterceptor;  // 로깅 인터셉터
	private final LicenseValidationInterceptor licenseValidationInterceptor;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		// InterceptorRegistry에 ApiLoggingInterceptor를 등록
		registry.addInterceptor(apiLoggingInterceptor)
			// 적용할 URL 패턴: /api/** 모든 API 요청
			.addPathPatterns("/api/**")
			// 제외할 URL 패턴: 로그인/회원가입 등 토큰 없이 접근해야 하는 엔드포인트
			.excludePathPatterns(
				"/api/auth/login",      // 로그인 엔드포인트
				"/api/masters/signup",  // Master 회원가입
				"/api/dealers/signup",  // Dealer 회원가입
				"/api/users/signup"     // User 회원가입
			);
		// 라이선스 유효성 검증
		registry.addInterceptor(licenseValidationInterceptor)
			.addPathPatterns("/api/**")
			.excludePathPatterns(
				"/api/auth/login",
				"/api/masters/signup",
				"/api/dealers/signup",  // Dealer 회원가입
				"/api/users/signup"     // User 회원가입
			); // 필요에 따라 제외 경로 조정
	}
}
