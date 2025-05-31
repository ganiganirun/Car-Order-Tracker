package com.example.osid.domain.model.entity;

import com.example.osid.common.entity.BaseEntity;
import com.example.osid.domain.model.enums.ModelCategory;
import com.example.osid.domain.model.enums.ModelColor;

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
@Entity
@Getter
@Table(name = "models")
@AllArgsConstructor
@NoArgsConstructor
public class Model extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String name; // 차량 이름(아반떼)

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private ModelColor color; // 차량 색상(레드)

	@Column(nullable = false)
	private String description; // 차량 설명

	@Column(nullable = false)
	private String image; // 차량 이미지

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private ModelCategory category; // 차 종류( 세단, SUV )

	@Column(nullable = false)
	private String seatCount; // 몇 인승 ( 2, 5, 7, 9 )

	@Column(nullable = false)
	private Long price; // 차량 가격

}
