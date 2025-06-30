package com.example.osid.common.request;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CommonPageRequest {

	// 페이징 조회 valid 용도
	private Integer page = 0;
	private Integer size = 10;

	// 기본 페이지 메서드
	// 페이지 사이즈 100 이하로 제한
	public Pageable toPageable() {
		int finalPage = (page != null && page >= 0) ? page : 0;
		int finalSize = (size != null && size >= 1 && size <= 100) ? size : 10;
		return PageRequest.of(finalPage, finalSize, Sort.by(Sort.Direction.DESC, "createdAt"));
	}

	// finalSize 고정
	// model, option 전체 조회같은 인증 없는 조회 전용
	public Pageable toPageableForAnonymousUser() {
		int finalPage = (page != null && page >= 0) ? page : 0;
		int finalSize = 20;

		return PageRequest.of(finalPage, finalSize, Sort.by(Sort.Direction.DESC, "createdAt"));
	}

}
