package com.example.osid.domain.dealer.service;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.osid.common.auth.CustomUserDetails;
import com.example.osid.common.auth.EmailValidator;
import com.example.osid.common.entity.enums.Role;
import com.example.osid.domain.dealer.dto.request.DealerDeletedRequestDto;
import com.example.osid.domain.dealer.dto.request.DealerRoleChangeRequestDto;
import com.example.osid.domain.dealer.dto.request.DealerSignUpRequestDto;
import com.example.osid.domain.dealer.dto.request.DealerUpdatedRequestDto;
import com.example.osid.domain.dealer.entity.Dealer;
import com.example.osid.domain.dealer.exception.DealerErrorCode;
import com.example.osid.domain.dealer.exception.DealerException;
import com.example.osid.domain.dealer.repository.DealerRepository;
import com.example.osid.domain.master.entity.Master;
import com.example.osid.domain.master.exception.MasterErrorCode;
import com.example.osid.domain.master.exception.MasterException;
import com.example.osid.domain.master.repository.MasterRepository;
import com.example.osid.domain.user.repository.UserRepository;

class DealerServiceTest {

	@Mock
	private DealerRepository dealerRepository;
	@Mock
	private MasterRepository masterRepository;
	@Mock
	private UserRepository userRepository;
	@Mock
	private PasswordEncoder passwordEncoder;
	@Mock
	private EmailValidator emailValidator;

	@InjectMocks
	private DealerService dealerService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void signUpDealer_success() {
		// given: DTO mock 생성 및 반환값 설정
		DealerSignUpRequestDto dto = mock(DealerSignUpRequestDto.class);
		when(dto.getEmail()).thenReturn("dealer@example.com");        // 이메일
		when(dto.getPassword()).thenReturn("12345678aA!");         // 비밀번호
		when(dto.getName()).thenReturn("홍길동");                    // 이름
		when(dto.getPhoneNumber()).thenReturn("010-1234-5678");     // 전화번호
		when(dto.getMasterEmail()).thenReturn("master@example.com"); // 마스터 이메일

		// given: 연관 Master 엔티티 stub 설정
		Master master = Master.builder()
			.id(2L)                                         // DB에서 부여된 id
			.businessNumber("111-22-33333")               // 사업자 번호
			.name("MasterName")                           // 마스터 이름
			.phoneNumber("010-0000-0000")                 // 전화번호
			.email("master@example.com")                  // 이메일
			.password("pwd")                              // 비밀번호 해시
			.address("Somewhere")                        // 주소
			.productKey("KEY")                           // 라이센스 키
			.build();
		when(masterRepository.findByEmailAndIsDeletedFalse("master@example.com"))
			.thenReturn(Optional.of(master));              // 조회 시 마스터 반환

		// given: 비밀번호 암호화 stub
		when(passwordEncoder.encode("12345678aA!")).thenReturn("encodedPwd");

		// when: 서비스 호출
		dealerService.signUpDealer(dto);

		// then: 이메일 중복 검사 호출 확인
		verify(emailValidator).validateDuplicateEmail("dealer@example.com");
		// then: 마스터 조회 호출 확인
		verify(masterRepository).findByEmailAndIsDeletedFalse("master@example.com");
		// then: 저장된 Dealer 객체 캡처 및 필드 검증
		ArgumentCaptor<Dealer> captor = ArgumentCaptor.forClass(Dealer.class);
		verify(dealerRepository).save(captor.capture());             // save 호출 시 인자 캡처
		Dealer saved = captor.getValue();                             // 캡처된 객체 가져오기
		assertEquals("dealer@example.com", saved.getEmail());       // 이메일 검증
		assertEquals("encodedPwd", saved.getPassword());   // 암호화된 비밀번호 검증
		assertEquals(master, saved.getMaster());     // 연관 마스터 검증
	}

	@Test
	void signUpDealer_masterNotFound_throwsException() {
		// given: DTO mock 및 마스터 미발견 시나리오
		DealerSignUpRequestDto dto = mock(DealerSignUpRequestDto.class);
		when(dto.getMasterEmail()).thenReturn("noone@example.com");
		when(masterRepository.findByEmailAndIsDeletedFalse("noone@example.com"))
			.thenReturn(Optional.empty());                       // 조회 시 Optional.empty()

		// when & then: 예외 발생 확인 및 에러코드 검증
		MasterException ex = assertThrows(MasterException.class,
			() -> dealerService.signUpDealer(dto));
		assertEquals(MasterErrorCode.MASTER_NOT_FOUND, ex.getBaseCode());
	}

	@Test
	void findByDealer_success() {
		// given
		CustomUserDetails userDetails = mock(CustomUserDetails.class);
		when(userDetails.getId()).thenReturn(1L);

		Master master = Master.builder()
			.id(1L)
			.email("master@example.com")
			.build();

		Dealer dealer = Dealer.builder()
			.id(1L)
			.email("dealer@example.com")
			.name("홍길동")
			.phoneNumber("010-1234-5678")
			.branch(com.example.osid.domain.dealer.enums.Branch.서울)
			.master(master)
			.build();

		when(dealerRepository.findById(1L)).thenReturn(Optional.of(dealer));

		// when
		var result = dealerService.findByDealer(userDetails);

		// then
		assertEquals(Long.valueOf(1L), result.getId());
		assertEquals("dealer@example.com", result.getEmail());
		assertEquals("홍길동", result.getName());
		assertEquals("010-1234-5678", result.getPhoneNumber());
		assertEquals("master@example.com", result.getMasterEmail());
		assertEquals(com.example.osid.domain.dealer.enums.Branch.서울, result.getBranch());
	}

	@Test
	void updatedDealer_success() {
		// given
		CustomUserDetails userDetails = mock(CustomUserDetails.class);
		when(userDetails.getEmail()).thenReturn("dealer@example.com");

		Dealer dealer = Dealer.builder()
			.id(1L)
			.email("dealer@example.com")
			.name("Old Name")
			.phoneNumber("010-0000-0000")
			.master(Master.builder().id(1L).build())
			.build();

		when(dealerRepository.findByEmailAndIsDeletedFalse("dealer@example.com"))
			.thenReturn(Optional.of(dealer));

		DealerUpdatedRequestDto dto = mock(DealerUpdatedRequestDto.class);
		when(dto.getName()).thenReturn("New Name");
		when(dto.getPhoneNumber()).thenReturn("010-9999-9999");

		// when
		dealerService.updatedDealer(userDetails, dto);

		// then
		assertEquals("New Name", dealer.getName());
		assertEquals("010-9999-9999", dealer.getPhoneNumber());
	}

	@Test
	void deletedDealer_success() {
		// given
		CustomUserDetails userDetails = mock(CustomUserDetails.class);
		when(userDetails.getEmail()).thenReturn("dealer@example.com");

		Dealer dealer = Dealer.builder()
			.id(1L)
			.email("dealer@example.com")
			.password("encodedPwd")
			.isDeleted(false)
			.master(Master.builder().id(1L).build())
			.build();

		when(dealerRepository.findByEmailAndIsDeletedFalse("dealer@example.com"))
			.thenReturn(Optional.of(dealer));

		when(passwordEncoder.matches("Password1!", "encodedPwd")).thenReturn(true);

		DealerDeletedRequestDto dto = mock(DealerDeletedRequestDto.class);
		when(dto.getPassword()).thenReturn("Password1!");

		// when
		dealerService.deletedDealer(userDetails, dto);

		// then
		assertTrue(dealer.isDeleted());
		assertNotNull(dealer.getDeletedAt());
	}

	@Test
	void updatedRoleChangeDealer_success() {
		// given: 로그인한 마스터 (CustomUserDetails)
		CustomUserDetails masterDetails = mock(CustomUserDetails.class);
		when(masterDetails.getEmail()).thenReturn("master@example.com");

		// given: 마스터 엔티티
		Master master = Master.builder()
			.id(100L)
			.email("master@example.com")
			.build();
		when(masterRepository.findByEmailAndIsDeletedFalse("master@example.com"))
			.thenReturn(Optional.of(master));

		// given: 해당 마스터에 소속된 딜러
		Dealer dealer = Dealer.builder()
			.id(200L)
			.email("dealer@example.com")
			.name("홍길동")
			.role(Role.APPLICANT)
			.master(master) // 마스터에 속해 있어야 함
			.build();
		when(dealerRepository.findByEmailAndIsDeletedFalse("dealer@example.com"))
			.thenReturn(Optional.of(dealer));

		// given: 역할 변경 요청 DTO
		DealerRoleChangeRequestDto dto = mock(DealerRoleChangeRequestDto.class);
		when(dto.getDealerEmail()).thenReturn("dealer@example.com");
		when(dto.getRole()).thenReturn(Role.DEALER); // 유효한 Role

		// when
		dealerService.updatedRoleChangeDealer(masterDetails, dto);

		// then
		assertEquals(Role.DEALER, dealer.getRole()); // 역할이 변경되었는지 확인
	}

	@Test
	void updatedRoleChangeDealer_fail_notBelongToMaster() {
		// given: 로그인한 마스터 (CustomUserDetails)
		CustomUserDetails masterDetails = mock(CustomUserDetails.class);
		when(masterDetails.getEmail()).thenReturn("masterA@example.com");

		// given: 실제 로그인한 마스터
		Master masterA = Master.builder()
			.id(1L)
			.email("masterA@example.com")
			.build();
		when(masterRepository.findByEmailAndIsDeletedFalse("masterA@example.com"))
			.thenReturn(Optional.of(masterA));

		// given: 딜러는 마스터 B 소속
		Master masterB = Master.builder()
			.id(2L)
			.email("masterB@example.com")
			.build();
		Dealer dealer = Dealer.builder()
			.id(10L)
			.email("dealer@example.com")
			.name("딜러1")
			.role(Role.APPLICANT)
			.master(masterB) // 다른 마스터에 소속
			.build();
		when(dealerRepository.findByEmailAndIsDeletedFalse("dealer@example.com"))
			.thenReturn(Optional.of(dealer));

		// given: 역할 변경 요청
		DealerRoleChangeRequestDto dto = mock(DealerRoleChangeRequestDto.class);
		when(dto.getDealerEmail()).thenReturn("dealer@example.com");
		when(dto.getRole()).thenReturn(Role.DEALER);

		// when & then: 예외 발생 검증
		DealerException ex = assertThrows(DealerException.class,
			() -> dealerService.updatedRoleChangeDealer(masterDetails, dto));

		assertEquals(DealerErrorCode.DEALER_NOT_BELONG_TO_MASTER, ex.getBaseCode());
	}

	@Test
	void updatedBranchChangeDealer() {
	}
}