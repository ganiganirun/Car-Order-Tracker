package com.example.osid.domain.dealer.entity;

import com.example.osid.common.entity.BaseEntity;
import com.example.osid.common.entity.enums.Role;

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
@Table(name = "dealers")
@AllArgsConstructor
@NoArgsConstructor
public class Dealer extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String email; //이메일

	@Column(nullable = false)
	private String name; //이름

	@Column(nullable = false)
	private String phoneNumber; //전화번호

	@Column(nullable = false)
	private String password; //비밀번호

	@Column(nullable = false)
	private String point; //지점

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Role role; //역할

}
