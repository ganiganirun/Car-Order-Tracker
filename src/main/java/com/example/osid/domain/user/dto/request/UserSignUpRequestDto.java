package com.example.osid.domain.user.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class UserSignUpRequestDto {

	@Email
	@NotEmpty(message = "이메일은 필수입니다.")
	private String email;

	@NotEmpty(message = "비밀번호는 필수입니다.")
	@Pattern(
		regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,}$",
		message = "비밀번호는 영문 대소문자, 숫자, 특수문자를 포함한 8자 이상이어야 합니다."
	)
	private String password;

	@NotEmpty(message = "이름은 필수입니다.")
	private String name;

	@NotNull(message = "생년월일은 필수입니다.")
	@PastOrPresent(message = "생년월일은 현재 날짜보다 과거여야 합니다.") // 날짜가 과거 또는 오늘이어야 한다는 추가 검증
	private LocalDate dateOfBirth; // 생년월일

	@NotEmpty(message = "전화번호는 필수입니다.")
	@Pattern(regexp = "^010-[0-9]{4}-[0-9]{4}$", message = "전화번호는 010-xxxx-xxxx 형식이어야 합니다.")
	private String phoneNumber;

	@NotEmpty(message = "주소는 필수입니다.")
	private String address;
}
