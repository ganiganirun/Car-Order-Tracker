package com.example.osid.domain.option.dto;

import com.example.osid.domain.option.enums.OptionCategory;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OptionRequest {

	@NotBlank(message = "옵션 이름을 입력해 주세요.")
	private final String name; //옵션 이름
	@NotBlank(message = "옵션 설명을 입력해 주세요.")
	private final String description; //옵션 설명
	@NotBlank(message = "옵션 이미지를 입력해 주세요.")
	private final String image; //옵션 이미지
	@NotNull(message = "옵션 카테고리를 입력해 주세요.")
	private final OptionCategory category; //옵션 카테고리
	@NotNull(message = "옵션 가격을 입력해 주세요.")
	private final Long price; //옵션 가격
}
