package com.example.osid.domain.model.service;

import static org.junit.jupiter.api.Assertions.*;
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

import com.example.osid.domain.model.dto.ModelCreateRequest;
import com.example.osid.domain.model.dto.ModelMasterResponse;
import com.example.osid.domain.model.dto.ModelResponse;
import com.example.osid.domain.model.dto.ModelUpdateRequest;
import com.example.osid.domain.model.entity.Model;
import com.example.osid.domain.model.enums.ModelCategory;
import com.example.osid.domain.model.enums.ModelColor;
import com.example.osid.domain.model.exception.ModelErrorCode;
import com.example.osid.domain.model.exception.ModelException;
import com.example.osid.domain.model.repository.ModelRepository;

@ExtendWith(MockitoExtension.class)
class ModelServiceImplTest {

	@Mock
	private ModelRepository modelRepository;

	@InjectMocks
	private ModelServiceImpl modelService;
	private Model model;

	@BeforeEach
	void setUp() {

		model = Model.builder()
			.id(1L)
			.name("테스트 모델명")
			.color(ModelColor.RED)
			.description("모델 설명")
			.deletedAt(null)
			.build();

	}

	@Test
	void 모델_생성_성공() {
		ModelCreateRequest request = new ModelCreateRequest(
			"추가 모델", ModelColor.RED, "추가 설명", "/image2.png", ModelCategory.SEDAN, "4", 5000000L
		);

		modelService.createModel(request);

		verify(modelRepository).save(any(Model.class));
	}

	@Test
	void 모델_단건_조회_성공() {

		Long modelId = 1L;

		given(modelRepository.findByIdAndDeletedAtIsNull(modelId)).willReturn(Optional.of(model));
		ModelResponse response = modelService.findModel(modelId);
		assertAll(
			() -> assertNotNull(response),
			() -> assertEquals(model.getId(), response.getId()),
			() -> assertEquals(model.getName(), response.getName())
		);

	}

	@Test
	void 모델_전체_조회_성공() {

		Pageable pageable = PageRequest.of(0, 10);

		List<Model> modelList = List.of(model);
		Page<Model> modelPage = new PageImpl<>(modelList, pageable, modelList.size());

		given(modelRepository.findAllByDeletedAtIsNull(pageable))
			.willReturn(modelPage);

		Page<ModelResponse> responsePage = modelService.findAllModel(pageable);

		assertAll(
			() -> assertNotNull(responsePage),
			() -> assertEquals(1, responsePage.getTotalElements()),
			() -> assertEquals(model.getName(), responsePage.getContent().get(0).getName()),
			() -> assertEquals(model.getColor(), responsePage.getContent().get(0).getColor())
		);

	}

	@Test
	void 모델_수정_성공() {

		Long modelId = 1L;

		ModelUpdateRequest request = new ModelUpdateRequest("수정된 모델명", ModelColor.RED, "수정된 모델설명", "/image.png",
			ModelCategory.SUV, "7", 1200000L);

		given(modelRepository.findByIdAndDeletedAtIsNull(modelId)).willReturn(Optional.of(model));

		ModelResponse response = modelService.updateModel(modelId, request);

		assertAll(
			() -> assertNotNull(response),
			() -> assertEquals(modelId, response.getId()),
			() -> assertEquals("수정된 모델명", response.getName()),
			() -> assertEquals("수정된 모델설명", response.getDescription()),
			() -> assertEquals("수정된 모델명", model.getName()),
			() -> assertEquals("수정된 모델설명", model.getDescription())
		);

	}

	@Test
	void 모델_삭제_성공() {

		Long modelId = 1L;

		given(modelRepository.findByIdAndDeletedAtIsNull(modelId)).willReturn(Optional.of(model));

		modelService.deleteModel(modelId);

		assertNotNull(model.getDeletedAt());
	}

	@Test
	void 마스터용_삭제된_모델_조회_성공() {

		model.setDeletedAt();

		Long modelId = model.getId();
		given(modelRepository.findById(modelId)).willReturn(Optional.of(model));

		ModelMasterResponse response = modelService.findModelForMaster(modelId);

		assertAll(
			() -> assertNotNull(response),
			() -> assertEquals(modelId, response.getId()),
			() -> assertEquals(model.getName(), response.getName())
		);
	}

	@Test
	void 마스터용_모델_전체_조회_성공() {

		Pageable pageable = PageRequest.of(0, 10);
		// 삭제된 데이터 조회할 경우 "true"
		String deletedFilter = "true";

		List<Model> modelList = List.of(model);
		Page<Model> modelPage = new PageImpl<>(modelList, pageable, modelList.size());

		given(modelRepository.findAllModel(pageable, deletedFilter)).willReturn(modelPage);

		Page<ModelMasterResponse> responsePage = modelService.findAllModelForMaster(pageable, deletedFilter);

		assertAll(
			() -> assertNotNull(responsePage),
			() -> assertEquals(1, responsePage.getTotalElements()),
			() -> assertEquals(model.getName(), responsePage.getContent().get(0).getName())
		);
	}

	@Test
	void 존재하지_않는_모델_호출시_에러출력() {

		Model anotherModel = Model.builder().id(2L).build();

		given(modelRepository.findByIdAndDeletedAtIsNull(anotherModel.getId())).willReturn(Optional.empty());
		ModelException exception = assertThrows(ModelException.class, () -> {
			modelService.findModel(anotherModel.getId());
		});

		assertEquals(ModelErrorCode.MODEL_NOT_FOUND, exception.getBaseCode());
	}

	@Test
	void 이미_삭제된_모델_삭제시_에러출력() {

		model.setDeletedAt();

		given(modelRepository.findByIdAndDeletedAtIsNull(model.getId())).willReturn(Optional.of(model));

		ModelException exception = assertThrows(ModelException.class, () -> {
			modelService.deleteModel(model.getId());
		});

		assertEquals(ModelErrorCode.MODEL_ALREADY_DELETED, exception.getBaseCode());
	}

}