package com.example.osid.domain.user.service;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.osid.common.auth.EmailValidator;
import com.example.osid.domain.dealer.repository.DealerRepository;
import com.example.osid.domain.master.repository.MasterRepository;
import com.example.osid.domain.user.dto.request.UserSignUpRequestDto;
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
	void findbyUser() {
	}

	@Test
	void updatedUser() {
	}

	@Test
	void deletedUser() {
	}
}