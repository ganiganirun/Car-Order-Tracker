package com.example.osid.domain.dealer.dto.response;

import com.example.osid.domain.dealer.enums.Branch;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DealerInfoResponseDto {
	private Long id;
	private String email;
	private String name;
	private String phoneNumber;
	private Branch branch;
}
