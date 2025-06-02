package com.example.osid.domain.dealer.dto.response;

import lombok.Getter;

@Getter
public class FindByDealerResponseDto {

	private Long id;
	private String email;
	private String name;
	private String point;
	private String phoneNumber;

	public FindByDealerResponseDto(
		Long id,
		String email,
		String name,
		String point,
		String phoneNumber
	) {
		this.id = id;
		this.email = email;
		this.name = name;
		this.point = point;
		this.phoneNumber = phoneNumber;
	}
}
