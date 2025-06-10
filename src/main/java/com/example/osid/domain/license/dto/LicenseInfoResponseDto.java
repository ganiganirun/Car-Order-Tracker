package com.example.osid.domain.license.dto;

import java.time.LocalDateTime;

import com.example.osid.domain.license.enums.LicenseStatus;

import lombok.Getter;

@Getter
public class LicenseInfoResponseDto {

	private String productKey;
	private LicenseStatus licenseStatus;
	private Long ownerId;
	private LocalDateTime assignedAt;

	public LicenseInfoResponseDto(
		String productKey,
		LicenseStatus licenseStatus,
		Long ownerId,
		LocalDateTime assignedAt
	) {
		this.productKey = productKey;
		this.licenseStatus = licenseStatus;
		this.ownerId = ownerId;
		this.assignedAt = assignedAt;
	}
}
