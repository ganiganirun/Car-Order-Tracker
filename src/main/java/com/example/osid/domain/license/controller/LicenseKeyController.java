package com.example.osid.domain.license.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.osid.common.response.CommonResponse;
import com.example.osid.domain.license.dto.LicenseInfoResponseDto;
import com.example.osid.domain.license.service.LicenseKeyService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/licenses")
@RequiredArgsConstructor
@PreAuthorize("hasRole('MASTER') and principal.id == 1")
public class LicenseKeyController {

	private final LicenseKeyService licenseKeyService;

	// 키 발급
	@PostMapping("/assign")
	public CommonResponse<String> assign(@RequestParam Long ownerId) {
		String key = licenseKeyService.assignLicense(ownerId);
		return CommonResponse.ok(key);
	}

	// 키 유효성 확인
	@GetMapping("/validate/{productKey}")
	public CommonResponse<Boolean> validate(@PathVariable String productKey) {
		boolean ok = licenseKeyService.validate(productKey);
		return CommonResponse.ok(ok);
	}

	// 키 취소
	@PostMapping("/revoke/{productKey}")
	public CommonResponse<Void> revoke(@PathVariable String productKey) {
		licenseKeyService.revoke(productKey);
		return CommonResponse.ok();
	}

	// @GetMapping()
	// public CommonResponse<List<LicenseInfoResponseDto>> findAllLicense() {
	// 	List<LicenseInfoResponseDto> licenseList = licenseKeyService.findAllLicense();
	// 	return CommonResponse.ok(licenseList);
	// }

	@GetMapping()
	public CommonResponse<Page<LicenseInfoResponseDto>> findAllLicense(
		@PageableDefault(size = 10) Pageable pageable
	) {
		Page<LicenseInfoResponseDto> licenseList = licenseKeyService.findAllLicense(pageable);
		return CommonResponse.ok(licenseList);
	}

}

