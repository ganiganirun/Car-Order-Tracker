package com.example.osid.domain.option.dto;

import java.time.LocalDateTime;

import com.example.osid.domain.option.entity.Option;
import com.example.osid.domain.option.enums.OptionCategory;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OptionMasterResponse {

	private final Long id;

	private final String name; //옵션 이름

	private final String description; //옵션 설명

	private final String image; //옵션 이미지

	private final OptionCategory category; //옵션 카테고리

	private final Long price; //옵션 가격

	private final LocalDateTime deletedAt;

	public static OptionMasterResponse from(Option option) {
		return new OptionMasterResponse(
			option.getId(),
			option.getName(),
			option.getDescription(),
			option.getImage(),
			option.getCategory(),
			option.getPrice(),
			option.getDeletedAt()
		);
	}
}
