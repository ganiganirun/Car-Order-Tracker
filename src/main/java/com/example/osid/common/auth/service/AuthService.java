package com.example.osid.common.auth.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.osid.common.auth.authentication.JwtUtil;
import com.example.osid.common.auth.dto.LoginRequestDto;
import com.example.osid.common.exception.CustomException;
import com.example.osid.common.exception.ErrorCode;
import com.example.osid.domain.dealer.entity.Dealer;
import com.example.osid.domain.dealer.exception.DealerErrorCode;
import com.example.osid.domain.dealer.exception.DealerException;
import com.example.osid.domain.dealer.repository.DealerRepository;
import com.example.osid.domain.master.entity.Master;
import com.example.osid.domain.master.exception.MasterErrorCode;
import com.example.osid.domain.master.exception.MasterException;
import com.example.osid.domain.master.repository.MasterRepository;
import com.example.osid.domain.user.entity.User;
import com.example.osid.domain.user.exception.UserErrorCode;
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

	public String login(LoginRequestDto loginRequestDto) {

		String email = loginRequestDto.getEmail();
		String rawPassword = loginRequestDto.getPassword();

		// 1) 활성 계정(isDeleted = false)만 조회
		User user = userRepository.findByEmailAndIsDeletedFalse(email).orElse(null);
		Master master = masterRepository.findByEmailAndIsDeletedFalse(email).orElse(null);
		Dealer dealer = dealerRepository.findByEmailAndIsDeletedFalse(email).orElse(null);

		// 2) 세 테이블 모두 null 이면 “존재하지 않음” 에러
		if (user == null && master == null && dealer == null) {
			throw new CustomException(ErrorCode.USER_NOT_FOUND);
		}

		// 3) user 인증
		if (user != null) {
			if (passwordEncoder.matches(rawPassword, user.getPassword())) {
				return jwtUtil.createToken(email, user.getName(), "USER", user.getId());
			} else {
				throw new UserException(UserErrorCode.USER_INVALID_PASSWORD);
			}
		}

		// 4) master 인증
		if (master != null) {
			if (passwordEncoder.matches(rawPassword, master.getPassword())) {
				return jwtUtil.createToken(email, master.getName(), "MASTER", master.getId());
			} else {
				throw new MasterException(MasterErrorCode.MASTER_INVALID_PASSWORD);
			}
		}

		// 5) dealer 인증 (user, master가 null이면 dealer만 남음)
		if (dealer != null) {
			if (passwordEncoder.matches(rawPassword, dealer.getPassword())) {
				return jwtUtil.createToken(email, dealer.getName(), "DEALER", dealer.getId());
			} else {
				throw new DealerException(DealerErrorCode.DEALER_INVALID_PASSWORD);
			}
		}

		// 도달하지 않음
		return null;
	}
}
