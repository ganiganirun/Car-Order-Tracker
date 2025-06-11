package com.example.osid.domain.order.service;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.example.osid.common.auth.authentication.JwtUtil;
import com.example.osid.domain.dealer.entity.Dealer;
import com.example.osid.domain.dealer.exception.DealerErrorCode;
import com.example.osid.domain.dealer.exception.DealerException;
import com.example.osid.domain.dealer.repository.DealerRepository;
import com.example.osid.domain.master.entity.Master;
import com.example.osid.domain.master.exception.MasterErrorCode;
import com.example.osid.domain.master.exception.MasterException;
import com.example.osid.domain.master.repository.MasterRepository;
import com.example.osid.domain.order.dto.request.OrderRequestDto;
import com.example.osid.domain.user.entity.User;
import com.example.osid.domain.user.exception.UserErrorCode;
import com.example.osid.domain.user.exception.UserException;
import com.example.osid.domain.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class OrderIntegerationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private DealerRepository dealerRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private MasterRepository masterRepository;

	private String masterAccessToken;
	private Long masterId = 1L;

	private String userAccessToken;
	private Long userId = 1L;

	private String dealerAccessToken;
	private Long dealerId = 1L;

	@BeforeEach
	void setUp() {
		User testUser = userRepository.findById(userId)
			.orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

		userAccessToken = jwtUtil.createToken(
			testUser.getEmail(),
			testUser.getName(),
			testUser.getRole().toString(),
			testUser.getId()
		);

		Dealer testDealer = dealerRepository.findById(userId)
			.orElseThrow(() -> new DealerException(DealerErrorCode.DEALER_NOT_FOUND));

		dealerAccessToken = jwtUtil.createToken(
			testDealer.getEmail(),
			testDealer.getName(),
			testDealer.getRole().toString(),
			testDealer.getId()
		);

		Master testMaster = masterRepository.findById(userId)
			.orElseThrow(() -> new MasterException(MasterErrorCode.MASTER_NOT_FOUND));

		masterAccessToken = jwtUtil.createToken(
			testMaster.getEmail(),
			testMaster.getName(),
			testMaster.getRole().toString(),
			testMaster.getId()
		);

	}

	@Test
	@DisplayName("User 주문생성_접근실패")
	void createOrderAsUser() throws Exception {
		// given
		OrderRequestDto.Add dto = new OrderRequestDto.Add(
			"user01@example.com",
			List.of(1L, 2L, 3L),
			1L,
			"대전시 중구..."
		);

		// when
		ResultActions actions = mockMvc.perform(
			post("/api/dealers/order")
				.header("Authorization", "Bearer " + userAccessToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsString(dto))
		);

		// then
		actions.andExpect(status().isForbidden())
			.andDo(print());
	}

	@Test
	@DisplayName("Master 주문생성_접근실패")
	void createOrderAsMaster() throws Exception {
		// given
		OrderRequestDto.Add dto = new OrderRequestDto.Add(
			"user01@example.com",
			List.of(1L, 2L, 3L),
			1L,
			"대전시 중구..."
		);

		// when
		ResultActions actions = mockMvc.perform(
			post("/api/dealers/order")
				.header("Authorization", "Bearer " + masterAccessToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsString(dto))
		);

		// then
		actions.andExpect(status().isForbidden())
			.andDo(print());
	}

	@Test
	@DisplayName("Dealer 주문생성_성공")
	void createOrderAsDealer() throws Exception {
		// given
		OrderRequestDto.Add dto = new OrderRequestDto.Add(
			"user01@example.com",
			List.of(1L, 2L, 3L),
			1L,
			"대전시 중구..."
		);

		// when
		ResultActions actions = mockMvc.perform(
			post("/api/dealers/order")
				.header("Authorization", "Bearer " + dealerAccessToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsString(dto))
		);

		// then
		actions.andExpect(status().isCreated())
			.andDo(print());
	}

}
