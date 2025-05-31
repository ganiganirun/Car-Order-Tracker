package com.example.osid.domain.master.entity;

import com.example.osid.common.entity.BaseEntity;
import com.example.osid.common.entity.enums.Role;

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

@Builder
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "masters")
public class Master extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String businessNumber; //사업자 번호

	@Column(nullable = false)
	private String email; //이메일

	@Column(nullable = false)
	private String password; //비밀번호

	@Column(nullable = false)
	private String address; //주소

	@Column(nullable = false)
	private String license; //라이센스

	@Column(nullable = false)
	private Role role; //역할

}
