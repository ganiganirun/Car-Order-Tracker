package com.example.osid.domain.master.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class MasterSignUpRequestDto {

	@NotNull(message = "사업자 번호는 필수입니다.")
	@Pattern(regexp = "^[0-9]{3}-[0-9]{2}-[0-9]{5}$", message = "사업자 번호는 XXX-XX-XXXXX 형식이어야 합니다.")
	private String businessNumber;

	@NotEmpty(message = "이름은 필수입니다.")
	private String name;

	@NotEmpty(message = "전화번호는 필수입니다.")
	@Pattern(regexp = "^010-[0-9]{4}-[0-9]{4}$", message = "전화번호는 010-xxxx-xxxx 형식이어야 합니다.")
	private String phoneNumber;

	@Email
	@NotEmpty(message = "이메일은 필수입니다.")
	private String email;

	@NotEmpty(message = "비밀번호는 필수입니다.")
	@Pattern(
		regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,}$",
		message = "비밀번호는 영문 대소문자, 숫자, 특수문자를 포함한 8자 이상이어야 합니다."
	)
	private String password;

	@NotEmpty(message = "주소는 필수입니다.")
	private String address;

	@NotEmpty(message = "라이센스는 필수입니다.")
	private String productKey;

}
