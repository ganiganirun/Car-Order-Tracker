package com.example.osid.domain.license.util;

import java.security.SecureRandom;
import java.util.Base64;

import org.springframework.stereotype.Component;

@Component
public class LicenseKeyGenerator {

	private final SecureRandom secureRandom = new SecureRandom();

	// 24바이트 랜덤 생성 후 Base64 URL-safe 인코딩
	public String generateKey() {
		byte[] bytes = new byte[24];
		secureRandom.nextBytes(bytes);
		return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
	}

}
