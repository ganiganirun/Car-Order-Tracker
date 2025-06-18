package com.example.osid.domain.user.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.example.osid.common.entity.BaseEntity;
import com.example.osid.common.entity.enums.Role;
import com.example.osid.domain.user.dto.request.UserUpdatedRequestDto;

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
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
public class User extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String email; //이메일

	@Column(nullable = false)
	private String password; //비밀번호

	@Column(nullable = false)
	private String name; //이름

	@Column(nullable = false)
	private LocalDate dateOfBirth; //생년월일

	@Column(nullable = false)
	private String phoneNumber; //전화번호

	@Column(nullable = false)
	private String address; //주소

	@Column(nullable = false)
	@Builder.Default
	@Enumerated(EnumType.STRING)
	private Role role = Role.USER;

	@Column(nullable = false)
	@Builder.Default
	private boolean isDeleted = false;

	// null 허용: 삭제 전에는 null
	private LocalDateTime deletedAt;

	public User(
		String email,
		String password,
		String name,
		LocalDate dateOfBirth,
		String phoneNumber,
		String address) {
		this.email = email;
		this.password = password;
		this.name = name;
		this.dateOfBirth = dateOfBirth;
		this.phoneNumber = phoneNumber;
		this.address = address;
	}

	public void updatedUser(UserUpdatedRequestDto userUpdatedRequestDto) {
		if (userUpdatedRequestDto.getName() != null) {
			this.name = userUpdatedRequestDto.getName();
		}
		if (userUpdatedRequestDto.getPhoneNumber() != null) {
			this.phoneNumber = userUpdatedRequestDto.getPhoneNumber();
		}
		if (userUpdatedRequestDto.getAddress() != null) {
			this.address = userUpdatedRequestDto.getAddress();
		}
	}

	// 소프트 딜리트: isDeleted = true, deletedAt = 현재 시각
	public void softDeletedUser() {
		this.isDeleted = true;
		this.deletedAt = LocalDateTime.now();
	}
}
