package com.example.osid.domain.model.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.osid.common.request.CommonPageRequest;
import com.example.osid.common.response.CommonResponse;
import com.example.osid.domain.model.dto.ModelCreateRequest;
import com.example.osid.domain.model.dto.ModelMasterResponse;
import com.example.osid.domain.model.dto.ModelResponse;
import com.example.osid.domain.model.dto.ModelUpdateRequest;
import com.example.osid.domain.model.service.ModelServiceImpl;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/model")
public class ModelController {

	private final ModelServiceImpl modelService;

	// get 요청 제외 master 권한 필요

	//모델 생성
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public CommonResponse<Void> createModel(@RequestBody @Valid ModelCreateRequest request) {

		modelService.createModel(request);
		return CommonResponse.created();
	}

	//모델 단건 조회
	@GetMapping("/{modelId}")
	@ResponseStatus(HttpStatus.OK)
	public CommonResponse<ModelResponse> findModel(@PathVariable Long modelId) {

		return CommonResponse.ok(modelService.findModel(modelId));
	}

	//모델 전체 조회
	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public CommonResponse<Page<ModelResponse>> findAllModel(
		@ModelAttribute CommonPageRequest commonPageRequest
	) {

		Pageable pageable = commonPageRequest.toPageableForAnonymousUser();
		return CommonResponse.ok(modelService.findAllModel(pageable));
	}

	//모델 수정
	@PatchMapping("/{modelId}")
	public CommonResponse<ModelResponse> updateModel(@PathVariable Long modelId,
		@RequestBody @Valid ModelUpdateRequest request) {

		return CommonResponse.ok(modelService.updateModel(modelId, request));
	}

	//모델 삭제(soft deleted)
	@ResponseStatus(HttpStatus.OK)
	@DeleteMapping("/{modelId}")
	public CommonResponse<Void> deleteModel(@PathVariable Long modelId) {
		modelService.deleteModel(modelId);
		return CommonResponse.ok();
	}

	// master 전용 모델 단건 조회
	@GetMapping("/master/{modelId}")
	@ResponseStatus(HttpStatus.OK)
	public CommonResponse<ModelMasterResponse> findModelForMaster(@PathVariable Long modelId) {

		return CommonResponse.ok(modelService.findModelForMaster(modelId));
	}

	// master 전용 모델 전체 조회
	@GetMapping("/master")
	@ResponseStatus(HttpStatus.OK)
	public CommonResponse<Page<ModelMasterResponse>> findAllModelForMaster(
		@ModelAttribute CommonPageRequest commonPageRequest,
		@RequestParam(required = false, defaultValue = "all", name = "deleted") String deletedFilter
	) {
		Pageable pageable = commonPageRequest.toPageable();
		return CommonResponse.ok(modelService.findAllModelForMaster(pageable, deletedFilter));
	}

}
