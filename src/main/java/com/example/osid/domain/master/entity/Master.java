package com.example.osid.domain.master.entity;

import java.time.LocalDateTime;

import com.example.osid.common.entity.BaseEntity;
import com.example.osid.common.entity.enums.Role;
import com.example.osid.domain.master.dto.request.MasterUpdatedRequestDto;

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
@Table(name = "masters")
public class Master extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String businessNumber; //사업자 번호

	@Column(nullable = false)
	private String name; // 담당자 이름

	@Column(nullable = false)
	private String phoneNumber; //전화번호

	@Column(nullable = false)
	private String email; //이메일

	@Column(nullable = false)
	private String password; //비밀번호

	@Column(nullable = false)
	private String address; //주소

	@Column(nullable = false)
	private String productKey; //라이센스

	@Column(nullable = false)
	@Builder.Default
	@Enumerated(EnumType.STRING)
	private Role role = Role.MASTER; //역할

	@Column(nullable = false)
	@Builder.Default
	private boolean isDeleted = false;

	// null 허용: 삭제 전에는 null
	private LocalDateTime deletedAt;

	public Master(
		String businessNumber,
		String name,
		String phoneNumber,
		String email,
		String password,
		String address,
		String productKey
	) {
		this.businessNumber = businessNumber;
		this.name = name;
		this.phoneNumber = phoneNumber;
		this.email = email;
		this.password = password;
		this.address = address;
		this.productKey = productKey;
	}

	public void updatedMaster(MasterUpdatedRequestDto masterUpdatedRequestDto) {
		if (masterUpdatedRequestDto.getName() != null) {
			this.name = masterUpdatedRequestDto.getName();
		}
		if (masterUpdatedRequestDto.getPhoneNumber() != null) {
			this.phoneNumber = masterUpdatedRequestDto.getPhoneNumber();
		}
		if (masterUpdatedRequestDto.getAddress() != null) {
			this.address = masterUpdatedRequestDto.getAddress();
		}
	}

	// 소프트 딜리트: isDeleted = true, deletedAt = 현재 시각
	public void softDeletedMaster() {
		this.isDeleted = true;
		this.deletedAt = LocalDateTime.now();
	}

	// 마스터 권한 있는 친구가 License 강제 삽입
	public void setProductKey(String productKey) {
		this.productKey = productKey;
	}

}
