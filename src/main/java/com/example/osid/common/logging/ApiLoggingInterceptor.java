package com.example.osid.common.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.example.osid.common.auth.CustomUserDetails;
import com.example.osid.common.entity.enums.Role;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class ApiLoggingInterceptor implements HandlerInterceptor {

	private static final Logger logger = LoggerFactory.getLogger(ApiLoggingInterceptor.class);

	@Override
	public boolean preHandle(HttpServletRequest request,
		HttpServletResponse response,
		Object handler) throws Exception {
		String uri = request.getRequestURI();   // ex) "/api/orders"
		String method = request.getMethod();       // ex) "POST"

		Long userId = null;
		String email = null;
		Role role = null;

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null && auth.isAuthenticated()
			&& auth.getPrincipal() instanceof CustomUserDetails) {

			CustomUserDetails user = (CustomUserDetails)auth.getPrincipal();
			userId = user.getId();
			email = user.getUsername();  // CustomUserDetails.getUsername() → email

			// 권한(GrantedAuthority)에서 ROLE_ 접두사 제거 후 enum 변환
			String authority = user.getAuthorities().stream()
				.findFirst()
				.map(a -> a.getAuthority().replace("ROLE_", ""))
				.orElse(null);
			try {
				role = authority != null
					? Role.valueOf(authority)
					: null;
			} catch (IllegalArgumentException e) {
				logger.warn("Unknown role in authorities: {}", authority);
			}
		}

		// key=value 형태로 로깅
		logger.info("event=API_REQUEST userId={} email={} role={} method={} uri={}",
			userId, email, role, method, uri);

		return true;
	}
}
