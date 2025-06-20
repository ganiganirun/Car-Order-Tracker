package com.example.osid.common.auth.service;

import java.time.Instant;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.osid.common.auth.authentication.JwtUtil;
import com.example.osid.common.auth.dto.LoginRequestDto;
import com.example.osid.common.auth.dto.LoginResponseDto;
import com.example.osid.common.auth.entity.RefreshToken;
import com.example.osid.common.auth.repository.RefreshTokenRepository;
import com.example.osid.common.exception.CustomException;
import com.example.osid.common.exception.ErrorCode;
import com.example.osid.domain.dealer.entity.Dealer;
import com.example.osid.domain.dealer.exception.DealerException;
import com.example.osid.domain.dealer.repository.DealerRepository;
import com.example.osid.domain.master.entity.Master;
import com.example.osid.domain.master.exception.MasterException;
import com.example.osid.domain.master.repository.MasterRepository;
import com.example.osid.domain.user.entity.User;
import com.example.osid.domain.user.exception.UserException;
import com.example.osid.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final UserRepository userRepository;
	private final MasterRepository masterRepository;
	private final DealerRepository dealerRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;
	private final RefreshTokenRepository refreshTokenRepository;

	public LoginResponseDto login(LoginRequestDto loginRequestDto) {

		String loginEmail = loginRequestDto.getEmail();
		String rawPassword = loginRequestDto.getPassword();

		// 1) 활성 계정(isDeleted = false)만 조회
		User user = userRepository.findByEmailAndIsDeletedFalse(loginEmail).orElse(null);
		Master master = masterRepository.findByEmailAndIsDeletedFalse(loginEmail).orElse(null);
		Dealer dealer = dealerRepository.findByEmailAndIsDeletedFalse(loginEmail).orElse(null);

		// 2) 세 테이블 모두 null 이면 “존재하지 않음” 에러
		if (user == null && master == null && dealer == null) {
			throw new CustomException(ErrorCode.USER_NOT_FOUND);
		}

		// 2) 인증 주체 결정 및 공통 정보 추출
		String email;   // PK로 사용할 이메일
		String name;
		String role;
		String pwHash;
		Long id;

		if (user != null) {
			email = user.getEmail();
			name = user.getName();
			role = "USER";
			pwHash = user.getPassword();
			id = user.getId();
		} else if (master != null) {
			email = master.getEmail();
			name = master.getName();
			role = "MASTER";
			pwHash = master.getPassword();
			id = master.getId();
		} else {
			email = dealer.getEmail();
			name = dealer.getName();
			role = "DEALER";
			pwHash = dealer.getPassword();
			id = dealer.getId();
		}

		// 3) 비밀번호 검증
		if (!passwordEncoder.matches(rawPassword, pwHash)) {
			switch (role) {
				case "USER":
					throw new UserException(ErrorCode.INVALID_PASSWORD);
				case "MASTER":
					throw new MasterException(ErrorCode.INVALID_PASSWORD);
				default:
					throw new DealerException(ErrorCode.INVALID_PASSWORD);
			}
		}

		// 4) 토큰 생성
		String accessToken = jwtUtil.createToken(email, name, role, id);
		String refreshToken = jwtUtil.createRefreshToken(email, role, id);

		// 5) 리프레시 토큰 데이터베이스에 저장 (이메일이 PK)
		Instant now = Instant.now();
		Instant expiry = now.plusMillis(jwtUtil.getRefreshTokenExpTime());
		RefreshToken entity = new RefreshToken(
			email,        // PK
			refreshToken,
			role,
			now,
			expiry
		);
		refreshTokenRepository.save(entity);

		// 6) 로그인 응답 반환
		return new LoginResponseDto(accessToken, refreshToken);

	}

	public void logout(String refreshToken) {
		// 토큰 유효성 검사
		if (!jwtUtil.validateToken(refreshToken)) {
			throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
		}
		// 토큰에서 이메일 추출 후 DB 레코드 삭제
		String email = jwtUtil.extractEmail(refreshToken);
		refreshTokenRepository.deleteById(email);
	}

	public String refreshAccessToken(String refreshToken) {
		// 1) 리프레시 토큰 유효성 검사
		if (!jwtUtil.validateToken(refreshToken)) {
			throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
		}
		// 2) DB 일치 여부 확인
		String email = jwtUtil.extractEmail(refreshToken);
		RefreshToken saved = refreshTokenRepository.findById(email)
			.orElseThrow(() -> new CustomException(ErrorCode.REFRESH_TOKEN_NOT_FOUND));
		if (!saved.getToken().equals(refreshToken)) {
			throw new CustomException(ErrorCode.REFRESH_TOKEN_MISMATCH);
		}
		// 3) 새 액세스 토큰 발급
		String name = jwtUtil.extractName(saved.getToken());
		String role = jwtUtil.extractRole(saved.getToken());
		Long id = jwtUtil.extractId(saved.getToken());
		return jwtUtil.createToken(email, name, role, id);
	}
}
