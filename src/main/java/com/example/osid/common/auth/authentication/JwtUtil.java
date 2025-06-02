package com.example.osid.common.auth.authentication;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtUtil {

	@Value("${jwt.secret}")
	private String SECRET_KEY; // 토큰을 생성할 때 사용할 비밀키

	private long TOKEN_TIME = 1000 * 60 * 60;

	// JWT TOKEN 생성
	public String createToken(String email, String name, String role, Long id) {
		return Jwts.builder()
			.setSubject(email)  // JWT의 Subject에 이메일 저장
			.claim("id", id)
			.claim("name", name)  // 이름을 claim에 추가
			.claim("role", role)  // 역할을 claim에 추가
			.setIssuedAt(new Date())  // 발급 시간
			.setExpiration(new Date(System.currentTimeMillis() + TOKEN_TIME))  // 만료 시간
			.signWith(SignatureAlgorithm.HS256, SECRET_KEY)  // HS256 알고리즘과 비밀키로 서명
			.compact();
	}

	// Claims 객체 추출
	private Claims extractAllClaims(String token) {
		return Jwts.parserBuilder()
			.setSigningKey(SECRET_KEY)
			.build()
			.parseClaimsJws(token)
			.getBody();
	}

	// 이메일(Subject) 추출
	public String extractEmail(String token) {
		return extractAllClaims(token).getSubject();
	}

	// 이름 추출
	public String extractName(String token) {
		return extractAllClaims(token).get("name", String.class);
	}

	// 역할 추출
	public String extractRole(String token) {
		return extractAllClaims(token).get("role", String.class);
	}

	// 만료 시간 추출
	public Date extractExpiration(String token) {
		return extractAllClaims(token).getExpiration();
	}

	// 만료 여부 확인
	public boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}

	// 토큰 유효성 검증 (토큰 내 이메일과 만료 여부)
	public boolean validateToken(String token) {
		try {
			final String email = extractEmail(token);
			return (email != null && !isTokenExpired(token));
		} catch (Exception e) {
			return false;
		}
	}
}
