package com.example.osid.domain.user.service;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
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
import com.example.osid.domain.dealer.repository.DealerRepository;
import com.example.osid.domain.master.repository.MasterRepository;
import com.example.osid.domain.user.dto.request.UserDeletedRequestDto;
import com.example.osid.domain.user.dto.request.UserSignUpRequestDto;
import com.example.osid.domain.user.dto.request.UserUpdatedRequestDto;
import com.example.osid.domain.user.entity.User;
import com.example.osid.domain.user.repository.UserRepository;

class UserServiceTest {

	@Mock
	private UserRepository userRepository;
	@Mock
	private MasterRepository masterRepository;
	@Mock
	private DealerRepository dealerRepository;
	@Mock
	private PasswordEncoder passwordEncoder;
	@Mock
	private EmailValidator emailValidator;

	@InjectMocks
	private UserService userService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void signUpUser_success() {
		// given: 테스트 입력 DTO를 모킹하고 필드별 반환값 설정
		UserSignUpRequestDto dto = mock(UserSignUpRequestDto.class);
		when(dto.getEmail()).thenReturn("user@example.com");                // 이메일 반환값
		when(dto.getPassword()).thenReturn("12345678aA!");               // 비밀번호 반환값
		when(dto.getName()).thenReturn("홍길동");                         // 이름 반환값
		when(dto.getDateOfBirth()).thenReturn(LocalDate.of(1999, 1, 1));   // 생년월일(LocalDate) 반환값
		when(dto.getPhoneNumber()).thenReturn("010-1234-5678");          // 전화번호 반환값
		when(dto.getAddress()).thenReturn("서울시 어쩌구");             // 주소 반환값

		// 비밀번호 암호화 결과 stub 설정
		when(passwordEncoder.encode("12345678aA!")).thenReturn("encodePwd");

		// when: 실제 서비스 메서드 호출
		userService.signUpUser(dto);

		// then: 이메일 중복 체크가 호출되었는지 검증
		verify(emailValidator).validateDuplicateEmail("user@example.com");

		// 저장된 User 객체를 검사하기 위해 ArgumentCaptor 사용
		ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
		verify(userRepository).save(captor.capture());                    // save() 호출 시 인자 캡처
		User saved = captor.getValue();                                  // 캡처된 User 객체 가져오기

		// 저장된 User 객체의 필드가 올바른지 검증
		assertEquals("user@example.com", saved.getEmail());            // 이메일 검증
		assertEquals("encodePwd", saved.getPassword());         // 암호화된 비밀번호 검증
		assertEquals("홍길동", saved.getName());            // 이름 검증
		assertEquals(LocalDate.of(1999, 1, 1), saved.getDateOfBirth()); // 생년월일 검증
		assertEquals("010-1234-5678", saved.getPhoneNumber());    // 전화번호 검증
		assertEquals("서울시 어쩌구", saved.getAddress());         // 주소 검증
	}

	@Test
	void findbyUser_success() {
		// given: 로그인한 사용자 ID 설정
		CustomUserDetails userDetails = mock(CustomUserDetails.class);
		when(userDetails.getEmail()).thenReturn("user@example.com");

		// given: 사용자 엔티티 모킹
		User user = User.builder()
			.id(1L)
			.email("user@example.com")
			.name("홍길동")
			.password("encodedPwd")
			.dateOfBirth(LocalDate.of(1995, 5, 20))
			.phoneNumber("010-1111-2222")
			.address("서울 강남구")
			.build();
		when(userRepository.findByEmailAndIsDeletedFalse("user@example.com"))
			.thenReturn(Optional.of(user));

		// when
		var result = userService.findbyUser(userDetails);

		// then
		assertEquals(Long.valueOf(1L), result.getId());
		assertEquals("user@example.com", result.getEmail());
		assertEquals("홍길동", result.getName());
		assertEquals(LocalDate.of(1995, 5, 20), result.getDateOfBirth());
		assertEquals("010-1111-2222", result.getPhoneNumber());
		assertEquals("서울 강남구", result.getAddress());
	}

	@Test
	void updatedUser_success() {
		// given
		CustomUserDetails userDetails = mock(CustomUserDetails.class);
		when(userDetails.getId()).thenReturn(1L);

		User user = User.builder()
			.id(1L)
			.name("Old Name")
			.phoneNumber("010-0000-0000")
			.address("Old Address")
			.build();
		when(userRepository.findById(1L)).thenReturn(Optional.of(user));

		UserUpdatedRequestDto dto = mock(UserUpdatedRequestDto.class);
		when(dto.getName()).thenReturn("New Name");
		when(dto.getPhoneNumber()).thenReturn("010-9999-9999");
		when(dto.getAddress()).thenReturn(null); // 주소는 변경 안함

		// when
		userService.updatedUser(userDetails, dto);

		// then
		assertEquals("New Name", user.getName());
		assertEquals("010-9999-9999", user.getPhoneNumber());
		assertEquals("Old Address", user.getAddress());
	}

	@Test
	void deletedUser_success() {
		// given
		CustomUserDetails userDetails = mock(CustomUserDetails.class);
		when(userDetails.getEmail()).thenReturn("user@example.com");

		User user = User.builder()
			.id(1L)
			.email("user@example.com")
			.password("encodedPwd")
			.isDeleted(false)
			.build();
		when(userRepository.findByEmailAndIsDeletedFalse("user@example.com"))
			.thenReturn(Optional.of(user));

		when(passwordEncoder.matches("Password1!", "encodedPwd")).thenReturn(true);

		UserDeletedRequestDto dto = mock(UserDeletedRequestDto.class);
		when(dto.getPassword()).thenReturn("Password1!");

		// when
		userService.deletedUser(userDetails, dto);

		// then
		assertTrue(user.isDeleted());
		assertNotNull(user.getDeletedAt());
	}

}