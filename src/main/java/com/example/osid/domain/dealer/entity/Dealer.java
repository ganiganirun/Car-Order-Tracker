package com.example.osid.domain.dealer.entity;

import java.time.LocalDateTime;

import com.example.osid.common.entity.BaseEntity;
import com.example.osid.common.entity.enums.Role;
import com.example.osid.domain.dealer.dto.request.DealerUpdatedRequestDto;
import com.example.osid.domain.dealer.enums.Branch;
import com.example.osid.domain.master.entity.Master;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
	private String password; //비밀번호

	@Column(nullable = false)
	private String name; //이름

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	@Builder.Default
	private Branch branch = Branch.미배정; //지점

	@Column(nullable = false)
	private String phoneNumber; //전화번호

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	@Builder.Default
	private Role role = Role.APPLICANT; //역할

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "master_id", nullable = false)
	private Master master;

	@Column(nullable = false)
	@Builder.Default
	private boolean isDeleted = false;

	// null 허용: 삭제 전에는 null
	private LocalDateTime deletedAt;

	public Dealer(
		String email,
		String password,
		String name,
		String phoneNumber,
		Master master
	) {
		this.email = email;
		this.password = password;
		this.name = name;
		this.phoneNumber = phoneNumber;
		this.master = master;
	}

	public void updatedDealer(DealerUpdatedRequestDto dealerUpdatedRequestDto) {
		if (dealerUpdatedRequestDto.getName() != null) {
			this.name = dealerUpdatedRequestDto.getName();
		}
		if (dealerUpdatedRequestDto.getPhoneNumber() != null) {
			this.phoneNumber = dealerUpdatedRequestDto.getPhoneNumber();
		}
	}

	// 소프트 딜리트: isDeleted = true, deletedAt = 현재 시각
	public void softDeletedDealer() {
		this.isDeleted = true;
		this.deletedAt = LocalDateTime.now();
	}

	public void updateRole(Role newRole) {
		this.role = newRole;
	}

	public void updateBranch(Branch newBranch) {
		this.branch = newBranch;
	}
}
