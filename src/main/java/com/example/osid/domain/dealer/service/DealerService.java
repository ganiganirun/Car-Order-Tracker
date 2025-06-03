package com.example.osid.domain.dealer.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.osid.common.auth.CustomUserDetails;
import com.example.osid.common.entity.enums.Role;
import com.example.osid.domain.dealer.dto.request.DealerDeletedRequestDto;
import com.example.osid.domain.dealer.dto.request.DealerRoleChangeRequestDto;
import com.example.osid.domain.dealer.dto.request.DealerSignUpRequestDto;
import com.example.osid.domain.dealer.dto.request.DealerUpdatedRequestDto;
import com.example.osid.domain.dealer.dto.response.FindByDealerResponseDto;
import com.example.osid.domain.dealer.entity.Dealer;
import com.example.osid.domain.dealer.exception.DealerErrorCode;
import com.example.osid.domain.dealer.exception.DealerException;
import com.example.osid.domain.dealer.repository.DealerRepository;
import com.example.osid.domain.master.entity.Master;
import com.example.osid.domain.master.exception.MasterErrorCode;
import com.example.osid.domain.master.exception.MasterException;
import com.example.osid.domain.master.repository.MasterRepository;
import com.example.osid.domain.user.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DealerService {

	private final DealerRepository dealerRepository;
	private final MasterRepository masterRepository;
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public void signUpDealer(DealerSignUpRequestDto dealerSignUpRequestDto) {
		if (dealerRepository.findByEmail(dealerSignUpRequestDto.getEmail()).isPresent()) {
			throw new DealerException(DealerErrorCode.EMAIL_ALREADY_EXISTS);
		}
		if (userRepository.findByEmail(dealerSignUpRequestDto.getEmail()).isPresent()) {
			throw new DealerException(DealerErrorCode.EMAIL_ALREADY_EXISTS);
		}

		if (masterRepository.findByEmail(dealerSignUpRequestDto.getEmail()).isPresent()) {
			throw new DealerException(DealerErrorCode.EMAIL_ALREADY_EXISTS);
		}

		Master master = verifyActiveMaster(dealerSignUpRequestDto.getMasterEmail());

		String encodedPassword = passwordEncoder.encode(dealerSignUpRequestDto.getPassword());

		Dealer dealer = new Dealer(
			dealerSignUpRequestDto.getEmail(),
			encodedPassword,
			dealerSignUpRequestDto.getName(),
			dealerSignUpRequestDto.getPhoneNumber(),
			master
		);
		dealerRepository.save(dealer);
	}

	public FindByDealerResponseDto findByDealer(CustomUserDetails customUserDetails) {

		Dealer dealer = verifyDealeer(customUserDetails.getId());

		return new FindByDealerResponseDto(
			dealer.getId(),
			dealer.getEmail(),
			dealer.getName(),
			dealer.getPoint(),
			dealer.getPhoneNumber(),
			dealer.getMaster()
		);
	}

	@Transactional
	public void updatedDealer(
		CustomUserDetails customUserDetails,
		DealerUpdatedRequestDto dealerUpdatedRequestDto
	) {
		Dealer dealer = verifyActiveDealer(customUserDetails.getEmail());
		dealer.UpdatedDealer(dealerUpdatedRequestDto);
	}

	@Transactional
	public void deletedDealer(
		CustomUserDetails customUserDetails,
		DealerDeletedRequestDto dealerDeletedRequestDto
	) {
		Dealer dealer = verifyActiveDealer(customUserDetails.getEmail());

		String rawPassword = dealerDeletedRequestDto.getPassword();
		String storedHash = dealer.getPassword();

		if (!passwordEncoder.matches(rawPassword, storedHash)) {
			// 비밀번호가 불일치하면 예외 던짐
			throw new DealerException(DealerErrorCode.DEALER_INVALID_PASSWORD);
		}

		dealer.softDeletedDealer();
	}

	@Transactional
	public void updatedRoleChangeDealer(
		CustomUserDetails customUserDetails,
		DealerRoleChangeRequestDto dealerRoleChangeRequestDto
	) {
		Master master = verifyActiveMaster(customUserDetails.getEmail());

		Dealer dealer = verifyActiveDealer(dealerRoleChangeRequestDto.getDealerEmail());

		// Dealer가 실제로 이 Master 소속인지 확인
		if (!dealer.getMaster().getId().equals(master.getId())) {
			throw new DealerException(DealerErrorCode.DEALER_NOT_BELONG_TO_MASTER);
		}

		// String → Role enum 변환 & 예외 처리
		Role newRole;
		try {
			newRole = Role.valueOf(dealerRoleChangeRequestDto.getRole().toUpperCase());
		} catch (IllegalArgumentException e) {
			// Role enum에 없는 값이 들어왔을 때
			throw new DealerException(DealerErrorCode.INVALID_ROLE);
		}

		// 딜러에게 허용된 Role인지 검증: DEALER 또는 APPLICANT만 허용
		if (newRole != Role.DEALER && newRole != Role.APPLICANT) {
			throw new DealerException(DealerErrorCode.INVALID_ROLE);
		}

		// 역할 변경
		dealer.updateRole(newRole);
		// JPA 영속성 컨텍스트가 관리 중이므로 커밋 시 자동 저장됩니다.
	}

	private Dealer verifyDealeer(Long dealerId) {
		return dealerRepository.findById(dealerId)
			.orElseThrow(() -> new DealerException(DealerErrorCode.DEALER_NOT_FOUND));
	}

	private Master verifyActiveMaster(String email) {
		return masterRepository.findByEmailAndIsDeletedFalse(email)
			.orElseThrow(() -> new MasterException(MasterErrorCode.MASTER_NOT_FOUND));
	}

	private Dealer verifyActiveDealer(String email) {
		return dealerRepository.findByEmailAndIsDeletedFalse(email)
			.orElseThrow(() -> new DealerException(DealerErrorCode.DEALER_NOT_FOUND));
	}

}
