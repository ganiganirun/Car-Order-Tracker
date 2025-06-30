package com.example.osid.domain.model.dto;

import com.example.osid.domain.model.enums.ModelCategory;
import com.example.osid.domain.model.enums.ModelColor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ModelCreateRequest {

	@NotBlank(message = "모델명을 입력해 주세요.")
	@Size(max = 50, message = "모델명은 50자 이하로 입력해 주세요.")
	private final String name;

	@NotNull(message = "모델의 색상을 입력해 주세요.")
	private final ModelColor color;

	@NotBlank(message = "모델의 설명을 입력해 주세요.")
	@Size(max = 500, message = "모델 설명은 500자 이하로 입력해 주세요.")
	private final String description;

	@NotBlank(message = "모델의 이미지를 입력해 주세요.")
	private final String image;

	@NotNull(message = "모델의 카테고리를 입력해 주세요.")
	private final ModelCategory category;

	@NotBlank(message = "모델의 탑승 인원을 입력해 주세요.")
	private final String seatCount;
	
	@NotNull(message = "모델의 가격을 입력해 주세요.")
	@Positive(message = "가격은 0보다 커야 합니다.")
	private final Long price;

}
