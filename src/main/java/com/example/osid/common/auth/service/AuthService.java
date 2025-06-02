package com.example.osid.common.auth.service;

import org.springframework.beans.factory.annotation.Autowired;
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

@Service
public class AuthService {
	private final UserRepository userRepository;
	private final MasterRepository masterRepository;
	private final DealerRepository dealerRepository;
	private final JwtUtil jwtUtil;

	@Autowired
	public AuthService(UserRepository userRepository, MasterRepository masterRepository,
		DealerRepository dealerRepository, JwtUtil jwtUtil) {
		this.userRepository = userRepository;
		this.masterRepository = masterRepository;
		this.dealerRepository = dealerRepository;
		this.jwtUtil = jwtUtil;
	}

	public String login(LoginRequestDto loginRequestDto) {
		// 이메일로 유저 찾기
		String email = loginRequestDto.getEmail();

		// User, Master, Dealer 모두 체크
		User user = userRepository.findByEmail(email).orElse(null);
		Master master = masterRepository.findByEmail(email).orElse(null);
		Dealer dealer = dealerRepository.findByEmail(email).orElse(null);

		// 각각의 존재 여부를 따로 체크하고 예외를 던지기
		if (user == null && master == null && dealer == null) {
			throw new CustomException(ErrorCode.USER_NOT_FOUND); // 모든 테이블에 없는 경우
		}

		// 비밀번호 확인
		String password = loginRequestDto.getPassword();

		if (user != null && user.getPassword().equals(password)) {
			// JWT 토큰 생성 후 반환 (User)
			return jwtUtil.createToken(email, user.getName(), "USER", user.getId());
		} else if (master != null && master.getPassword().equals(password)) {
			// JWT 토큰 생성 후 반환 (Master)
			return jwtUtil.createToken(email, master.getName(), "MASTER", master.getId());
		} else if (dealer != null && dealer.getPassword().equals(password)) {
			// JWT 토큰 생성 후 반환 (Dealer)
			return jwtUtil.createToken(email, dealer.getName(), "DEALER", dealer.getId());
		} else if (master != null) {
			throw new MasterException(MasterErrorCode.MASTER_INVALID_PASSWORD);  // 마스터 비밀번호가 틀림
		} else if (dealer != null) {
			throw new DealerException(DealerErrorCode.DEALER_INVALID_PASSWORD);  // 딜러 비밀번호가 틀림
		} else if (user != null) {
			throw new UserException(UserErrorCode.USER_INVALID_PASSWORD);  // 유저 비밀번호가 틀림
		}

		return null;  // 이 줄은 사실상 도달하지 않음
	}
}
