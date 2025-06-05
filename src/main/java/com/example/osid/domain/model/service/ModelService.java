package com.example.osid.domain.model.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.osid.domain.model.dto.ModelCreateRequest;
import com.example.osid.domain.model.dto.ModelMasterResponse;
import com.example.osid.domain.model.dto.ModelResponse;
import com.example.osid.domain.model.dto.ModelUpdateRequest;

public interface ModelService {

	void createModel(ModelCreateRequest request);

	ModelResponse findModel(Long modelId);

	Page<ModelResponse> findAllModel(Pageable pageable);

	ModelResponse updateModel(Long modelId, ModelUpdateRequest request);

	void deleteModel(Long modelId);

	ModelMasterResponse findModelForMaster(Long modelId);

	Page<ModelMasterResponse> findAllModelForMaster(Pageable pageable, String deletedFilter);
}
