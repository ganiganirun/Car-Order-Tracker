package com.example.osid.common.auth;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.example.osid.domain.license.enums.LicenseStatus;
import com.example.osid.domain.license.repository.LicenseKeyRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LicenseValidationInterceptor implements HandlerInterceptor {

	private final LicenseKeyRepository licenseKeyRepository;

	@Override
	public boolean preHandle(HttpServletRequest request,
		HttpServletResponse response,
		Object handler) throws IOException {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || !auth.isAuthenticated()) {
			return true;  // 인증 전/비로그인 요청은 스킵
		}

		Object principal = auth.getPrincipal();
		if (principal instanceof CustomUserDetails user) {
			// 오직 MASTER에게만 라이선스 체크
			boolean isMaster = user.getAuthorities().stream()
				.anyMatch(a -> a.getAuthority().equals("ROLE_MASTER"));
			if (isMaster) {
				boolean hasValid = licenseKeyRepository
					.findByOwnerIdAndLicenseStatus(user.getId(), LicenseStatus.ASSIGNED)
					.isPresent();
				if (!hasValid) {
					response.sendError(HttpStatus.FORBIDDEN.value(),
						"라이선스가 없거나 취소되었습니다.");
					return false;
				}
			}
		}
		return true;  // DEALER/USER는 전부 통과
	}

}
