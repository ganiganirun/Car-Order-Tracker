package com.example.osid.domain.user.service;

import org.springframework.stereotype.Service;

import com.example.osid.common.auth.CustomUserDetails;
import com.example.osid.domain.dealer.repository.DealerRepository;
import com.example.osid.domain.master.repository.MasterRepository;
import com.example.osid.domain.user.dto.request.UserSignUpRequestDto;
import com.example.osid.domain.user.dto.request.UserUpdatedRequestDto;
import com.example.osid.domain.user.dto.response.FindbyUserResponseDto;
import com.example.osid.domain.user.entity.User;
import com.example.osid.domain.user.exception.UserErrorCode;
import com.example.osid.domain.user.exception.UserException;
import com.example.osid.domain.user.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final DealerRepository dealerRepository;
	private final MasterRepository masterRepository;

	public void signUpUser(UserSignUpRequestDto userSignUpRequestDto) {

		if (userRepository.findByEmail(userSignUpRequestDto.getEmail()).isPresent()) {
			throw new UserException(UserErrorCode.EMAIL_ALREADY_EXISTS);
		}
		if (dealerRepository.findByEmail(userSignUpRequestDto.getEmail()).isPresent()) {
			throw new UserException(UserErrorCode.EMAIL_ALREADY_EXISTS);
		}

		if (masterRepository.findByEmail(userSignUpRequestDto.getEmail()).isPresent()) {
			throw new UserException(UserErrorCode.EMAIL_ALREADY_EXISTS);
		}
		User user = new User(
			userSignUpRequestDto.getEmail(),
			userSignUpRequestDto.getPassword(),
			userSignUpRequestDto.getName(),
			userSignUpRequestDto.getDateOfBirth(),
			userSignUpRequestDto.getPhoneNumber(),
			userSignUpRequestDto.getAddress()
		);

		userRepository.save(user);
	}

	public FindbyUserResponseDto findbyUser(CustomUserDetails customUserDetails) {
		User user = verifyUser(customUserDetails.getId());

		return new FindbyUserResponseDto(
			user.getId(),
			user.getEmail(),
			user.getName(),
			user.getDateOfBirth(),
			user.getPhoneNumber(),
			user.getAddress()
		);
	}

	@Transactional
	public void updatedUser(
		CustomUserDetails customUserDetails,
		UserUpdatedRequestDto userUpdatedRequestDto
	) {
		User user = verifyUser(customUserDetails.getId());
		user.UpdatedUser(userUpdatedRequestDto);
	}

	private User verifyUser(Long userId) {
		return userRepository.findById(userId)
			.orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
	}

}
