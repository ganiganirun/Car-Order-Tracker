package com.example.osid.domain.master.dto.response;

import lombok.Getter;

@Getter
public class FindByAllMasterResponseDto {
	private Long id;
	private String businessNumber;
	private String name;
	private String phoneNumber;
	private String email;
	private String address;
	// private Long dealerCount;

	public FindByAllMasterResponseDto(
		Long id,
		String businessNumber,
		String name,
		String phoneNumber,
		String email,
		String address
		// Long dealerCount
	) {
		this.id = id;
		this.businessNumber = businessNumber;
		this.name = name;
		this.phoneNumber = phoneNumber;
		this.email = email;
		this.address = address;
		// this.dealerCount = dealerCount;
	}
}
