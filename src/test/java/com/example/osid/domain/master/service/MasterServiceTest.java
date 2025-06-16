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
import com.example.osid.domain.master.dto.request.MasterDeletedRequestDto;
import com.example.osid.domain.master.dto.request.MasterSignUpRequestDto;
import com.example.osid.domain.master.dto.request.MasterUpdatedRequestDto;
import com.example.osid.domain.master.dto.response.FindByAllMasterResponseDto;
import com.example.osid.domain.master.dto.response.FindByMasterResponseDto;
import com.example.osid.domain.master.entity.Master;
import com.example.osid.domain.master.exception.MasterException;
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
	void updatedMaster_success() {
		// given
		CustomUserDetails userDetails = mock(CustomUserDetails.class);
		when(userDetails.getEmail()).thenReturn("master@example.com");

		Master master = Master.builder()
			.id(1L)
			.businessNumber("123-45-67890")
			.name("Old Name")
			.phoneNumber("010-0000-0000")
			.email("master@example.com")
			.password("encodedPwd")
			.address("Old Address")
			.productKey("KEY123")
			.build();
		when(masterRepository.findByEmailAndIsDeletedFalse("master@example.com"))
			.thenReturn(Optional.of(master));

		MasterUpdatedRequestDto dto = mock(MasterUpdatedRequestDto.class);
		when(dto.getName()).thenReturn("New Name");
		when(dto.getPhoneNumber()).thenReturn("010-9999-9999");
		when(dto.getAddress()).thenReturn(null); // 주소는 null → 업데이트 안 함

		// when
		masterService.updatedMaster(userDetails, dto);

		// then
		assertEquals("New Name", master.getName());                   // 변경됨
		assertEquals("010-9999-9999", master.getPhoneNumber());       // 변경됨
		assertEquals("Old Address", master.getAddress());             // 그대로 유지
	}

	@Test
	void updatedMaster_fail_masterNotFound() {
		// given: 인증된 사용자의 정보를 흉내내는 CustomUserDetails를 만든다
		CustomUserDetails userDetails = mock(CustomUserDetails.class);
		when(userDetails.getEmail()).thenReturn("notfound@example.com");

		// given: 업데이트할 이름만 존재하는 dto를 만든다
		MasterUpdatedRequestDto dto = mock(MasterUpdatedRequestDto.class);
		when(dto.getName()).thenReturn("New Name");

		// given: 이메일로 Master를 못 찾게 하기 위해 Optional.empty()를 리턴하도록 mock 설정
		when(masterRepository.findByEmailAndIsDeletedFalse("notfound@example.com"))
			.thenReturn(Optional.empty());

		// when & then: 해당 상황에서 MasterException이 터져야 함을 검증
		assertThrows(MasterException.class, () -> {
			masterService.updatedMaster(userDetails, dto);
		});
	}

	@Test
	void deletedMaster_success() {
		// given: 로그인한 마스터의 정보를 흉내냄
		CustomUserDetails userDetails = mock(CustomUserDetails.class);
		when(userDetails.getEmail()).thenReturn("master@example.com");

		// given: 실제 마스터 객체 (비밀번호 암호화된 상태)
		Master master = Master.builder()
			.id(1L)
			.email("master@example.com")
			.password("encodedPassword")
			.name("홍길동")
			.phoneNumber("010-1234-5678")
			.address("서울시 강남구")
			.businessNumber("123-45-67890")
			.productKey("KEY123")
			.build();

		// given: 비밀번호 일치 처리
		when(masterRepository.findByEmailAndIsDeletedFalse("master@example.com"))
			.thenReturn(Optional.of(master));
		when(passwordEncoder.matches("Password1!", "encodedPassword")).thenReturn(true);

		// given: 탈퇴 요청에 사용될 비밀번호 DTO
		MasterDeletedRequestDto dto = mock(MasterDeletedRequestDto.class);
		when(dto.getPassword()).thenReturn("Password1!");

		// when: 서비스 호출
		masterService.deletedMaster(userDetails, dto);

		// then: 마스터가 soft delete 상태로 바뀌었는지 확인
		assertTrue(master.isDeleted());              // 삭제 처리됨
		assertNotNull(master.getDeletedAt());        // 삭제 시간 설정됨
	}

	@Test
	void findByAllMaster_success() {
		// given: 로그인한 사용자의 정보를 모킹
		CustomUserDetails userDetails = mock(CustomUserDetails.class);
		when(userDetails.getEmail()).thenReturn("masterA@example.com");

		// given: 로그인한 마스터 엔티티 모킹
		Master me = Master.builder()
			.id(1L)
			.businessNumber("123-45-67890")
			.name("Master A")
			.phoneNumber("010-1111-2222")
			.email("masterA@example.com")
			.password("encodedPwd")
			.address("서울 강남구")
			.productKey("KEY-1")
			.build();
		when(masterRepository.findByEmailAndIsDeletedFalse("masterA@example.com"))
			.thenReturn(Optional.of(me));

		// given: 같은 사업자 번호를 가진 다른 마스터들도 포함한 리스트 반환
		Master other = Master.builder()
			.id(2L)
			.businessNumber("123-45-67890")
			.name("Master B")
			.phoneNumber("010-3333-4444")
			.email("masterB@example.com")
			.password("encodedPwd")
			.address("서울 마포구")
			.productKey("KEY-2")
			.build();
		when(masterRepository.findByBusinessNumberAndIsDeletedFalse("123-45-67890"))
			.thenReturn(List.of(me, other));

		// when: 서비스 호출
		List<FindByAllMasterResponseDto> result = masterService.findByAllMaster(userDetails);

		// then: 결과 검증
		assertEquals(2, result.size()); // 마스터가 2명

		// 마스터 A 정보 확인
		FindByAllMasterResponseDto dto1 = result.get(0);
		assertEquals("Master A", dto1.getName());
		assertEquals("010-1111-2222", dto1.getPhoneNumber());
		assertEquals("masterA@example.com", dto1.getEmail());

		// 마스터 B 정보 확인
		FindByAllMasterResponseDto dto2 = result.get(1);
		assertEquals("Master B", dto2.getName());
		assertEquals("010-3333-4444", dto2.getPhoneNumber());
		assertEquals("masterB@example.com", dto2.getEmail());
	}

}