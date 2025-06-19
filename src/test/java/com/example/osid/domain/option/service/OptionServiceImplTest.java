package com.example.osid.domain.option.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.example.osid.domain.option.dto.OptionMasterResponse;
import com.example.osid.domain.option.dto.OptionRequest;
import com.example.osid.domain.option.dto.OptionResponse;
import com.example.osid.domain.option.dto.OptionUpdateRequest;
import com.example.osid.domain.option.entity.Option;
import com.example.osid.domain.option.enums.OptionCategory;
import com.example.osid.domain.option.exception.OptionErrorCode;
import com.example.osid.domain.option.exception.OptionException;
import com.example.osid.domain.option.repository.OptionRepository;

@ExtendWith(MockitoExtension.class)
class OptionServiceImplTest {

	@Mock
	private OptionRepository optionRepository;

	@InjectMocks
	private OptionServiceImpl optionService;
	private Option option;

	@BeforeEach
	void setUp() {

		option = Option.builder()
			.id(1L)
			.name("테스트 옵션명")
			.description("옵션 설명")
			.deletedAt(null)
			.build();

	}

	@Test
	void 옵션_생성_성공() {
		OptionRequest request = new OptionRequest(
			"추가 옵션", "추가 설명", "/image2.png", OptionCategory.AUTO_PARKING, 1200000L
		);

		optionService.createOption(request);

		verify(optionRepository).save(any(Option.class));
	}

	@Test
	void 옵션_단건_조회_성공() {

		Long optionId = 1L;

		given(optionRepository.findByIdAndDeletedAtIsNull(optionId)).willReturn(Optional.of(option));
		OptionResponse response = optionService.findOption(optionId);
		assertAll(
			() -> assertNotNull(response),
			() -> assertEquals(option.getId(), response.getId()),
			() -> assertEquals(option.getName(), response.getName())
		);

	}

	@Test
	void 옵션_전체_조회_성공() {

		Pageable pageable = PageRequest.of(0, 10);

		List<Option> optionList = List.of(option);
		Page<Option> optionPage = new PageImpl<>(optionList, pageable, optionList.size());

		given(optionRepository.findAllByDeletedAtIsNull(pageable))
			.willReturn(optionPage);

		Page<OptionResponse> responsePage = optionService.findAllOption(pageable);

		assertAll(
			() -> assertNotNull(responsePage),
			() -> assertEquals(1, responsePage.getTotalElements()),
			() -> assertEquals(option.getId(), responsePage.getContent().get(0).getId())
		);

	}

	@Test
	void 옵션_수정_성공() {

		Long optionId = 1L;

		OptionUpdateRequest request = new OptionUpdateRequest("수정된 옵션명", "수정된 옵션설명", "/image.png",
			OptionCategory.AUTO_PARKING, 1200002L);

		given(optionRepository.findByIdAndDeletedAtIsNull(optionId)).willReturn(Optional.of(option));

		OptionResponse response = optionService.updateOption(optionId, request);

		assertAll(
			() -> assertNotNull(response),
			() -> assertEquals(optionId, response.getId()),
			() -> assertEquals("수정된 옵션명", response.getName()),
			() -> assertEquals("수정된 옵션설명", response.getDescription()),
			() -> assertEquals("수정된 옵션명", option.getName()),
			() -> assertEquals("수정된 옵션설명", option.getDescription())
		);

	}

	@Test
	void 옵션_삭제_성공() {

		Long optionId = 1L;

		given(optionRepository.findByIdAndDeletedAtIsNull(optionId)).willReturn(Optional.of(option));

		optionService.deleteOption(optionId);

		assertNotNull(option.getDeletedAt());
	}

	@Test
	void 마스터용_삭제된_옵션_조회_성공() {

		option.setDeletedAt();

		Long optionId = 1L;

		given(optionRepository.findById(optionId)).willReturn(Optional.of(option));

		OptionMasterResponse response = optionService.findOptionForMaster(optionId);

		assertAll(
			() -> assertNotNull(response),
			() -> assertEquals(optionId, response.getId()),
			() -> assertEquals(option.getName(), response.getName())
		);
	}

	@Test
	void 마스터용_모델_전체_조회_성공() {

		option.setDeletedAt();

		Pageable pageable = PageRequest.of(0, 10);
		// 삭제된 데이터 조회할 경우 "true"
		String deletedFilter = "true";

		List<Option> optionList = List.of(option);
		Page<Option> optionPage = new PageImpl<>(optionList, pageable, optionList.size());

		given(optionRepository.findAllOption(pageable, deletedFilter)).willReturn(optionPage);

		Page<OptionMasterResponse> responsePage = optionService.findAllOptionForMaster(pageable, deletedFilter);

		assertAll(
			() -> assertNotNull(responsePage),
			() -> assertEquals(1, responsePage.getTotalElements()),
			() -> assertEquals(option.getId(), responsePage.getContent().get(0).getId())
		);
	}

	@Test
	void 존재하지_않는_옵션_호출시_에러출력() {

		Option anotherOption = Option.builder().id(2L).build();

		given(optionRepository.findByIdAndDeletedAtIsNull(anotherOption.getId())).willReturn(Optional.empty());
		OptionException exception = assertThrows(OptionException.class, () -> {
			optionService.findOption(anotherOption.getId());
		});

		assertEquals(OptionErrorCode.OPTION_NOT_FOUND, exception.getBaseCode());
	}

	@Test
	void 이미_삭제된_옵션_재삭제시_에러출력() {

		option.setDeletedAt();
		Long optionId = 1L;

		given(optionRepository.findByIdAndDeletedAtIsNull(optionId)).willReturn(Optional.of(option));

		OptionException exception = assertThrows(OptionException.class, () -> {
			optionService.deleteOption(optionId);
		});

		assertEquals(OptionErrorCode.OPTION_ALREADY_DELETED, exception.getBaseCode());
	}
}