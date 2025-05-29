package com.example.osid.domain.order.service;

import org.springframework.stereotype.Service;

import com.example.osid.domain.dealer.repository.DealerRepository;
import com.example.osid.domain.model.repository.ModelRepository;
import com.example.osid.domain.option.repository.OptionRepository;
import com.example.osid.domain.order.dto.request.OrderRequestDto;
import com.example.osid.domain.order.repository.OrderOptionRepository;
import com.example.osid.domain.order.repository.OrderRepository;
import com.example.osid.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService {

	private final OrderRepository orderRepository;
	private final OptionRepository optionRepository;
	private final ModelRepository modelRepository;
	private final OrderOptionRepository orderOptionRepository;
	private final UserRepository userRepository;
	private final DealerRepository dealerRepository;

	public void createOrder(Long dealerId, OrderRequestDto.Add requestDto) {
		/*
		 * dealerId로 딜러 가져오기
		 * 유저 이메일로 유저 가져오기
		 * 옵션 리스트로 옵션에서 찾아옴
		 * 모델 id으로 모델 객체 가져와야함
		 * 옵션리스트로 총가격 계산하고
		 * order 객체 저장
		 * orderoption 생성 및 저장
		 *
		 * */

		// Optional<Dealer> byId = dealerRepository.findById(dealerId);
		// Optional<User> byEmail = userRepository.findByEmail(requestDto.getUserEmail());
		// Optional<Model> byId = modelRepository.findById(requestDto.getModelId());
		// optionRepository.findByIdIn(requestDto.getOption());
	}
}
