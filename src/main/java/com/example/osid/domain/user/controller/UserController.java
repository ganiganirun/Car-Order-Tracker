package com.example.osid.domain.user.controller;

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
import com.example.osid.domain.user.dto.request.UserDeletedRequestDto;
import com.example.osid.domain.user.dto.request.UserSignUpRequestDto;
import com.example.osid.domain.user.dto.request.UserUpdatedRequestDto;
import com.example.osid.domain.user.dto.response.FindbyUserResponseDto;
import com.example.osid.domain.user.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
	private final UserService userService;

	@PostMapping("/signup")
	@ResponseStatus(HttpStatus.CREATED)
	public CommonResponse<Void> signUpUser(@RequestBody @Valid UserSignUpRequestDto userSignUpRequestDto) {
		userService.signUpUser(userSignUpRequestDto);
		return CommonResponse.created();
	}

	@GetMapping("/me")
	public CommonResponse<FindbyUserResponseDto> findByUser(
		@AuthenticationPrincipal CustomUserDetails customUserDetails
	) {
		FindbyUserResponseDto me = userService.findbyUser(customUserDetails);
		return CommonResponse.ok(me);
	}

	@PatchMapping("/me")
	public CommonResponse<Void> updatedUser(
		@AuthenticationPrincipal CustomUserDetails customUserDetails,
		@Valid @RequestBody UserUpdatedRequestDto userUpdatedRequestDto
	) {
		userService.updatedUser(customUserDetails, userUpdatedRequestDto);
		return CommonResponse.ok();
	}

	@DeleteMapping("/me")
	public CommonResponse<Void> deletedUser(
		@AuthenticationPrincipal CustomUserDetails customUserDetails,
		@Valid @RequestBody UserDeletedRequestDto userDeletedRequestDto
	) {
		userService.deletedUser(customUserDetails, userDeletedRequestDto);
		return CommonResponse.ok();
	}
}
