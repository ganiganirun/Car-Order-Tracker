package com.example.osid.domain.model.enums;

import com.example.osid.domain.model.exception.ModelErrorCode;
import com.example.osid.domain.model.exception.ModelException;
import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ModelCategory {
	SEDAN("세단"),            // 세단
	SUV("SUV"),              // SUV
	ELECTRIC("전기차"),         // 전기차
	HYBRID("하이브리드");           // 하이브리드

	private final String displayModelCategory;

	@JsonCreator
	public static ModelCategory from(String inputCategory) {
		for (ModelCategory category : ModelCategory.values()) {
			if (category.name().equalsIgnoreCase(inputCategory)) {
				return category;
			}
		}
		throw new ModelException(ModelErrorCode.INVALID_MODEL_CATEGORY);
	}
}
