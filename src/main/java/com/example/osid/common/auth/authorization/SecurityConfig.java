package com.example.osid.common.auth.authorization;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.osid.common.auth.authentication.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {

	private final JwtAuthenticationFilter jwtAuthenticationFilter;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.csrf(csrf -> csrf.disable())
			.sessionManagement(sm ->
				sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			)
			.authorizeHttpRequests(auth -> auth
				// 1) 모두 가능
				.requestMatchers("/payment-test.html")
				.permitAll()
				.requestMatchers("/api/my-ml-predict")
				.permitAll()
				.requestMatchers(HttpMethod.POST, "/api/auth/login")
				.permitAll()
				.requestMatchers(HttpMethod.POST, "/api/users/signup")
				.permitAll()        // ← User 회원가입
				.requestMatchers(HttpMethod.POST, "/api/masters/signup")
				.permitAll()      // ← Master 회원가입
				.requestMatchers(HttpMethod.POST, "/api/dealers/signup")
				.permitAll()      // ← Dealer 회원가입
				.requestMatchers(HttpMethod.GET, "/api/model/**")
				.permitAll()            // 차량 모델 조회(단건/전체) → 모두 허용
				.requestMatchers(HttpMethod.GET, "/api/option/**")
				.permitAll()            // 차량 옵션 조회(단건/전체) → 모두 허용
				// 2) Master
				.requestMatchers("/api/masters/**")
				.hasRole("MASTER")
				.requestMatchers("/api/model/**")
				.hasRole("MASTER")                        // Model 생성, 수정, 삭제 → 마스터 허용
				.requestMatchers("/api/option/**")
				.hasRole("MASTER")                        // Option 생성, 수정, 삭제 → 마스터 허용
				.requestMatchers(HttpMethod.PATCH, "/api/dealers/role")
				.hasRole("MASTER")                          // Dealer Role 수정
				.requestMatchers(HttpMethod.PATCH, "/api/dealers/branch")
				.hasRole("MASTER")                          // Dealer branch 수정
				// 3) Dealer
				.requestMatchers("/api/dealers/**")
				.hasRole("DEALER")

				// 4) User
				.requestMatchers("/api/users/**")
				.hasRole("USER")
				.requestMatchers("/api/myCar/**")
				.hasRole("USER")
				// 3) 그 외는 인증만 있으면 OK
				.anyRequest()
				.authenticated()
			)
			// JWT 필터 등록
			.addFilterBefore(
				jwtAuthenticationFilter,
				UsernamePasswordAuthenticationFilter.class
			);

		return http.build();
	}

}
