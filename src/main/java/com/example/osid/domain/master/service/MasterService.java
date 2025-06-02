package com.example.osid.domain.master.service;

import org.springframework.stereotype.Service;

import com.example.osid.common.auth.CustomUserDetails;
import com.example.osid.domain.dealer.repository.DealerRepository;
import com.example.osid.domain.master.dto.request.MasterSignUpRequestDto;
import com.example.osid.domain.master.dto.request.MasterUpdatedRequestDto;
import com.example.osid.domain.master.dto.response.FindByMasterResponseDto;
import com.example.osid.domain.master.entity.Master;
import com.example.osid.domain.master.exception.MasterErrorCode;
import com.example.osid.domain.master.exception.MasterException;
import com.example.osid.domain.master.repository.MasterRepository;
import com.example.osid.domain.user.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MasterService {

	private final MasterRepository masterRepository;
	private final UserRepository userRepository;
	private final DealerRepository dealerRepository;

	public void signUpMaster(MasterSignUpRequestDto masterSignUpRequestDto) {
		if (userRepository.findByEmail(masterSignUpRequestDto.getEmail()).isPresent()) {
			throw new MasterException(MasterErrorCode.EMAIL_ALREADY_EXISTS);
		}
		if (dealerRepository.findByEmail(masterSignUpRequestDto.getEmail()).isPresent()) {
			throw new MasterException(MasterErrorCode.EMAIL_ALREADY_EXISTS);
		}

		if (masterRepository.findByEmail(masterSignUpRequestDto.getEmail()).isPresent()) {
			throw new MasterException(MasterErrorCode.EMAIL_ALREADY_EXISTS);
		}
		Master master = new Master(
			masterSignUpRequestDto.getBusinessNumber(),
			masterSignUpRequestDto.getName(),
			masterSignUpRequestDto.getPhoneNumber(),
			masterSignUpRequestDto.getEmail(),
			masterSignUpRequestDto.getPassword(),
			masterSignUpRequestDto.getAddress(),
			masterSignUpRequestDto.getLicense()
		);
		masterRepository.save(master);

	}

	public FindByMasterResponseDto findByMaster(CustomUserDetails customUserDetails) {
		Master master = verifyMaster(customUserDetails.getId());
		return new FindByMasterResponseDto(
			master.getId(),
			master.getBusinessNumber(),
			master.getName(),
			master.getPhoneNumber(),
			master.getEmail(),
			master.getAddress()
		);
	}

	@Transactional
	public void updatedMaster(
		CustomUserDetails customUserDetails,
		MasterUpdatedRequestDto masterUpdatedRequestDto
	) {
		Master master = verifyMaster(customUserDetails.getId());
		master.UpdateMaster(masterUpdatedRequestDto);
	}

	private Master verifyMaster(Long masterId) {
		return masterRepository.findById(masterId)
			.orElseThrow(() -> new MasterException(MasterErrorCode.MASTER_NOT_FOUND));
	}
}
