package com.example.osid.domain.order.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import com.example.osid.common.auth.CustomUserDetails;
import com.example.osid.domain.dealer.entity.Dealer;
import com.example.osid.domain.dealer.exception.DealerErrorCode;
import com.example.osid.domain.dealer.exception.DealerException;
import com.example.osid.domain.dealer.repository.DealerRepository;
import com.example.osid.domain.master.repository.MasterRepository;
import com.example.osid.domain.model.entity.Model;
import com.example.osid.domain.model.enums.ModelCategory;
import com.example.osid.domain.model.enums.ModelColor;
import com.example.osid.domain.model.exception.ModelErrorCode;
import com.example.osid.domain.model.exception.ModelException;
import com.example.osid.domain.model.repository.ModelRepository;
import com.example.osid.domain.option.entity.Option;
import com.example.osid.domain.option.enums.OptionCategory;
import com.example.osid.domain.option.exception.OptionErrorCode;
import com.example.osid.domain.option.exception.OptionException;
import com.example.osid.domain.option.repository.OptionRepository;
import com.example.osid.domain.order.dto.request.OrderRequestDto;
import com.example.osid.domain.order.entity.OrderOption;
import com.example.osid.domain.order.entity.Orders;
import com.example.osid.domain.order.enums.OrderStatus;
import com.example.osid.domain.order.repository.OrderRepository;
import com.example.osid.domain.order.repository.OrderSearch;
import com.example.osid.domain.user.entity.User;
import com.example.osid.domain.user.exception.UserErrorCode;
import com.example.osid.domain.user.exception.UserException;
import com.example.osid.domain.user.repository.UserRepository;
import com.example.osid.event.repository.FailedEventRepository;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {
	@Mock
	private OrderRepository orderRepository;
	@Mock
	private OptionRepository optionRepository;
	@Mock
	private ModelRepository modelRepository;
	@Mock
	private UserRepository userRepository;
	@Mock
	private DealerRepository dealerRepository;
	@Mock
	private OrderSearch orderSearch;
	@Mock
	private MasterRepository masterRepository;
	@Mock
	private RabbitTemplate rabbitTemplate;
	@Mock
	private FailedEventRepository failedEventRepository;
	@InjectMocks
	private OrderService orderService;

	@Test
	@DisplayName("딜러를 찾을 수 없음")
	void dealerNotFound() {

		// given
		String dealerEmail = "nodealer@example.com";

		Dealer fakeDealer = Dealer.builder()
			.id(99L)
			.email(dealerEmail)
			.password("dummy")
			.build();
		CustomUserDetails userDetails = CustomUserDetails.fromDealer(fakeDealer);

		OrderRequestDto.Add requestDto = new OrderRequestDto.Add(
			"user@example.com",
			List.of(1L, 2L),
			1L,
			"서울시 강남구"
		);

		given(dealerRepository.findByEmailAndIsDeletedFalse(dealerEmail))
			.willReturn(Optional.empty());

		// when
		DealerException ex = assertThrows(
			DealerException.class,
			() -> orderService.createOrder(userDetails, requestDto)
		);

		// then
		assertEquals(DealerErrorCode.DEALER_NOT_FOUND, ex.getBaseCode());

	}

	@Test
	@DisplayName("유저를 찾을 수 없음")
	void userNotFound() {

		// given
		String dealerEmail = "nodealer@example.com";

		Dealer fakeDealer = Dealer.builder()
			.id(99L)
			.email(dealerEmail)
			.password("dummy")
			.build();
		CustomUserDetails userDetails = CustomUserDetails.fromDealer(fakeDealer);

		OrderRequestDto.Add requestDto = new OrderRequestDto.Add(
			"user@example.com",
			List.of(1L, 2L),
			1L,
			"서울시 강남구"
		);

		given(dealerRepository.findByEmailAndIsDeletedFalse(dealerEmail))
			.willReturn(Optional.of(fakeDealer));

		given(userRepository.findByEmailAndIsDeletedFalse(requestDto.getUserEmail()))
			.willReturn(Optional.empty());

		// when
		UserException ex = assertThrows(
			UserException.class,
			() -> orderService.createOrder(userDetails, requestDto)
		);

		// then
		assertEquals(UserErrorCode.USER_NOT_FOUND, ex.getBaseCode());

	}

	@Test
	@DisplayName("모델을 찾을 수 없음")
	void modelNotFound() {

		// given
		String dealerEmail = "nodealer@example.com";
		String userEmail = "user@example.com";

		Dealer fakeDealer = Dealer.builder()
			.id(99L)
			.email(dealerEmail)
			.password("dummy")
			.build();
		CustomUserDetails userDetails = CustomUserDetails.fromDealer(fakeDealer);

		User fakeUser = User.builder()
			.id(99L)
			.email(userEmail)
			.password("dummy")
			.build();

		OrderRequestDto.Add requestDto = new OrderRequestDto.Add(
			"user@example.com",
			List.of(1L, 2L),
			1L,
			"서울시 강남구"
		);

		given(dealerRepository.findByEmailAndIsDeletedFalse(dealerEmail))
			.willReturn(Optional.of(fakeDealer));

		given(userRepository.findByEmailAndIsDeletedFalse(requestDto.getUserEmail()))
			.willReturn(Optional.of(fakeUser));

		given(modelRepository.findById(requestDto.getModelId()))
			.willReturn(Optional.empty());

		// when
		ModelException ex = assertThrows(
			ModelException.class,
			() -> orderService.createOrder(userDetails, requestDto)
		);

		// then
		assertEquals(ModelErrorCode.MODEL_NOT_FOUND, ex.getBaseCode());

	}

	@Test
	@DisplayName("옵션을 찾을 수 없음")
	void optionNotFound() {

		// given
		String dealerEmail = "nodealer@example.com";
		String userEmail = "user@example.com";

		Dealer fakeDealer = Dealer.builder()
			.id(99L)
			.email(dealerEmail)
			.password("dummy")
			.build();

		CustomUserDetails userDetails = CustomUserDetails.fromDealer(fakeDealer);

		User fakeUser = User.builder()
			.id(99L)
			.email(userEmail)
			.password("dummy")
			.build();

		Model fakeModel = Model.builder()
			.id(1L)
			.name("모델1")
			.color(ModelColor.RED)
			.description("설명")
			.image("/image.png")
			.category(ModelCategory.ELECTRIC)
			.seatCount("5")
			.price(100000000L)
			.build();

		OrderRequestDto.Add requestDto = new OrderRequestDto.Add(
			"user@example.com",
			List.of(1L, 2L),
			1L,
			"서울시 강남구"
		);

		given(dealerRepository.findByEmailAndIsDeletedFalse(dealerEmail))
			.willReturn(Optional.of(fakeDealer));

		given(userRepository.findByEmailAndIsDeletedFalse(requestDto.getUserEmail()))
			.willReturn(Optional.of(fakeUser));

		given(modelRepository.findById(requestDto.getModelId()))
			.willReturn(Optional.of(fakeModel));

		given(optionRepository.findByIdIn(requestDto.getOption()))
			.willReturn(List.of());

		// when
		OptionException ex = assertThrows(
			OptionException.class,
			() -> orderService.createOrder(userDetails, requestDto)
		);

		// then
		assertEquals(OptionErrorCode.OPTION_NOT_FOUND, ex.getBaseCode());

	}

	@Test
	@DisplayName("주문 생성 성공")
	void successCreateOrder() {

		// given
		String dealerEmail = "nodealer@example.com";
		String userEmail = "user@example.com";

		Dealer fakeDealer = Dealer.builder()
			.id(99L)
			.email(dealerEmail)
			.password("dummy")
			.build();

		CustomUserDetails userDetails = CustomUserDetails.fromDealer(fakeDealer);

		User fakeUser = User.builder()
			.id(99L)
			.email(userEmail)
			.password("dummy")
			.build();

		Model fakeModel = Model.builder()
			.id(1L)
			.name("모델1")
			.color(ModelColor.RED)
			.description("설명")
			.image("/image.png")
			.category(ModelCategory.ELECTRIC)
			.seatCount("5")
			.price(100000000L)
			.build();

		Option option1 = Option.builder()
			.id(1L)
			.name("옵션1")
			.description("설명1")
			.image("image.png")
			.category(OptionCategory.ADAPTIVE_HEADLIGHTS)
			.price(100000L)
			.build();

		Option option2 = Option.builder()
			.id(2L)
			.name("옵션2")
			.description("설명2")
			.image("image.png")
			.category(OptionCategory.DASHCAM)
			.price(100000L)
			.build();

		OrderRequestDto.Add requestDto = new OrderRequestDto.Add(
			"user@example.com",
			List.of(1L, 2L),
			1L,
			"서울시 강남구"
		);

		Orders orders = Orders.builder()
			.address(requestDto.getAddress())
			.totalPrice(fakeModel.getPrice() + option1.getPrice() + option2.getPrice())
			.orderStatus(OrderStatus.ORDERED)
			.user(fakeUser)
			.dealer(fakeDealer)
			.model(fakeModel)
			.orderOptions(List.of(
				new OrderOption(null, option1),
				new OrderOption(null, option2)
			))
			.build();

		given(dealerRepository.findByEmailAndIsDeletedFalse(dealerEmail))
			.willReturn(Optional.of(fakeDealer));

		given(userRepository.findByEmailAndIsDeletedFalse(requestDto.getUserEmail()))
			.willReturn(Optional.of(fakeUser));

		given(modelRepository.findById(requestDto.getModelId()))
			.willReturn(Optional.of(fakeModel));

		given(optionRepository.findByIdIn(requestDto.getOption()))
			.willReturn(List.of(option1, option2));

		given(orderRepository.save(any(Orders.class)))
			.willReturn(orders);

		// when
		assertDoesNotThrow(() -> orderService.createOrder(userDetails, requestDto));

	}

}
