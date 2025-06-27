package com.example.osid.domain.license.scheduler;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.osid.domain.license.entity.LicenseKey;
import com.example.osid.domain.license.enums.LicenseStatus;
import com.example.osid.domain.license.repository.LicenseKeyRepository;
import com.example.osid.domain.license.util.LicenseKeyGenerator;

import lombok.RequiredArgsConstructor;

@Component
@EnableScheduling
@RequiredArgsConstructor
public class LicenseKeyRefillScheduler {

	private final LicenseKeyRepository licenseKeyRepository;
	private final LicenseKeyGenerator licenseKeyGenerator;

	private static final long MIN_POOL_SIZE = 9;    // 최소 보유 수
	private static final int TARGET_POOL_SIZE = 15;  // 목표 풀 크기

	// @Scheduled(cron = "0 0 * * * *")  // 매시 정각 실행
	@Scheduled(cron = "0 */1 * * * *")
	@Transactional("dataTransactionManager")
	public void refillPoolIfNeeded() {
		long available = licenseKeyRepository.countByLicenseStatus(LicenseStatus.AVAILABLE);
		if (available < MIN_POOL_SIZE) {
			int toCreate = TARGET_POOL_SIZE - (int)available;
			for (int i = 0; i < toCreate; i++) {
				String key = licenseKeyGenerator.generateKey();
				if (licenseKeyRepository.findByProductKeyAndLicenseStatus(key, LicenseStatus.AVAILABLE).isEmpty()) {
					licenseKeyRepository.save(new LicenseKey(key, LicenseStatus.AVAILABLE));
				} else {
					i--;
				}
			}
		}
	}
}
