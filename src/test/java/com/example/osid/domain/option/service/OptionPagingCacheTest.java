package com.example.osid.domain.option.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StopWatch;

@SpringBootTest
class OptionPagingCacheTest {

	@Autowired
	private OptionService optionService;

	@Autowired
	private CacheManager cacheManager;

	@Test
	@DisplayName("옵션 캐시적용 속도테스트")
	void findAllOption_Cache() {
		Pageable pageable = PageRequest.of(0, 10);

		// 첫 호출 (DB hit)
		StopWatch stopWatch1 = new StopWatch();
		stopWatch1.start();
		optionService.findAllOption(pageable);
		stopWatch1.stop();
		System.out.println("첫 호출 (DB HIT) 걸린 시간(ms): " + stopWatch1.getTotalTimeMillis());

		// 두 번째 호출 (캐시 hit)
		StopWatch stopWatch2 = new StopWatch();
		stopWatch2.start();
		optionService.findAllOption(pageable);
		stopWatch2.stop();
		System.out.println("두 번째 호출 (캐시 HIT) 걸린 시간(ms): " + stopWatch2.getTotalTimeMillis());

		// 캐시 삭제
		cacheManager.getCache("options").clear();

		// 다시 호출 (DB hit)
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		optionService.findAllOption(pageable);
		stopWatch.stop();
		System.out.println("캐시 삭제 후 재호출 걸린 시간(ms): " + stopWatch.getTotalTimeMillis());

	}

}
