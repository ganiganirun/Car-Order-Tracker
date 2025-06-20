package com.example.osid.domain.waitingorder.service;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.example.osid.domain.order.dto.OrderPaidEvent;
import com.example.osid.domain.order.entity.Orders;
import com.example.osid.domain.order.exception.OrderErrorCode;
import com.example.osid.domain.order.exception.OrderException;
import com.example.osid.domain.order.repository.OrderRepository;
import com.example.osid.domain.waitingorder.entity.WaitingOrders;
import com.example.osid.domain.waitingorder.repository.WatingOrderRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Component
@Slf4j
public class WatingOrderListener {

	private final WatingOrderRepository watingOrderRepository;
	private final OrderRepository orderRepository;

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void handleWatingOrderSaved(OrderPaidEvent event) {

		Orders orders = orderRepository.findById(event.getOrderId())
			.orElseThrow(() -> new OrderException(OrderErrorCode.ORDER_NOT_FOUND));

		WaitingOrders save = watingOrderRepository.save(new WaitingOrders(orders));

		log.info("save WatinOrders id : {}, orderId : {}, date : {}", save.getId(), save.getOrders().getId(),
			save.getCreatedAt());
	}
}
