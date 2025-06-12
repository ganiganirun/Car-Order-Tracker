package com.example.osid.domain.model.dto;

import com.example.osid.domain.model.entity.Model;
import com.example.osid.domain.model.enums.ModelCategory;
import com.example.osid.domain.model.enums.ModelColor;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ModelResponse {

	private final Long id;
	private final String name;
	private final ModelColor color;
	private final String description;
	private final String image;
	private final ModelCategory category;
	private final String seatCount;
	private final Long price;

	public static ModelResponse from(Model model) {
		return new ModelResponse(model.getId(),
			model.getName(), model.getColor(), model.getDescription(),
			model.getImage(), model.getCategory(), model.getSeatCount(),
			model.getPrice()
		);
	}

}
