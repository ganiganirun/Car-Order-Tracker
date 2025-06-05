package com.example.osid.domain.dealer.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.osid.common.auth.CustomUserDetails;
import com.example.osid.common.response.CommonResponse;
import com.example.osid.domain.dealer.dto.request.DealerBranchChangeRequestDto;
import com.example.osid.domain.dealer.dto.request.DealerDeletedRequestDto;
import com.example.osid.domain.dealer.dto.request.DealerRoleChangeRequestDto;
import com.example.osid.domain.dealer.dto.request.DealerSignUpRequestDto;
import com.example.osid.domain.dealer.dto.request.DealerUpdatedRequestDto;
import com.example.osid.domain.dealer.dto.response.FindByDealerResponseDto;
import com.example.osid.domain.dealer.service.DealerService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/dealers")
@RequiredArgsConstructor
public class DealerController {

	private final DealerService dealerService;

	@PostMapping("/signup")
	@ResponseStatus(HttpStatus.CREATED)
	public CommonResponse<Void> signUpDealer(@RequestBody @Valid DealerSignUpRequestDto dealerSignUpRequestDto) {
		dealerService.signUpDealer(dealerSignUpRequestDto);
		return CommonResponse.created();
	}

	@GetMapping("/me")
	public CommonResponse<FindByDealerResponseDto> findByDealer(
		@AuthenticationPrincipal CustomUserDetails customUserDetails
	) {
		FindByDealerResponseDto me = dealerService.findByDealer(customUserDetails);
		return CommonResponse.ok(me);
	}

	@PatchMapping("/me")
	public CommonResponse<Void> updatedDealer(
		@AuthenticationPrincipal CustomUserDetails customUserDetails,
		@Valid @RequestBody DealerUpdatedRequestDto dealerUpdatedRequestDto
	) {
		dealerService.updatedDealer(customUserDetails, dealerUpdatedRequestDto);
		return CommonResponse.ok();
	}

	@DeleteMapping("/me")
	public CommonResponse<Void> deletedDealer(
		@AuthenticationPrincipal CustomUserDetails customUserDetails,
		@Valid @RequestBody DealerDeletedRequestDto dealerDeletedRequestDto
	) {
		dealerService.deletedDealer(customUserDetails, dealerDeletedRequestDto);
		return CommonResponse.ok();
	}

	@PatchMapping("/role")
	public CommonResponse<Void> updatedRoleChange(
		@AuthenticationPrincipal CustomUserDetails customUserDetails,
		@Valid @RequestBody DealerRoleChangeRequestDto dealerRoleChangeRequestDto
	) {
		dealerService.updatedRoleChangeDealer(customUserDetails, dealerRoleChangeRequestDto);
		return CommonResponse.ok();
	}

	@PatchMapping("/branch")
	public CommonResponse<Void> updatedBranchChange(
		@AuthenticationPrincipal CustomUserDetails customUserDetails,
		@Valid @RequestBody DealerBranchChangeRequestDto dealerBranchChangeRequestDto
	) {
		dealerService.updatedBranchChangeDealer(customUserDetails, dealerBranchChangeRequestDto);
		return CommonResponse.ok();
	}
}
