package com.example.osid.domain.model.enums;

import com.example.osid.domain.model.exception.ModelErrorCode;
import com.example.osid.domain.model.exception.ModelException;
import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ModelColor {
	RED("빨강"),
	WHITE("하양"),
	BLACK("검정"),
	BLUE("파랑"),
	GREEN("초록");

	private final String displayModelColor;

	@JsonCreator
	public static ModelColor from(String inputCategory) {
		for (ModelColor category : ModelColor.values()) {
			if (category.name().equalsIgnoreCase(inputCategory)) {
				return category;
			}
		}
		throw new ModelException(ModelErrorCode.INVALID_MODEL_CATEGORY);
	}
}
