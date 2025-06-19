package com.example.osid.domain.option.dto;

import com.example.osid.domain.option.entity.Option;
import com.example.osid.domain.option.enums.OptionCategory;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OptionResponse {

	private Long id;

	private String name; //옵션 이름

	private String description; //옵션 설명

	private String image; //옵션 이미지

	private OptionCategory category; //옵션 카테고리

	private Long price; //옵션 가격

	public static OptionResponse from(Option option) {
		return new OptionResponse(
			option.getId(),
			option.getName(),
			option.getDescription(),
			option.getImage(),
			option.getCategory(),
			option.getPrice()
		);
	}
}
