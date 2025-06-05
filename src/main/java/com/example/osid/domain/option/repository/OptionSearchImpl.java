package com.example.osid.domain.option.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import com.example.osid.domain.option.entity.Option;
import com.example.osid.domain.option.entity.QOption;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;

import io.micrometer.common.lang.Nullable;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class OptionSearchImpl extends QuerydslRepositorySupport implements OptionSearch {

	public OptionSearchImpl(JPAQueryFactory jpaQueryFactory) {
		super(Option.class);
		this.jpaQueryFactory = jpaQueryFactory;
	}

	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public Page<Option> findAllOption(Pageable pageable, @Nullable String deletedFilter) {

		QOption option = QOption.option;
		BooleanBuilder builder = new BooleanBuilder();

		// MASTER 인 경우 삭제된 데이터도 조회 가능
		if ("true".equalsIgnoreCase(deletedFilter)) {
			builder.and(option.deletedAt.isNotNull());
		} else if ("false".equalsIgnoreCase(deletedFilter)) {
			builder.and(option.deletedAt.isNull());
		}

		// content 조회
		List<Option> options = jpaQueryFactory
			.selectFrom(option)
			.where(builder)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		// count 쿼리
		Long count = Optional.ofNullable(
			jpaQueryFactory
				.select(option.count())
				.from(option)
				.where(builder)
				.fetchOne()
		).orElse(0L);

		return new PageImpl<>(options, pageable, count);

	}
}
