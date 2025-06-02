package com.example.osid.domain.option.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.osid.common.response.CommonResponse;
import com.example.osid.domain.option.dto.OptionRequest;
import com.example.osid.domain.option.dto.OptionResponse;
import com.example.osid.domain.option.dto.OptionUpdateRequest;
import com.example.osid.domain.option.service.OptionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/option")
public class OptionController {

	private final OptionService optionService;

	// get 요청 제외 master 권한 필요

	//옵션 생성
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public CommonResponse<Void> createOption(@RequestBody @Valid OptionRequest request) {

		optionService.createOption(request);
		return CommonResponse.created();
	}

	//옵션 단건 조회
	@GetMapping("/{optionId}")
	@ResponseStatus(HttpStatus.OK)
	public CommonResponse<OptionResponse> findOption(@PathVariable Long optionId) {

		return CommonResponse.ok(optionService.findOption(optionId));
	}

	//옵션 전체 조회
	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public CommonResponse<Page<OptionResponse>> findAllOption(
		@RequestParam(defaultValue = "1") int page,
		@RequestParam(defaultValue = "10") int size
	) {

		return CommonResponse.ok(optionService.findAllOption(page, size));
	}

	//옵션 수정
	@PatchMapping("/{optionId}")
	public CommonResponse<OptionResponse> updateOption(@PathVariable Long optionId,
		@RequestBody @Valid OptionUpdateRequest request) {

		return CommonResponse.ok(optionService.updateOption(optionId, request));
	}

	//옵션 삭제(soft deleted)
	@ResponseStatus(HttpStatus.OK)
	@DeleteMapping("/{optionId}")
	public CommonResponse<Void> deleteModel(@PathVariable Long optionId) {
		optionService.deleteOption(optionId);
		return CommonResponse.ok();
	}

}
