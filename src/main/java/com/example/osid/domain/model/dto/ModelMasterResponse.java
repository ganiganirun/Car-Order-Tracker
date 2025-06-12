package com.example.osid.domain.model.dto;

import java.time.LocalDateTime;

import com.example.osid.domain.model.entity.Model;
import com.example.osid.domain.model.enums.ModelCategory;
import com.example.osid.domain.model.enums.ModelColor;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ModelMasterResponse {

	private final Long id;
	private final String name;
	private final ModelColor color;
	private final String description;
	private final String image;
	private final ModelCategory category;
	private final String seatCount;
	private final Long price;
	private final LocalDateTime deletedAt;

	public static ModelMasterResponse from(Model model) {
		return new ModelMasterResponse(model.getId(),
			model.getName(), model.getColor(), model.getDescription(),
			model.getImage(), model.getCategory(), model.getSeatCount(),
			model.getPrice(), model.getDeletedAt()
		);
	}
}
