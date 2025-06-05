package com.example.osid.domain.dealer.dto.request;

import com.example.osid.common.entity.enums.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class DealerRoleChangeRequestDto {

	@Email(message = "유효한 이메일 형식이어야 합니다.")
	@NotBlank(message = "dealerEmail은 필수입니다.")
	private String dealerEmail;

	@NotNull(message = "role은 필수입니다.")
	private Role role;
}
