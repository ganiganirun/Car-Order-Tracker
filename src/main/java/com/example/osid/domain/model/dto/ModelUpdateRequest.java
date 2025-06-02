package com.example.osid.domain.model.dto;

import com.example.osid.domain.model.enums.ModelCategory;
import com.example.osid.domain.model.enums.ModelColor;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ModelUpdateRequest {

	private final String name;
	private final ModelColor color;
	private final String description;
	private final String image;
	private final ModelCategory category;
	private final String seatCount;
	private final Long price;
}
