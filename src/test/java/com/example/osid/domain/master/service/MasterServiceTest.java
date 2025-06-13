package com.example.osid.domain.master.service;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.osid.common.auth.CustomUserDetails;
import com.example.osid.common.auth.EmailValidator;
import com.example.osid.domain.dealer.dto.response.DealerInfoResponseDto;
import com.example.osid.domain.dealer.entity.Dealer;
import com.example.osid.domain.dealer.repository.DealerRepository;
import com.example.osid.domain.license.service.LicenseKeyService;
import com.example.osid.domain.master.dto.request.MasterSignUpRequestDto;
import com.example.osid.domain.master.dto.response.FindByMasterResponseDto;
import com.example.osid.domain.master.entity.Master;
import com.example.osid.domain.master.repository.MasterRepository;
import com.example.osid.domain.user.repository.UserRepository;

class MasterServiceTest {

	@Mock // 의존 객체
	private MasterRepository masterRepository;
	@Mock // 의존 객체
	private UserRepository userRepository;
	@Mock // 의존 객체
	private DealerRepository dealerRepository;
	@Mock // 의존 객체
	private PasswordEncoder passwordEncoder;
	@Mock // 의존 객체
	private EmailValidator emailValidator;
	@Mock // 의존 객체
	private LicenseKeyService licenseKeyService;

	@InjectMocks // 실제 테스트 대상
	private MasterService masterService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void signUpMaster_success() {
		// given 테스트 환경·입력값 준비
		MasterSignUpRequestDto dto = mock(MasterSignUpRequestDto.class);
		when(dto.getEmail()).thenReturn("test@example.com");
		when(dto.getPassword()).thenReturn("Password1!");
		when(dto.getBusinessNumber()).thenReturn("123-45-67890");
		when(dto.getName()).thenReturn("John Doe");
		when(dto.getPhoneNumber()).thenReturn("010-1234-5678");
		when(dto.getAddress()).thenReturn("Seoul");
		when(dto.getProductKey()).thenReturn("LICENSE-KEY-123");

		when(passwordEncoder.encode(dto.getPassword())).thenReturn("encodedPwd");
		Master savedMaster = Master.builder()
			.id(1L)
			.businessNumber(dto.getBusinessNumber())
			.name(dto.getName())
			.phoneNumber(dto.getPhoneNumber())
			.email(dto.getEmail())
			.password("encodedPwd")
			.address(dto.getAddress())
			.productKey(dto.getProductKey())
			.build();
		when(masterRepository.save(any(Master.class))).thenReturn(savedMaster);

		// when 실제 메서드 호출
		masterService.signUpMaster(dto);

		// then  결과·상호작용 검증
		verify(emailValidator).validateDuplicateEmail(dto.getEmail());
		verify(passwordEncoder).encode(dto.getPassword());
		verify(masterRepository).save(any(Master.class));
		verify(licenseKeyService).assignExistingKey(eq(dto.getProductKey()), isNull());
	}

	@Test
	void findByMaster() {
	}

	@Test
	void updatedMaster() {
	}

	@Test
	void findByMaster_success() {
		// given: CustomUserDetails에서 반환할 Master ID 설정
		CustomUserDetails userDetails = mock(CustomUserDetails.class);
		when(userDetails.getId()).thenReturn(1L);

		// given: 마스터 엔티티 stub
		Master master = Master.builder()
			.id(1L)
			.businessNumber("123-45-67890")
			.name("Master Name")
			.phoneNumber("010-1111-2222")
			.email("master@example.com")
			.password("pwdHash")
			.address("Seoul")
			.productKey("KEY123")
			.build();
		when(masterRepository.findById(1L)).thenReturn(Optional.of(master));

		// given: 마스터 밑의 딜러 리스트 stub
		Dealer dealer = Dealer.builder()
			.id(10L)
			.email("dealer@example.com")
			.password("encodedPwd")
			.name("Dealer Name")
			.phoneNumber("010-3333-4444")
			.branch(com.example.osid.domain.dealer.enums.Branch.미배정)
			.role(com.example.osid.common.entity.enums.Role.APPLICANT)
			.master(master)
			.build();
		when(dealerRepository.findByMasterAndIsDeletedFalse(master))
			.thenReturn(List.of(dealer));

		// when: 서비스 호출
		FindByMasterResponseDto response = masterService.findByMaster(userDetails);

		// then: 반환 DTO의 마스터 정보 검증
		assertEquals(Long.valueOf(1L), response.getId());                               // ID
		assertEquals("123-45-67890", response.getBusinessNumber());       // 사업자 번호
		assertEquals("Master Name", response.getName());                  // 이름
		assertEquals("010-1111-2222", response.getPhoneNumber());         // 전화번호
		assertEquals("master@example.com", response.getEmail());          // 이메일
		assertEquals("Seoul", response.getAddress());                     // 주소

		// then: 딜러 리스트 검증
		List<DealerInfoResponseDto> dealers = response.getDealers();        // DTO 리스트
		assertEquals(1, dealers.size());                                   // 개수
		DealerInfoResponseDto d = dealers.get(0);
		assertEquals(Long.valueOf(10L), d.getId());                                   // 딜러 ID
		assertEquals("dealer@example.com", d.getEmail());                // 딜러 이메일
		assertEquals("Dealer Name", d.getName());                        // 딜러 이름
		assertEquals("010-3333-4444", d.getPhoneNumber());               // 딜러 전화번호
		assertEquals(com.example.osid.domain.dealer.enums.Branch.미배정, d.getBranch()); // 딜러 지점
	}

	@Test
	void findByAllMaster() {
	}
}