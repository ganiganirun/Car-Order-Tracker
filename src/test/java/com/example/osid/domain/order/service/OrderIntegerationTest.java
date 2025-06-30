// package com.example.osid.domain.order.service;
//
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
// import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
// import java.util.List;
//
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.openapitools.jackson.nullable.JsonNullable;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.http.MediaType;
// import org.springframework.test.web.servlet.MockMvc;
// import org.springframework.test.web.servlet.ResultActions;
// import org.springframework.transaction.annotation.Transactional;
//
// import com.example.osid.common.auth.authentication.JwtUtil;
// import com.example.osid.domain.dealer.entity.Dealer;
// import com.example.osid.domain.dealer.exception.DealerErrorCode;
// import com.example.osid.domain.dealer.exception.DealerException;
// import com.example.osid.domain.dealer.repository.DealerRepository;
// import com.example.osid.domain.master.entity.Master;
// import com.example.osid.domain.master.exception.MasterErrorCode;
// import com.example.osid.domain.master.exception.MasterException;
// import com.example.osid.domain.master.repository.MasterRepository;
// import com.example.osid.domain.order.dto.request.OrderRequestDto;
// import com.example.osid.domain.user.entity.User;
// import com.example.osid.domain.user.exception.UserErrorCode;
// import com.example.osid.domain.user.exception.UserException;
// import com.example.osid.domain.user.repository.UserRepository;
// import com.fasterxml.jackson.databind.ObjectMapper;
//
// @SpringBootTest
// @Transactional
// @AutoConfigureMockMvc
// public class OrderIntegerationTest {
//
// 	@Autowired
// 	private MockMvc mockMvc;
//
// 	@Autowired
// 	private JwtUtil jwtUtil;
//
// 	@Autowired
// 	private DealerRepository dealerRepository;
//
// 	@Autowired
// 	private UserRepository userRepository;
//
// 	@Autowired
// 	private MasterRepository masterRepository;
//
// 	@Autowired
// 	private ObjectMapper objectMapper;
//
// 	private String masterAccessToken;
// 	private Long masterId = 1L;
//
// 	private String userAccessToken;
// 	private Long userId = 1L;
//
// 	private String dealerAccessToken;
// 	private Long dealerId = 1L;
//
// 	private Long orderId = 1L;
//
// 	@BeforeEach
// 	void setUp() {
// 		User testUser = userRepository.findById(userId)
// 			.orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
//
// 		userAccessToken = jwtUtil.createToken(
// 			testUser.getEmail(),
// 			testUser.getName(),
// 			testUser.getRole().toString(),
// 			testUser.getId()
// 		);
//
// 		Dealer testDealer = dealerRepository.findById(userId)
// 			.orElseThrow(() -> new DealerException(DealerErrorCode.DEALER_NOT_FOUND));
//
// 		dealerAccessToken = jwtUtil.createToken(
// 			testDealer.getEmail(),
// 			testDealer.getName(),
// 			testDealer.getRole().toString(),
// 			testDealer.getId()
// 		);
//
// 		Master testMaster = masterRepository.findById(userId)
// 			.orElseThrow(() -> new MasterException(MasterErrorCode.MASTER_NOT_FOUND));
//
// 		masterAccessToken = jwtUtil.createToken(
// 			testMaster.getEmail(),
// 			testMaster.getName(),
// 			testMaster.getRole().toString(),
// 			testMaster.getId()
// 		);
//
// 	}
//
// 	@Test
// 	@DisplayName("User 주문생성_접근실패")
// 	void createOrderAsUser() throws Exception {
// 		// given
// 		OrderRequestDto.Add dto = new OrderRequestDto.Add(
// 			"user01@example.com",
// 			List.of(1L, 2L, 3L),
// 			1L,
// 			"대전시 중구..."
// 		);
//
// 		// when
// 		ResultActions actions = mockMvc.perform(
// 			post("/api/dealers/order")
// 				.header("Authorization", "Bearer " + userAccessToken)
// 				.contentType(MediaType.APPLICATION_JSON)
// 				.content(new ObjectMapper().writeValueAsString(dto))
// 		);
//
// 		// then
// 		actions.andExpect(status().isForbidden())
// 			.andDo(print());
// 	}
//
// 	@Test
// 	@DisplayName("Master 주문생성_접근실패")
// 	void createOrderAsMaster() throws Exception {
// 		// given
// 		OrderRequestDto.Add dto = new OrderRequestDto.Add(
// 			"user01@example.com",
// 			List.of(1L, 2L, 3L),
// 			1L,
// 			"대전시 중구..."
// 		);
//
// 		// when
// 		ResultActions actions = mockMvc.perform(
// 			post("/api/dealers/order")
// 				.header("Authorization", "Bearer " + masterAccessToken)
// 				.contentType(MediaType.APPLICATION_JSON)
// 				.content(new ObjectMapper().writeValueAsString(dto))
// 		);
//
// 		// then
// 		actions.andExpect(status().isForbidden())
// 			.andDo(print());
// 	}
//
// 	@Test
// 	@DisplayName("Dealer 주문생성_성공")
// 	void createOrderAsDealer() throws Exception {
// 		// given
// 		OrderRequestDto.Add dto = new OrderRequestDto.Add(
// 			"user01@example.com",
// 			List.of(1L, 2L, 3L),
// 			1L,
// 			"대전시 중구..."
// 		);
//
// 		// when
// 		ResultActions actions = mockMvc.perform(
// 			post("/api/dealers/order")
// 				.header("Authorization", "Bearer " + dealerAccessToken)
// 				.contentType(MediaType.APPLICATION_JSON)
// 				.content(new ObjectMapper().writeValueAsString(dto))
// 		);
//
// 		// then
// 		actions.andExpect(status().isCreated())
// 			.andDo(print());
// 	}
//
// 	@Test
// 	@DisplayName("User 주문수정_접근실패")
// 	void updateOrderAsUser() throws Exception {
// 		// given
// 		OrderRequestDto.Update dto = new OrderRequestDto.Update(
// 			JsonNullable.of("서울 강남"),
// 			null,
// 			JsonNullable.undefined(),
// 			JsonNullable.undefined()
// 		);
//
// 		// when
// 		ResultActions actions = mockMvc.perform(
// 			patch("/api/dealers/order/{id}", orderId)
// 				.header("Authorization", "Bearer " + userAccessToken)
// 				.contentType(MediaType.APPLICATION_JSON)
// 				.content(objectMapper.writeValueAsString(dto))
// 		);
//
// 		// then
// 		actions.andExpect(status().isForbidden())
// 			.andDo(print());
// 	}
//
// 	@Test
// 	@DisplayName("Master 주문수정_접근실패")
// 	void updateOrderAsMaster() throws Exception {
// 		// given
// 		OrderRequestDto.Update dto = new OrderRequestDto.Update(
// 			JsonNullable.of("서울 강남"),
// 			null,
// 			JsonNullable.undefined(),
// 			JsonNullable.undefined()
// 		);
//
// 		// when
// 		ResultActions actions = mockMvc.perform(
// 			patch("/api/dealers/order/{id}", orderId)
// 				.header("Authorization", "Bearer " + masterAccessToken)
// 				.contentType(MediaType.APPLICATION_JSON)
// 				.content(objectMapper.writeValueAsString(dto))
// 		);
//
// 		// then
// 		actions.andExpect(status().isForbidden())
// 			.andDo(print());
// 	}
//
// 	@Test
// 	@DisplayName("Dealer 주문수정_성공")
// 	void updateOrderAsDealer() throws Exception {
// 		// given
// 		OrderRequestDto.Update dto = new OrderRequestDto.Update(
// 			JsonNullable.of("서울 강남"),
// 			null,
// 			JsonNullable.undefined(),
// 			JsonNullable.undefined()
// 		);
//
// 		// when
// 		ResultActions actions = mockMvc.perform(
// 			patch("/api/dealers/order/{id}", orderId)
// 				.header("Authorization", "Bearer " + dealerAccessToken)
// 				.contentType(MediaType.APPLICATION_JSON)
// 				.content(objectMapper.writeValueAsString(dto))
// 		);
//
// 		// then
// 		actions
// 			.andDo(print())
// 			.andExpect(status().isOk());
// 	}
//
// 	@Test
// 	@DisplayName("User 주문조회_접근성공")
// 	void findOrderAsUser() throws Exception {
//
// 		// when
// 		ResultActions actions = mockMvc.perform(
// 			get("/api/order/{id}", orderId)
// 				.header("Authorization", "Bearer " + userAccessToken)
// 				.contentType(MediaType.APPLICATION_JSON)
//
// 		);
//
// 		// then
// 		actions.andExpect(status().isOk())
// 			.andDo(print());
// 	}
//
// 	@Test
// 	@DisplayName("Master 주문조회_성공")
// 	void findOrderAsMaster() throws Exception {
//
// 		// when
// 		ResultActions actions = mockMvc.perform(
// 			get("/api/order/{id}", orderId)
// 				.header("Authorization", "Bearer " + masterAccessToken)
// 				.contentType(MediaType.APPLICATION_JSON)
//
// 		);
//
// 		// then
// 		actions.andExpect(status().isOk())
// 			.andDo(print());
// 	}
//
// 	@Test
// 	@DisplayName("Dealer 주문조회_성공")
// 	void findOrderAsDealer() throws Exception {
//
// 		// when
// 		ResultActions actions = mockMvc.perform(
// 			get("/api/order/{id}", orderId)
// 				.header("Authorization", "Bearer " + dealerAccessToken)
// 				.contentType(MediaType.APPLICATION_JSON)
//
// 		);
//
// 		// then
// 		actions
// 			.andDo(print())
// 			.andExpect(status().isOk());
// 	}
//
// 	@Test
// 	@DisplayName("User 주문전체조회_접근성공")
// 	void findAllOrderAsUser() throws Exception {
//
// 		// when
// 		ResultActions actions = mockMvc.perform(
// 			get("/api/order")
// 				.header("Authorization", "Bearer " + userAccessToken)
// 				.contentType(MediaType.APPLICATION_JSON)
//
// 		);
//
// 		// then
// 		actions.andExpect(status().isOk())
// 			.andDo(print());
// 	}
//
// 	@Test
// 	@DisplayName("Master 주문전체조회_성공")
// 	void findAllOrderAsMaster() throws Exception {
//
// 		// when
// 		ResultActions actions = mockMvc.perform(
// 			get("/api/order")
// 				.header("Authorization", "Bearer " + masterAccessToken)
// 				.contentType(MediaType.APPLICATION_JSON)
//
// 		);
//
// 		// then
// 		actions.andExpect(status().isOk())
// 			.andDo(print());
// 	}
//
// 	@Test
// 	@DisplayName("Dealer 주문전체조회_성공")
// 	void findAllOrderAsDealer() throws Exception {
//
// 		// when
// 		ResultActions actions = mockMvc.perform(
// 			get("/api/order")
// 				.header("Authorization", "Bearer " + dealerAccessToken)
// 				.contentType(MediaType.APPLICATION_JSON)
//
// 		);
//
// 		// then
// 		actions
// 			.andDo(print())
// 			.andExpect(status().isOk());
// 	}
//
// 	@Test
// 	@DisplayName("User 주문삭제_접근실패")
// 	void deleteOrderAsUser() throws Exception {
//
// 		// when
// 		ResultActions actions = mockMvc.perform(
// 			delete("/api/dealers/order/{id}", orderId)
// 				.header("Authorization", "Bearer " + userAccessToken)
// 				.contentType(MediaType.APPLICATION_JSON)
//
// 		);
//
// 		// then
// 		actions.andExpect(status().isForbidden())
// 			.andDo(print());
// 	}
//
// 	@Test
// 	@DisplayName("Master 주문삭제_접근실패")
// 	void deleteOrderAsMaster() throws Exception {
//
// 		// when
// 		ResultActions actions = mockMvc.perform(
// 			delete("/api/dealers/order/{id}", orderId)
// 				.header("Authorization", "Bearer " + masterAccessToken)
// 				.contentType(MediaType.APPLICATION_JSON)
//
// 		);
//
// 		// then
// 		actions.andExpect(status().isForbidden())
// 			.andDo(print());
// 	}
//
// 	@Test
// 	@DisplayName("Dealer 주문삭제_성공")
// 	void deleteOrderAsDealer() throws Exception {
//
// 		// when
// 		ResultActions actions = mockMvc.perform(
// 			delete("/api/dealers/order/{id}", orderId)
// 				.header("Authorization", "Bearer " + dealerAccessToken)
// 				.contentType(MediaType.APPLICATION_JSON)
//
// 		);
//
// 		// then
// 		actions
// 			.andDo(print())
// 			.andExpect(status().isOk());
// 	}
//
// }
