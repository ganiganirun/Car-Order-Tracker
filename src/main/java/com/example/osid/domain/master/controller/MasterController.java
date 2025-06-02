package com.example.osid.domain.master.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.osid.common.auth.CustomUserDetails;
import com.example.osid.common.response.CommonResponse;
import com.example.osid.domain.master.dto.request.MasterSignUpRequestDto;
import com.example.osid.domain.master.dto.request.MasterUpdatedRequestDto;
import com.example.osid.domain.master.dto.response.FindByMasterResponseDto;
import com.example.osid.domain.master.service.MasterService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/masters")
@RequiredArgsConstructor
public class MasterController {

	private final MasterService masterService;

	@PostMapping("/signup")
	@ResponseStatus(HttpStatus.CREATED)
	public CommonResponse<Void> signUpMaster(@RequestBody @Valid MasterSignUpRequestDto masterSignUpRequestDto) {
		masterService.signUpMaster(masterSignUpRequestDto);
		return CommonResponse.created();
	}

	@GetMapping("/me")
	public CommonResponse<FindByMasterResponseDto> findByMaster(
		@AuthenticationPrincipal CustomUserDetails customUserDetails
	) {
		FindByMasterResponseDto me = masterService.findByMaster(customUserDetails);
		return CommonResponse.ok(me);
	}

	@PatchMapping("/me")
	public CommonResponse<Void> updatedMaster(
		@AuthenticationPrincipal CustomUserDetails customUserDetails,
		@RequestBody MasterUpdatedRequestDto masterUpdatedRequestDto
	) {
		masterService.updatedMaster(customUserDetails, masterUpdatedRequestDto);
		return CommonResponse.ok();
	}

}
