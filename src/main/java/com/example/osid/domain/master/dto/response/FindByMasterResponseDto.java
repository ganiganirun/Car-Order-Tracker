package com.example.osid.domain.master.dto.response;

import java.util.List;

import com.example.osid.domain.dealer.dto.response.DealerInfoResponseDto;

import lombok.Getter;

@Getter
public class FindByMasterResponseDto {
	private Long id;
	private String businessNumber;
	private String name;
	private String phoneNumber;
	private String email;
	private String address;

	// 마스터 밑에 속한 활성 딜러 목록
	private List<DealerInfoResponseDto> dealers;

	public FindByMasterResponseDto(
		Long id,
		String businessNumber,
		String name,
		String phoneNumber,
		String email,
		String address,
		List<DealerInfoResponseDto> dealers
	) {
		this.id = id;
		this.businessNumber = businessNumber;
		this.name = name;
		this.phoneNumber = phoneNumber;
		this.email = email;
		this.address = address;
		this.dealers = dealers;
	}
}
