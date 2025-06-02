package com.example.osid.domain.master.dto.response;

import lombok.Getter;

@Getter
public class FindByMasterResponseDto {
	private Long id;
	private String businessNumber;
	private String name;
	private String phoneNumber;
	private String email;
	private String address;

	public FindByMasterResponseDto(
		Long id,
		String businessNumber,
		String name,
		String phoneNumber,
		String email,
		String address
	) {
		this.id = id;
		this.businessNumber = businessNumber;
		this.name = name;
		this.phoneNumber = phoneNumber;
		this.email = email;
		this.address = address;
	}
}
