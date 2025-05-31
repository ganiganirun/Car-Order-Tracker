package com.example.osid.config;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

import com.example.osid.common.entity.enums.Role;
import com.example.osid.domain.dealer.entity.Dealer;
import com.example.osid.domain.dealer.repository.DealerRepository;
import com.example.osid.domain.model.entity.Model;
import com.example.osid.domain.model.enums.ModelCategory;
import com.example.osid.domain.model.enums.ModelColor;
import com.example.osid.domain.model.repository.ModelRepository;
import com.example.osid.domain.option.entity.Option;
import com.example.osid.domain.option.enums.OptionCategory;
import com.example.osid.domain.option.repository.OptionRepository;
import com.example.osid.domain.user.entity.User;
import com.example.osid.domain.user.repository.UserRepository;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TestData {

	private final DealerRepository dealerRepository;
	private final UserRepository userRepository;
	private final ModelRepository modelRepository;
	private final OptionRepository optionRepository;

	@PostConstruct
	public void initTestData() {
		dealerRepository.save(
			Dealer.builder()
				.email("dealer1@naver.com")
				.name("딜러1")
				.phoneNumber("010-1111-1111")
				.password("Testpassword1@")
				.point("강남")
				.role(Role.DEALER)
				.build()
		);
		userRepository.save(
			User.builder()
				.email("user1@naver.com")
				.name("유저1")
				.phoneNumber("010-1111-1111")
				.password("Testpassword1@")
				.role(Role.USER)
				.dateOfBirth(LocalDate.parse("2000-11-28"))
				.address("대전시 중구")
				.build()
		);
		modelRepository.save(
			Model.builder()
				.name("쏘나타")
				.color(ModelColor.RED)
				.description("중형 세단으로 탁월한 연비와 편안한 승차감 제공")
				.image("https://example.com/sonata.jpg")
				.category(ModelCategory.EASE)
				.seatCount("5")
				.price(20000000L)
				.build()
		);
		optionRepository.save(
			Option.builder()
				.name("썬루프")
				.description("차량에 자연광과 개방감을 더해주는 파노라마 썬루프")
				.image("https://example.com/sunroof.jpg")
				.category(OptionCategory.EXTERIOR)
				.price(500000L)
				.build()
		);

		optionRepository.save(
			Option.builder()
				.name("네비게이션")
				.description("최신 지도와 경로 안내를 제공하는 인포테인먼트 내비게이션 시스템")
				.image("https://example.com/navigation.jpg")
				.category(OptionCategory.INFOTAINMENT)
				.price(300000L)
				.build()
		);

		optionRepository.save(
			Option.builder()
				.name("열선 시트")
				.description("겨울철에도 따뜻하게 유지되는 앞좌석 열선 시트")
				.image("https://example.com/heatedseat.jpg")
				.category(OptionCategory.CONVENIENCE)
				.price(200000L)
				.build()
		);

		optionRepository.save(
			Option.builder()
				.name("긴급 제동 시스템")
				.description("충돌 위험 시 자동으로 브레이크를 작동시키는 안전 시스템")
				.image("https://example.com/emergencybrake.jpg")
				.category(OptionCategory.SAFETY)
				.price(700000L)
				.build()
		);

		optionRepository.save(
			Option.builder()
				.name("스포츠 서스펜션")
				.description("보다 다이나믹한 주행을 위한 강화 서스펜션")
				.image("https://example.com/sportssuspension.jpg")
				.category(OptionCategory.PERFORMANCE)
				.price(800000L)
				.build()
		);

	}
}
