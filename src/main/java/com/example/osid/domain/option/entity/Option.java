package com.example.osid.domain.option.entity;

import java.time.LocalDateTime;

import com.example.osid.common.entity.BaseEntity;
import com.example.osid.domain.option.dto.OptionUpdateRequest;
import com.example.osid.domain.option.enums.OptionCategory;
import com.example.osid.domain.option.exception.OptionErrorCode;
import com.example.osid.domain.option.exception.OptionException;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "options")
public class Option extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String name; //옵션 이름

	@Column(nullable = false)
	private String description; //옵션 설명

	@Column(nullable = false)
	private String image; //옵션 이미지

	@Column(nullable = false, length = 50)
	@Enumerated(EnumType.STRING)
	private OptionCategory category; //옵션 카테고리

	@Column(nullable = false)
	private Long price; //옵션 가격

	@Column
	private LocalDateTime deletedAt; //삭제 여부

	public Option(String name, String description, String image, OptionCategory category, Long price) {
		this.name = name;
		this.description = description;
		this.image = image;
		this.category = category;
		this.price = price;
	}

	public void setDeletedAt() {
		if (this.deletedAt != null) {
			throw new OptionException(OptionErrorCode.OPTION_ALREADY_DELETED);
		}
		this.deletedAt = LocalDateTime.now();
	}

	public void updateOption(OptionUpdateRequest request) {
		if (request.getName() != null) {
			this.name = request.getName();
		}
		if (request.getDescription() != null) {
			this.description = request.getDescription();
		}
		if (request.getImage() != null) {
			this.image = request.getImage();
		}
		if (request.getCategory() != null) {
			this.category = request.getCategory();
		}
		if (request.getPrice() != null) {
			this.price = request.getPrice();
		}
	}
}
