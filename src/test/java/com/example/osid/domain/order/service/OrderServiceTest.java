// package com.example.osid.domain.order.service;
//
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;
// import org.springframework.amqp.rabbit.core.RabbitTemplate;
//
// import com.example.osid.domain.dealer.repository.DealerRepository;
// import com.example.osid.domain.master.repository.MasterRepository;
// import com.example.osid.domain.model.repository.ModelRepository;
// import com.example.osid.domain.option.repository.OptionRepository;
// import com.example.osid.domain.order.repository.OrderRepository;
// import com.example.osid.domain.order.repository.OrderSearch;
// import com.example.osid.domain.user.repository.UserRepository;
// import com.example.osid.event.repository.FailedEventRepository;
//
// @ExtendWith(MockitoExtension.class)
// public class OrderServiceTest {
// 	@Mock
// 	private OrderRepository orderRepository;
// 	@Mock
// 	private OptionRepository optionRepository;
// 	@Mock
// 	private ModelRepository modelRepository;
// 	@Mock
// 	private UserRepository userRepository;
// 	@Mock
// 	private DealerRepository dealerRepository;
// 	@Mock
// 	private OrderSearch orderSearch;
// 	@Mock
// 	private MasterRepository masterRepository;
// 	@Mock
// 	private RabbitTemplate rabbitTemplate;
// 	@Mock
// 	private FailedEventRepository failedEventRepository;
// 	@InjectMocks
// 	private OrderService orderService;
//
// }
