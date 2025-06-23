package com.example.osid.domain.waitingorder.entity;

import com.example.osid.common.entity.BaseEntity;
import com.example.osid.domain.order.entity.Orders;
import com.example.osid.domain.waitingorder.enums.WaitingStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "waiting_orders")
public class WaitingOrders extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "order_id")
	private Orders orders;

	@Enumerated(EnumType.STRING)
	private WaitingStatus waitingStatus;

	public WaitingOrders(Orders orders) {
		this.orders = orders;
		this.waitingStatus = WaitingStatus.WAITING;
	}
}