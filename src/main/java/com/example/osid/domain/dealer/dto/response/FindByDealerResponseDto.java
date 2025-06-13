package com.example.osid.domain.dealer.dto.response;

import com.example.osid.domain.dealer.enums.Branch;

import lombok.Getter;

@Getter
public class FindByDealerResponseDto {

	private Long id;
	private String email;
	private String name;
	private Branch branch;
	private String phoneNumber;
	private String masterEmail;

	public FindByDealerResponseDto(
		Long id,
		String email,
		String name,
		Branch branch,
		String phoneNumber,
		String masterEmail
	) {
		this.id = id;
		this.email = email;
		this.name = name;
		this.branch = branch;
		this.phoneNumber = phoneNumber;
		this.masterEmail = masterEmail;
	}
}
