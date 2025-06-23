package com.example.osid.config.batch;

import java.time.LocalDateTime;

import org.springframework.batch.core.ItemWriteListener;
import org.springframework.batch.item.Chunk;
import org.springframework.stereotype.Component;

import com.example.osid.domain.history.entity.History;
import com.example.osid.domain.order.entity.Orders;
import com.example.osid.domain.order.exception.OrderErrorCode;
import com.example.osid.domain.order.exception.OrderException;
import com.example.osid.domain.order.repository.OrderRepository;
import com.example.osid.domain.waitingorder.entity.WaitingOrders;
import com.example.osid.domain.waitingorder.enums.WaitingStatus;
import com.example.osid.domain.waitingorder.exception.WaitingOrderErrorCode;
import com.example.osid.domain.waitingorder.exception.WaitingOrderException;
import com.example.osid.domain.waitingorder.repository.WaitingOrderRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class HistoryWriteListener implements ItemWriteListener<History> {

	private final WaitingOrderRepository waitingOrderRepository;
	private final OrderRepository orderRepository;

	@Override
	public void afterWrite(Chunk<? extends History> items) {
		for (History history : items) {
			Orders order = orderRepository.findByBodyNumber(history.getBodyNumber())
				.orElseThrow(() -> new OrderException(OrderErrorCode.ORDER_NOT_FOUND));
			WaitingOrders waitingOrder = waitingOrderRepository.findByOrders(order)
				.orElseThrow(() -> new WaitingOrderException(WaitingOrderErrorCode.WAITING_ORDER_NOT_FOUND));

			waitingOrder.setWaitingStatus(WaitingStatus.COMPLETED);
			waitingOrderRepository.save(waitingOrder);

			double totalDuration = history.getTotalDuration().doubleValue(); // ex: 1.5 → 1시간 30분
			int hours = (int)totalDuration;
			int minutes = (int)Math.round((totalDuration - hours) * 60);

			LocalDateTime updatedAt = waitingOrder.getUpdatedAt();
			LocalDateTime expectedAt = updatedAt.plusDays(16).plusHours(hours).plusMinutes(minutes);

			order.setExpectedDeliveryAt(expectedAt);

			orderRepository.save(order);
		}
	}
}
