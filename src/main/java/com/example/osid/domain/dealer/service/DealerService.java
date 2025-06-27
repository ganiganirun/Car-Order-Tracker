package com.example.osid.domain.dealer.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.osid.common.auth.CustomUserDetails;
import com.example.osid.common.auth.EmailValidator;
import com.example.osid.common.entity.enums.Role;
import com.example.osid.domain.dealer.dto.request.DealerBranchChangeRequestDto;
import com.example.osid.domain.dealer.dto.request.DealerDeletedRequestDto;
import com.example.osid.domain.dealer.dto.request.DealerRoleChangeRequestDto;
import com.example.osid.domain.dealer.dto.request.DealerSignUpRequestDto;
import com.example.osid.domain.dealer.dto.request.DealerUpdatedRequestDto;
import com.example.osid.domain.dealer.dto.response.FindByDealerResponseDto;
import com.example.osid.domain.dealer.entity.Dealer;
import com.example.osid.domain.dealer.enums.Branch;
import com.example.osid.domain.dealer.exception.DealerErrorCode;
import com.example.osid.domain.dealer.exception.DealerException;
import com.example.osid.domain.dealer.repository.DealerRepository;
import com.example.osid.domain.master.entity.Master;
import com.example.osid.domain.master.exception.MasterErrorCode;
import com.example.osid.domain.master.exception.MasterException;
import com.example.osid.domain.master.repository.MasterRepository;
import com.example.osid.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DealerService {

	private final DealerRepository dealerRepository;
	private final MasterRepository masterRepository;
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final EmailValidator emailValidator;

	public void signUpDealer(DealerSignUpRequestDto dealerSignUpRequestDto) {

		// 공통된 이메일이 있는지 확인 ( Master, Dealer, User )
		emailValidator.validateDuplicateEmail(dealerSignUpRequestDto.getEmail());

		Master master = verifyActiveMaster(dealerSignUpRequestDto.getMasterEmail());

		String encodedPassword = passwordEncoder.encode(dealerSignUpRequestDto.getPassword());

		Dealer dealer = Dealer.builder()
			.email(dealerSignUpRequestDto.getEmail())
			.password(encodedPassword)
			.name(dealerSignUpRequestDto.getName())
			.phoneNumber(dealerSignUpRequestDto.getPhoneNumber())
			.master(master)
			.build();        // @Builder.Default가 적용되어 branch=미배정, role=APPLICANT, isDeleted=false 자동 세팅

		dealerRepository.save(dealer);
	}

	public FindByDealerResponseDto findByDealer(CustomUserDetails customUserDetails) {

		Dealer dealer = verifyDealeer(customUserDetails.getId());

		return new FindByDealerResponseDto(
			dealer.getId(),
			dealer.getEmail(),
			dealer.getName(),
			dealer.getBranch(),
			dealer.getPhoneNumber(),
			dealer.getMaster().getEmail()
		);
	}

	@Transactional("dataTransactionManager")
	public void updatedDealer(
		CustomUserDetails customUserDetails,
		DealerUpdatedRequestDto dealerUpdatedRequestDto
	) {
		Dealer dealer = verifyActiveDealer(customUserDetails.getEmail());
		dealer.updatedDealer(dealerUpdatedRequestDto);
	}

	@Transactional("dataTransactionManager")
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

	@Transactional("dataTransactionManager")
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

		Role newRole = dealerRoleChangeRequestDto.getRole();

		// 딜러에게 허용된 Role인지 검증: DEALER 또는 APPLICANT만 허용
		if (newRole != Role.DEALER && newRole != Role.APPLICANT) {
			throw new DealerException(DealerErrorCode.INVALID_ROLE);
		}

		// 역할 변경
		dealer.updateRole(newRole);

	}

	@Transactional("dataTransactionManager")
	public void updatedBranchChangeDealer(
		CustomUserDetails customUserDetails,
		DealerBranchChangeRequestDto dealerBranchChangeRequestDto
	) {
		Master master = verifyActiveMaster(customUserDetails.getEmail());

		Dealer dealer = verifyActiveDealer(dealerBranchChangeRequestDto.getDealerEmail());

		// Dealer가 실제로 이 Master 소속인지 확인
		if (!dealer.getMaster().getId().equals(master.getId())) {
			throw new DealerException(DealerErrorCode.DEALER_NOT_BELONG_TO_MASTER);
		}

		Branch newBranch = dealerBranchChangeRequestDto.getBranch();

		dealer.updateBranch(newBranch);
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
