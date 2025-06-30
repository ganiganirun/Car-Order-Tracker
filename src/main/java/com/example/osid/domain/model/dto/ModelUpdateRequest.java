package com.example.osid.domain.model.dto;

import com.example.osid.domain.model.enums.ModelCategory;
import com.example.osid.domain.model.enums.ModelColor;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ModelUpdateRequest {

	@Size(max = 50, message = "모델명은 50자 이하로 입력해 주세요.")
	private final String name;

	private final ModelColor color;

	@Size(max = 500, message = "모델 설명은 500자 이하로 입력해 주세요.")
	private final String description;

	private final String image;

	private final ModelCategory category;

	private final String seatCount;

	@Positive(message = "가격은 0보다 커야 합니다.")
	private final Long price;
}
