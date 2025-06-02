package com.example.osid.domain.user.dto.request;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class UserUpdatedRequestDto {

	private String name;

	@Pattern(regexp = "^010-[0-9]{4}-[0-9]{4}$", message = "전화번호는 010-xxxx-xxxx 형식이어야 합니다.")
	private String phoneNumber;

	private String address;
}
