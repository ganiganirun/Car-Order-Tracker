package com.example.osid.domain.option.dto;

import com.example.osid.domain.option.enums.OptionCategory;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OptionUpdateRequest {

	@Size(max = 50, message = "옵션명은 50자 이하로 입력해 주세요.")
	private final String name; //옵션 이름

	@Size(max = 500, message = "옵션 설명은 500자 이하로 입력해 주세요.")
	private final String description; //옵션 설명

	private final String image; //옵션 이미지

	private final OptionCategory category; //옵션 카테고리

	@Positive(message = "가격은 0보다 커야 합니다.")
	private final Long price; //옵션 가격

}
