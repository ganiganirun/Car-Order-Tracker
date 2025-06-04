package com.example.osid.common.auth;

import org.springframework.stereotype.Component;

import com.example.osid.common.exception.CustomException;
import com.example.osid.common.exception.ErrorCode;
import com.example.osid.domain.dealer.repository.DealerRepository;
import com.example.osid.domain.master.repository.MasterRepository;
import com.example.osid.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EmailValidator {
	private final DealerRepository dealerRepository;
	private final UserRepository userRepository;
	private final MasterRepository masterRepository;

	// 이메일 중복 시 바로 예외 던짐
	public void validateDuplicateEmail(String email) {
		boolean exists = dealerRepository.findByEmail(email).isPresent()
			|| userRepository.findByEmail(email).isPresent()
			|| masterRepository.findByEmail(email).isPresent();

		if (exists) {
			throw new CustomException(ErrorCode.EMAIL_ALREADY_EXISTS);
		}
	}
}
