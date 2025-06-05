package com.example.osid.domain.model.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import com.example.osid.domain.model.entity.Model;
import com.example.osid.domain.model.entity.QModel;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;

import io.micrometer.common.lang.Nullable;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class ModelSearchImpl extends QuerydslRepositorySupport implements ModelSearch {

	public ModelSearchImpl(JPAQueryFactory jpaQueryFactory) {
		super(Model.class);
		this.jpaQueryFactory = jpaQueryFactory;
	}

	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public Page<Model> findAllModel(Pageable pageable, @Nullable String deletedFilter) {

		QModel model = QModel.model;
		BooleanBuilder builder = new BooleanBuilder();

		// MASTER 인 경우 삭제된 데이터도 조회 가능
		if ("true".equalsIgnoreCase(deletedFilter)) {
			builder.and(model.deletedAt.isNotNull());
		} else if ("false".equalsIgnoreCase(deletedFilter)) {
			builder.and(model.deletedAt.isNull());
		}

		// content 조회
		List<Model> models = jpaQueryFactory
			.selectFrom(model)
			.where(builder)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		// count 쿼리
		Long count = Optional.ofNullable(
			jpaQueryFactory
				.select(model.count())
				.from(model)
				.where(builder)
				.fetchOne()
		).orElse(0L);

		return new PageImpl<>(models, pageable, count);

	}
}
