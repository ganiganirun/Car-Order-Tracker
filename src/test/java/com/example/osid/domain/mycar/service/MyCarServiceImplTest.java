package com.example.osid.domain.mycar.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.example.osid.common.auth.CustomUserDetails;
import com.example.osid.common.entity.enums.Role;
import com.example.osid.domain.model.entity.Model;
import com.example.osid.domain.model.enums.ModelColor;
import com.example.osid.domain.mycar.dto.MyCarListResponse;
import com.example.osid.domain.mycar.dto.MyCarResponse;
import com.example.osid.domain.mycar.entity.Mycar;
import com.example.osid.domain.mycar.exception.MyCarErrorCode;
import com.example.osid.domain.mycar.exception.MyCarException;
import com.example.osid.domain.mycar.repository.MycarRepository;
import com.example.osid.domain.option.entity.Option;
import com.example.osid.domain.order.entity.OrderOption;
import com.example.osid.domain.order.entity.Orders;
import com.example.osid.domain.order.enums.OrderStatus;
import com.example.osid.domain.order.repository.OrderRepository;
import com.example.osid.domain.user.entity.User;

@ExtendWith(MockitoExtension.class)
class MyCarServiceImplTest {

	@Mock
	private MycarRepository mycarRepository;
	@Mock
	private OrderRepository orderRepository;

	@InjectMocks
	private MyCarServiceImpl myCarService;
	private User user;
	private CustomUserDetails customUserDetails;
	private Orders orders;
	private Mycar myCar;
	private User user2;
	private Mycar myCar2;
	private Orders orders2;
	private Model model;
	private Option option;
	private List<OrderOption> orderOptionList;

	@BeforeEach
	void setUp() {

		user = User.builder()
			.id(1L)
			.isDeleted(false)
			.role(Role.USER)
			.build();

		model = Model.builder()
			.id(1L)
			.name("테스트 모델명")
			.color(ModelColor.RED)
			.description("모델 설명")
			.deletedAt(null)
			.build();

		option = Option.builder()
			.id(1L)
			.name("테스트 옵션명")
			.description("옵션 설명")
			.deletedAt(null)
			.build();

		orderOptionList = List.of(
			OrderOption.builder()
				.id(1L)
				.option(option)
				.orders(orders)
				.build()
		);

		orders = Orders.builder().
			id(1L)
			.user(user)
			.model(model)
			.orderOptions(orderOptionList)
			.bodyNumber("test_body_number")
			.orderStatus(OrderStatus.COMPLETED)
			.build();

		myCar = Mycar.builder()
			.id(1L)
			.orders(orders)
			.user(user)
			.deletedAt(null)
			.build();

		user2 = User.builder()
			.id(2L)
			.email("userMail@email.com")
			.password("user_password")
			.isDeleted(false)
			.role(Role.USER)
			.build();

		orders2 = Orders.builder().
			id(2L)
			.user(user2)
			.orderStatus(OrderStatus.ORDERED)
			.build();

		myCar2 = Mycar.builder()
			.id(2L)
			.user(user2)
			.deletedAt(null)
			.build();

		customUserDetails = CustomUserDetails.fromUser(user);

	}

	@Test
	@DisplayName("내 차량 단일 조회 성공")
	void findMyCar_success() {

		Long myCarId = 1L;
		given(mycarRepository.findByIdAndDeletedAtIsNull(myCar.getId())).willReturn(Optional.of(myCar));
		MyCarResponse response = myCarService.findMyCar(customUserDetails, myCarId);

		assertAll(
			() -> assertNotNull(response),
			() -> assertEquals(myCar.getOrders().getBodyNumber(), response.getBodyNumber())
		);
	}

	@Test
	@DisplayName("내 차량 전체 조회 성공")
	void findAllMyCar_success() {

		Pageable pageable = PageRequest.of(0, 10);

		List<Mycar> myCarList = List.of(myCar);
		Page<Mycar> myCarPage = new PageImpl<>(myCarList, pageable, myCarList.size());

		given(mycarRepository.findAllByUserIdAndDeletedAtIsNull(customUserDetails.getId(), pageable))
			.willReturn(myCarPage);

		Page<MyCarListResponse> responsePage = myCarService.findAllMyCar(customUserDetails, pageable);

		assertAll(
			() -> assertNotNull(responsePage),
			() -> assertEquals(1, responsePage.getTotalElements()),
			() -> assertEquals(myCar.getOrders().getModel().getName(), responsePage.getContent().get(0).getMyCarName())
		);
	}

	@Test
	@DisplayName("내 차량 삭제 성공")
	void deleteMyCar_success() {

		Long myCarId = 1L;

		given(mycarRepository.findByIdAndDeletedAtIsNull(myCarId)).willReturn(Optional.of(myCar));

		myCarService.deleteMyCar(customUserDetails, myCarId);

		assertNotNull(myCar.getDeletedAt());
	}

	@Test
	@DisplayName("내 소유가 아닌 myCar 호출시 에러 출력")
	void findMyCarError() {

		// 로그인한 유저 = 1
		// 호출한 myCar = 2
		Long myCarId = 2L;
		given(mycarRepository.findByIdAndDeletedAtIsNull(myCarId)).willReturn(Optional.of(myCar2));

		MyCarException ex = assertThrows(MyCarException.class, () -> {
			myCarService.findMyCar(customUserDetails, myCarId);
		});

		assertEquals(MyCarErrorCode.MY_CAR_NOT_OWED, ex.getBaseCode());

	}

	@Test
	@DisplayName("이미 등록된 차량 등록시 에러 출력")
	void createMyCar_Error() {

		given(orderRepository.findWithOptionsById(orders.getId())).willReturn(Optional.of(orders));
		given(mycarRepository.existsByOrdersId(orders.getId())).willReturn(true);

		MyCarException ex = assertThrows(MyCarException.class, () -> {
			myCarService.saveMyCar(orders.getId());
		});

		assertEquals(MyCarErrorCode.CAR_ALREADY_OWNED, ex.getBaseCode());

	}

}