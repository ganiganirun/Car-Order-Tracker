package com.example.osid.domain.dealer.service;

import org.springframework.stereotype.Service;

import com.example.osid.common.auth.CustomUserDetails;
import com.example.osid.domain.dealer.dto.request.DealerSignUpRequestDto;
import com.example.osid.domain.dealer.dto.request.DealerUpdatedRequestDto;
import com.example.osid.domain.dealer.dto.response.FindByDealerResponseDto;
import com.example.osid.domain.dealer.entity.Dealer;
import com.example.osid.domain.dealer.exception.DealerErrorCode;
import com.example.osid.domain.dealer.exception.DealerException;
import com.example.osid.domain.dealer.repository.DealerRepository;
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

		Dealer dealer = new Dealer(
			dealerSignUpRequestDto.getEmail(),
			dealerSignUpRequestDto.getPassword(),
			dealerSignUpRequestDto.getName(),
			dealerSignUpRequestDto.getPhoneNumber()
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
			dealer.getPhoneNumber()
		);
	}

	@Transactional
	public void updatedDealer(
		CustomUserDetails customUserDetails,
		DealerUpdatedRequestDto dealerUpdatedRequestDto
	) {
		Dealer dealer = verifyDealeer(customUserDetails.getId());
		dealer.UpdateDelaer(dealerUpdatedRequestDto);
	}

	private Dealer verifyDealeer(Long dealerId) {
		return dealerRepository.findById(dealerId)
			.orElseThrow(() -> new DealerException(DealerErrorCode.DEALER_NOT_FOUND));
	}

}
