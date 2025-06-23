package com.example.osid.domain.history.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "histories")
public class History {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String bodyNumber;

	@Column(nullable = false)
	private BigDecimal stage1;

	@Column(nullable = false)
	private BigDecimal stage2;

	@Column(nullable = false)
	private BigDecimal stage3;

	@Column(nullable = false)
	private BigDecimal stage4;

	@Column(nullable = false)
	private BigDecimal stage5;

	@Column(nullable = false)
	private BigDecimal totalDuration;

}
