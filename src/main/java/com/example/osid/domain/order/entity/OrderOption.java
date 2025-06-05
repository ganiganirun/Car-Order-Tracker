package com.example.osid.domain.order.entity;

import com.example.osid.domain.option.entity.Option;
import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "order_option")
public class OrderOption {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JsonBackReference
	@JoinColumn(name = "order_id")
	private Orders orders;

	@ManyToOne
	@JoinColumn(name = "option_id")
	private Option option;

	public OrderOption(Orders orders, Option option) {
		this.orders = orders;
		this.option = option;
	}
}
