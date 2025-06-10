package com.example.osid.domain.license.entity;

import java.time.LocalDateTime;

import com.example.osid.domain.license.enums.LicenseStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Table(name = "licensekeys")
@Getter
public class LicenseKey {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true, length = 36)
	private String productKey;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private LicenseStatus licenseStatus;

	@Column(columnDefinition = "TIMESTAMP")
	private LocalDateTime assignedAt;

	private Long ownerId;

	public LicenseKey() {
	}

	public LicenseKey(String productKey, LicenseStatus licenseStatus) {
		this.productKey = productKey;
		this.licenseStatus = licenseStatus;
	}

	public void setLicenseStatus(LicenseStatus licenseStatus) {
		this.licenseStatus = licenseStatus;
	}

	public void setAssignedAt(LocalDateTime assignedAt) {
		this.assignedAt = assignedAt;
	}

	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
	}

}
