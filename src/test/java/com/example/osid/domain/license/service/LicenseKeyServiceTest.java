package com.example.osid.domain.license.service;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.osid.domain.license.dto.LicenseInfoResponseDto;
import com.example.osid.domain.license.entity.LicenseKey;
import com.example.osid.domain.license.enums.LicenseStatus;
import com.example.osid.domain.license.repository.LicenseKeyRepository;
import com.example.osid.domain.master.repository.MasterRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

class LicenseKeyServiceTest {

	@Mock
	private LicenseKeyRepository licenseKeyRepository;

	@Mock
	private MasterRepository masterRepository;

	@InjectMocks
	private LicenseKeyService licenseKeyService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void findAllLicense_success() {
		// given: 샘플 라이센스 키 3개 생성
		List<LicenseKey> licenseKeys = List.of(
				createLicenseKey("KEY-1", LicenseStatus.ASSIGNED, 1L),
				createLicenseKey("KEY-2", LicenseStatus.AVAILABLE, null),
				createLicenseKey("KEY-3", LicenseStatus.REVOKED, 2L)
		);
		Page<LicenseKey> licenseKeyPage = new PageImpl<>(licenseKeys);
		Pageable pageable = Pageable.unpaged();
		when(licenseKeyRepository.findAll(pageable)).thenReturn(licenseKeyPage);

		// when: 서비스 메서드 호출
		Page<LicenseInfoResponseDto> resultPage = licenseKeyService.findAllLicense(pageable);
		List<LicenseInfoResponseDto> result = resultPage.getContent();

		// then: 결과 검증
		assertEquals(3, resultPage.getTotalElements());

		LicenseInfoResponseDto first = result.get(0);
		assertEquals("KEY-1", first.getProductKey());
		assertEquals(LicenseStatus.ASSIGNED, first.getLicenseStatus());
		assertEquals(Long.valueOf(1L), first.getOwnerId());
		assertNotNull(first.getAssignedAt());
	}

	// 헬퍼 메서드: 테스트용 LicenseKey 객체 생성
	private LicenseKey createLicenseKey(String key, LicenseStatus status, Long ownerId) {
		LicenseKey licenseKey = new LicenseKey(key, status);
		licenseKey.setOwnerId(ownerId);
		licenseKey.setAssignedAt(LocalDateTime.of(2025, 6, 15, 16, 48)); // 고정 시간 사용
		return licenseKey;
	}
}