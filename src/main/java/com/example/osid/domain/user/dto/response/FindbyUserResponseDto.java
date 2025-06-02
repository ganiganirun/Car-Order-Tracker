package com.example.osid.domain.user.dto.response;

import java.time.LocalDate;

import lombok.Getter;

@Getter
public class FindbyUserResponseDto {
	private Long id;
	private String email;
	private String name;
	private LocalDate dateOfBirth;
	private String phoneNumber;
	private String address;

	public FindbyUserResponseDto(
		Long id,
		String email,
		String name,
		LocalDate dateOfBirth,
		String phoneNumber,
		String address
	) {
		this.id = id;
		this.email = email;
		this.name = name;
		this.dateOfBirth = dateOfBirth;
		this.phoneNumber = phoneNumber;
		this.address = address;
	}
}
