package com.example.osid.domain.license.init;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.osid.domain.license.entity.LicenseKey;
import com.example.osid.domain.license.enums.LicenseStatus;
import com.example.osid.domain.license.repository.LicenseKeyRepository;
import com.example.osid.domain.license.util.LicenseKeyGenerator;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LicenseKeyInitializer implements ApplicationRunner {

	private final LicenseKeyRepository licenseKeyRepository;
	private final LicenseKeyGenerator licenseKeyGenerator;
	private static final int INITIAL_POOL_SIZE = 10;  // 초기 사용할 수 있는 라이센스키 수

	/**
	 * 애플리케이션 시작 시 한 번 실행됩니다.
	 * 현재 AVAILABLE 상태의 키 개수를 조회하여,
	 * INITIAL_POOL_SIZE에 미치지 못하면 부족한 개수만큼 새 키를 생성합니다.
	 *
	 * @param args ApplicationArguments: 실행 인자 (사용되지 않음)
	 */
	@Override
	@Transactional("dataTransactionManager")
	public void run(ApplicationArguments args) {
		// AVAILABLE 상태 키 개수 조회
		long count = licenseKeyRepository.countByLicenseStatus(LicenseStatus.AVAILABLE);
		// 필요한 키 개수 계산
		int toCreate = (int)Math.max(0, INITIAL_POOL_SIZE - count);

		// 부족한 만큼 새 키 생성
		for (int i = 0; i < toCreate; i++) {

			// 랜덤 키 생성
			String key = licenseKeyGenerator.generateKey();

			// 중복 검사 후 생성
			if (licenseKeyRepository.findByProductKeyAndLicenseStatus(key, LicenseStatus.AVAILABLE).isEmpty()) {
				licenseKeyRepository.save(new LicenseKey(key, LicenseStatus.AVAILABLE));
			} else {
				// 중복 발생 시에 재 시도
				i--;
			}
		}
	}

}
