package com.example.osid.domain.license.service;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.osid.domain.license.dto.LicenseInfoResponseDto;
import com.example.osid.domain.license.entity.LicenseKey;
import com.example.osid.domain.license.enums.LicenseStatus;
import com.example.osid.domain.license.exception.LicenseErrorCode;
import com.example.osid.domain.license.exception.LicenseException;
import com.example.osid.domain.license.repository.LicenseKeyRepository;
import com.example.osid.domain.master.entity.Master;
import com.example.osid.domain.master.exception.MasterErrorCode;
import com.example.osid.domain.master.exception.MasterException;
import com.example.osid.domain.master.repository.MasterRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LicenseKeyService {
	/** 라이센스 서비스에 관련된 기능
	 *  발급기능
	 *  유효성 검사
	 *  라이센스 키 취소
	 *  전체 조회
	 *  */
	private final LicenseKeyRepository licenseKeyRepository;
	private final MasterRepository masterRepository;

	@Transactional("dataTransactionManager")
	public String assignLicense(Long ownerId) {
		// 1) 기존에 할당된 키가 있으면 해제
		licenseKeyRepository
			.findByOwnerIdAndLicenseStatus(ownerId, LicenseStatus.ASSIGNED)
			.ifPresent(existing -> {
				existing.setLicenseStatus(LicenseStatus.AVAILABLE);
				existing.setOwnerId(null);
				existing.setAssignedAt(null);
				// JPA 관리 대상이므로 save() 안 해도 dirty-checking 으로 반영됩니다.
			});

		// 2) 새로운 사용 가능한 키를 꺼내서 할당
		LicenseKey lic = licenseKeyRepository
			.findFirstByLicenseStatus(LicenseStatus.AVAILABLE)
			.orElseThrow(() -> new LicenseException(LicenseErrorCode.NO_AVAILABLE_KEYS));

		lic.setLicenseStatus(LicenseStatus.ASSIGNED);
		lic.setOwnerId(ownerId);
		lic.setAssignedAt(LocalDateTime.now());

		// 3) Master 엔티티에도 새 키 반영
		Master master = masterRepository.findById(ownerId)
			.orElseThrow(() -> new MasterException(MasterErrorCode.MASTER_NOT_FOUND));
		master.setProductKey(lic.getProductKey());

		return lic.getProductKey();
	}

	/** 발급된 키 유효성 확인 */
	public boolean validate(String productKey) {
		return licenseKeyRepository.findByProductKeyAndLicenseStatus(productKey, LicenseStatus.ASSIGNED)
			.filter(l -> l.getLicenseStatus() != LicenseStatus.REVOKED)
			.isPresent();
	}

	// Key 취소
	@Transactional("dataTransactionManager")
	public void revoke(String productKey) {
		licenseKeyRepository.findByProductKeyAndLicenseStatus(productKey, LicenseStatus.ASSIGNED)
			.ifPresentOrElse(
				l -> {
					l.setLicenseStatus(LicenseStatus.REVOKED);
					licenseKeyRepository.save(l);
				},
				() -> {
					throw new LicenseException(LicenseErrorCode.KEY_NOT_FOUND);
				}
			);
	}

	// public List<LicenseInfoResponseDto> findAllLicense() {
	//
	// 	List<LicenseKey> findAllLicenses = licenseKeyRepository.findAll();
	//
	// 	List<LicenseInfoResponseDto> licenseList = new ArrayList<>();
	//
	// 	for (LicenseKey licenseKey : findAllLicenses) {
	// 		licenseList.add(new LicenseInfoResponseDto(
	// 				licenseKey.getProductKey(),
	// 				licenseKey.getLicenseStatus(),
	// 				licenseKey.getOwnerId(),
	// 				licenseKey.getAssignedAt()
	// 			)
	// 		);
	// 	}
	//
	// 	return licenseList;
	// }

	public Page<LicenseInfoResponseDto> findAllLicense(Pageable pageable) {
		Page<LicenseKey> page = licenseKeyRepository.findAll(pageable);

		return page.map(licenseKey -> new LicenseInfoResponseDto(
			licenseKey.getProductKey(),
			licenseKey.getLicenseStatus(),
			licenseKey.getOwnerId(),
			licenseKey.getAssignedAt()
		));
	}

	/** 기존키를 ownerId에 할당(가입 시) */
	@Transactional("dataTransactionManager")
	public void assignExistingKey(String productKey, Long ownerId) {
		LicenseKey key = licenseKeyRepository.findByProductKeyAndLicenseStatus(productKey, LicenseStatus.AVAILABLE)
			// 사용 가능하지 않은 키(존재하지 않거나 이미 할당/취소됨)인 경우
			.orElseThrow(() -> new LicenseException(LicenseErrorCode.KEY_NOT_FOUND));

		key.setLicenseStatus(LicenseStatus.ASSIGNED);
		key.setOwnerId(ownerId);
		key.setAssignedAt(LocalDateTime.now());
		licenseKeyRepository.save(key);
	}
}
