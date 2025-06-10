package com.example.osid.domain.license.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.osid.domain.license.entity.LicenseKey;
import com.example.osid.domain.license.enums.LicenseStatus;

@Repository
public interface LicenseKeyRepository extends JpaRepository<LicenseKey, Long> {

	// 상태 별로 키 개수 조회
	long countByLicenseStatus(LicenseStatus licenseStatus);

	// 가장 먼저 발견되는 AVAILABLE 키 하나 가져오기
	Optional<LicenseKey> findFirstByLicenseStatus(LicenseStatus licenseStatus);

	// 특정 키가 AVAILABLE 상태인지 조회
	Optional<LicenseKey> findByProductKeyAndLicenseStatus(String productKey, LicenseStatus licenseStatus);

	Optional<LicenseKey> findByOwnerIdAndLicenseStatus(Long ownerId, LicenseStatus licenseStatus);
}
