package com.example.osid.common.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

@Getter
public class LoginRequestDto {

	@Email(message = "이메일 형식이 올바르지 않습니다.")
	@NotEmpty(message = "이메일은 필수입니다.")
	private String email;

	@NotEmpty(message = "비밀번호는 필수입니다.")
	private String password;

}
