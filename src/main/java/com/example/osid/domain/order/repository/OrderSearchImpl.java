package com.example.osid.domain.order.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import com.example.osid.common.entity.enums.Role;
import com.example.osid.common.exception.CustomException;
import com.example.osid.common.exception.ErrorCode;
import com.example.osid.config.QuerydslUtils;
import com.example.osid.domain.order.entity.Orders;
import com.example.osid.domain.order.entity.QOrders;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class OrderSearchImpl extends QuerydslRepositorySupport implements OrderSearch {

	public OrderSearchImpl(JPAQueryFactory jpaQueryFactory) {
		super(Orders.class);
		this.jpaQueryFactory = jpaQueryFactory;

	}

	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public Page<Orders> findOrderAllForUserOrDealer(Role role, Pageable pageable, Long id) {

		QOrders orders = QOrders.orders;

		BooleanBuilder predicate = new BooleanBuilder();

		switch (role) {
			case USER -> predicate.and(orders.user.id.eq(id)); // 자신이 주문한 건에 대해서만 전체 조회 가능

			case DEALER -> predicate.and(orders.dealer.id.eq(id)); // 자신이 주문한 건에 대해서만 전체 조회 가능

			default -> throw new CustomException(ErrorCode.FORBIDDEN); // 정의되지 않은 역할
		}

		List<Orders> pagingOrders = jpaQueryFactory
			.select(orders)
			.from(orders)
			.where(predicate)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.orderBy(QuerydslUtils.getSort(pageable, orders))
			.fetch();

		long total = jpaQueryFactory
			.selectFrom(orders)
			.where(predicate)
			.fetchCount();

		return new PageImpl<>(pagingOrders, pageable, total);
	}

	@Override
	public Page<Orders> findOrderAllForMaster(Role role, Pageable pageable, List<Long> dealerIds) {

		QOrders orders = QOrders.orders;

		BooleanBuilder predicate = new BooleanBuilder();

		if (role == Role.MASTER && dealerIds != null && !dealerIds.isEmpty()) {
			predicate.and(orders.dealer.id.in(dealerIds));
		} else {
			return Page.empty(pageable);
		}

		List<Orders> pagingOrders = jpaQueryFactory
			.select(orders)
			.from(orders)
			.where(predicate)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.orderBy(QuerydslUtils.getSort(pageable, orders))
			.fetch();

		long total = jpaQueryFactory
			.selectFrom(orders)
			.where(predicate)
			.fetchCount();

		return new PageImpl<>(pagingOrders, pageable, total);
	}

}
