package com.example.osid.common.auth.authorization;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
				// 1) 로그인·회원가입은 무조건 풀어두기
				.requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
				.requestMatchers(HttpMethod.POST, "/api/users/signup").permitAll()         // ← User 회원가입
				.requestMatchers(HttpMethod.POST, "/api/masters/signup").permitAll()       // ← Master 회원가입
				.requestMatchers(HttpMethod.POST, "/api/dealers/signup").permitAll()       // ← Dealer 회원가입
				// 2) 각 역할별 엔드포인트
				.requestMatchers("/api/masters/**").hasRole("MASTER")
				.requestMatchers("/api/dealers/**").hasRole("DEALER")
				.requestMatchers("/api/users/**").hasRole("USER")
				// 3) 그 외는 인증만 있으면 OK
				.anyRequest().authenticated()
			)
			// JWT 필터 등록
			.addFilterBefore(
				jwtAuthenticationFilter,
				UsernamePasswordAuthenticationFilter.class
			);

		return http.build();
	}

}
