package com.example.osid.domain.user.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.osid.common.auth.CustomUserDetails;
import com.example.osid.common.auth.EmailValidator;
import com.example.osid.domain.dealer.repository.DealerRepository;
import com.example.osid.domain.master.repository.MasterRepository;
import com.example.osid.domain.user.dto.request.UserDeletedRequestDto;
import com.example.osid.domain.user.dto.request.UserSignUpRequestDto;
import com.example.osid.domain.user.dto.request.UserUpdatedRequestDto;
import com.example.osid.domain.user.dto.response.FindbyUserResponseDto;
import com.example.osid.domain.user.entity.User;
import com.example.osid.domain.user.exception.UserErrorCode;
import com.example.osid.domain.user.exception.UserException;
import com.example.osid.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final DealerRepository dealerRepository;
	private final MasterRepository masterRepository;
	private final PasswordEncoder passwordEncoder;
	private final EmailValidator emailValidator;

	public void signUpUser(UserSignUpRequestDto userSignUpRequestDto) {

		// 공통된 이메일이 있는지 확인 ( Master, Dealer, User )
		emailValidator.validateDuplicateEmail(userSignUpRequestDto.getEmail());

		String encodedPassword = passwordEncoder.encode(userSignUpRequestDto.getPassword());

		// User user = new User(
		// 	userSignUpRequestDto.getEmail(),
		// 	encodedPassword,
		// 	userSignUpRequestDto.getName(),
		// 	userSignUpRequestDto.getDateOfBirth(),
		// 	userSignUpRequestDto.getPhoneNumber(),
		// 	userSignUpRequestDto.getAddress()
		// );

		User user = User.builder()
			.email(userSignUpRequestDto.getEmail())
			.password(encodedPassword)
			.name(userSignUpRequestDto.getName())
			.dateOfBirth(userSignUpRequestDto.getDateOfBirth())
			.phoneNumber(userSignUpRequestDto.getPhoneNumber())
			.address(userSignUpRequestDto.getAddress())
			.build();

		userRepository.save(user);
	}

	public FindbyUserResponseDto findbyUser(CustomUserDetails customUserDetails) {
		User user = verifyActiveUser(customUserDetails.getEmail());

		return new FindbyUserResponseDto(
			user.getId(),
			user.getEmail(),
			user.getName(),
			user.getDateOfBirth(),
			user.getPhoneNumber(),
			user.getAddress()
		);
	}

	@Transactional("dataTransactionManager")
	public void updatedUser(
		CustomUserDetails customUserDetails,
		UserUpdatedRequestDto userUpdatedRequestDto
	) {
		User user = verifyUser(customUserDetails.getId());
		user.updatedUser(userUpdatedRequestDto);
	}

	@Transactional("dataTransactionManager")
	public void deletedUser(
		CustomUserDetails customUserDetails,
		UserDeletedRequestDto userDeletedRequestDto
	) {
		User user = verifyActiveUser(customUserDetails.getEmail());

		String rawPassword = userDeletedRequestDto.getPassword();
		String storedHash = user.getPassword();

		if (!passwordEncoder.matches(rawPassword, storedHash)) {
			// 비밀번호가 불일치하면 예외 던짐
			throw new UserException(UserErrorCode.USER_INVALID_PASSWORD);
		}

		user.softDeletedUser();
	}

	private User verifyUser(Long userId) {
		return userRepository.findById(userId)
			.orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
	}

	private User verifyActiveUser(String email) {
		return userRepository.findByEmailAndIsDeletedFalse(email)
			.orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
	}

}
