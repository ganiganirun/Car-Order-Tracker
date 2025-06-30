package com.example.osid.domain.mycar.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.osid.common.auth.CustomUserDetails;
import com.example.osid.common.request.CommonPageRequest;
import com.example.osid.common.response.CommonResponse;
import com.example.osid.domain.mycar.dto.MyCarListResponse;
import com.example.osid.domain.mycar.dto.MyCarResponse;
import com.example.osid.domain.mycar.service.MyCarService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/myCar")
public class MyCarController {

	private final MyCarService myCarService;

	//내 차 단건 조회
	@GetMapping("/{myCarId}")
	@ResponseStatus(HttpStatus.OK)
	public CommonResponse<MyCarResponse> findMyCar(
		@AuthenticationPrincipal CustomUserDetails customUserDetails,
		@PathVariable Long myCarId
	) {

		MyCarResponse myCarResponse = myCarService.findMyCar(customUserDetails, myCarId);
		return CommonResponse.ok(myCarResponse);
	}

	//내 차 전체 조회
	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public CommonResponse<Page<MyCarListResponse>> findAllMyCar(
		@AuthenticationPrincipal CustomUserDetails customUserDetails,
		@ModelAttribute CommonPageRequest commonPageRequest
	) {
		Pageable pageable = commonPageRequest.toPageable();
		return CommonResponse.ok(myCarService.findAllMyCar(customUserDetails, pageable));
	}

	//내 차 삭제(soft deleted)
	@ResponseStatus(HttpStatus.OK)
	@DeleteMapping("/{myCarId}")
	public CommonResponse<Void> deleteModel(
		@AuthenticationPrincipal CustomUserDetails customUserDetails,
		@PathVariable Long myCarId) {
		myCarService.deleteMyCar(customUserDetails, myCarId);
		return CommonResponse.ok();
	}

	//테스트용 내 차 등록
	@PostMapping
	@ResponseStatus(HttpStatus.OK)
	public CommonResponse<MyCarResponse> saveMyCar(
		@AuthenticationPrincipal CustomUserDetails customUserDetails
	) {
		Long ordersId = 1L;
		Long userId = customUserDetails.getId();
		return CommonResponse.ok(myCarService.saveMyCar(ordersId));
	}
}
