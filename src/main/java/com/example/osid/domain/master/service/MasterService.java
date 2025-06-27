package com.example.osid.domain.master.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.osid.common.auth.CustomUserDetails;
import com.example.osid.common.auth.EmailValidator;
import com.example.osid.domain.dealer.dto.response.DealerInfoResponseDto;
import com.example.osid.domain.dealer.entity.Dealer;
import com.example.osid.domain.dealer.repository.DealerRepository;
import com.example.osid.domain.license.service.LicenseKeyService;
import com.example.osid.domain.master.dto.request.MasterDeletedRequestDto;
import com.example.osid.domain.master.dto.request.MasterSignUpRequestDto;
import com.example.osid.domain.master.dto.request.MasterUpdatedRequestDto;
import com.example.osid.domain.master.dto.response.FindByAllMasterResponseDto;
import com.example.osid.domain.master.dto.response.FindByMasterResponseDto;
import com.example.osid.domain.master.entity.Master;
import com.example.osid.domain.master.exception.MasterErrorCode;
import com.example.osid.domain.master.exception.MasterException;
import com.example.osid.domain.master.repository.MasterRepository;
import com.example.osid.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MasterService {

	private final MasterRepository masterRepository;
	private final UserRepository userRepository;
	private final DealerRepository dealerRepository;
	private final PasswordEncoder passwordEncoder;
	private final EmailValidator emailValidator;
	private final LicenseKeyService licenseKeyService;

	@Transactional("dataTransactionManager")
	public void signUpMaster(MasterSignUpRequestDto masterSignUpRequestDto) {

		// 공통된 이메일이 있는지 확인 ( Master, Dealer, User )
		emailValidator.validateDuplicateEmail(masterSignUpRequestDto.getEmail());

		String encodedPassword = passwordEncoder.encode(masterSignUpRequestDto.getPassword());

		Master master = Master.builder()
			.businessNumber(masterSignUpRequestDto.getBusinessNumber())
			.name(masterSignUpRequestDto.getName())
			.phoneNumber(masterSignUpRequestDto.getPhoneNumber())
			.email(masterSignUpRequestDto.getEmail())
			.password(encodedPassword)
			.address(masterSignUpRequestDto.getAddress())
			.productKey(masterSignUpRequestDto.getProductKey())
			.build();
		masterRepository.save(master);

		// 3) 라이선스 키 검증 & 할당
		//    - AVAILABLE 상태인지 확인
		//    - ASSIGNED 상태로 변경, ownerId = master.getId()
		licenseKeyService.assignExistingKey(masterSignUpRequestDto.getProductKey(), master.getId());

	}

	public FindByMasterResponseDto findByMaster(CustomUserDetails customUserDetails) {

		Master master = verifyMaster(customUserDetails.getId());

		List<Dealer> dealers = dealerRepository.findByMasterAndIsDeletedFalse(master);

		List<DealerInfoResponseDto> dealerList = new ArrayList<>();

		for (Dealer dealer : dealers) {
			DealerInfoResponseDto dealerDto = new DealerInfoResponseDto(
				dealer.getId(),
				dealer.getEmail(),
				dealer.getName(),
				dealer.getPhoneNumber(),
				dealer.getBranch()
			);
			dealerList.add(dealerDto);
		}

		return new FindByMasterResponseDto(
			master.getId(),
			master.getBusinessNumber(),
			master.getName(),
			master.getPhoneNumber(),
			master.getEmail(),
			master.getAddress(),
			dealerList
		);
	}

	@Transactional("dataTransactionManager")
	public void updatedMaster(
		CustomUserDetails customUserDetails,
		MasterUpdatedRequestDto masterUpdatedRequestDto
	) {
		Master master = verifyActiveMaster(customUserDetails.getEmail());
		master.updatedMaster(masterUpdatedRequestDto);
	}

	@Transactional("dataTransactionManager")
	public void deletedMaster(
		CustomUserDetails customUserDetails,
		MasterDeletedRequestDto masterDeletedRequestDto
	) {
		Master master = verifyActiveMaster(customUserDetails.getEmail());

		String rawPassword = masterDeletedRequestDto.getPassword();
		String storedHash = master.getPassword();

		if (!passwordEncoder.matches(rawPassword, storedHash)) {
			// 비밀번호가 불일치하면 예외 던짐
			throw new MasterException(MasterErrorCode.MASTER_INVALID_PASSWORD);
		}

		master.softDeletedMaster();
	}

	public List<FindByAllMasterResponseDto> findByAllMaster(CustomUserDetails customUserDetails) {

		Master me = verifyActiveMaster(customUserDetails.getEmail());

		// 2) 같은 사업자 번호를 가진 활성 마스터들 조회
		List<Master> masters = masterRepository
			.findByBusinessNumberAndIsDeletedFalse(me.getBusinessNumber());

		// 3) List<Master> → List<FindByAllMasterResponseDto>로 변환
		List<FindByAllMasterResponseDto> masterList = new ArrayList<>();
		for (Master master : masters) {
			masterList.add(new FindByAllMasterResponseDto(
				master.getId(),
				master.getBusinessNumber(),
				master.getName(),
				master.getPhoneNumber(),
				master.getEmail(),
				master.getAddress()
			));
		}

		return masterList;
	}

	private Master verifyMaster(Long masterId) {
		return masterRepository.findById(masterId)
			.orElseThrow(() -> new MasterException(MasterErrorCode.MASTER_NOT_FOUND));
	}

	private Master verifyActiveMaster(String email) {
		return masterRepository.findByEmailAndIsDeletedFalse(email)
			.orElseThrow(() -> new MasterException(MasterErrorCode.MASTER_NOT_FOUND));
	}

}
