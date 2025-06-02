package com.example.osid.domain.model.service;

import org.springframework.data.domain.Page;

import com.example.osid.domain.model.dto.ModelCreateRequest;
import com.example.osid.domain.model.dto.ModelResponse;
import com.example.osid.domain.model.dto.ModelUpdateRequest;

public interface ModelService {

	void createModel(ModelCreateRequest request);

	ModelResponse findModel(Long modelId);

	Page<ModelResponse> findAllModel(int page, int size);

	ModelResponse updateModel(Long modelId, ModelUpdateRequest request);

	void deleteModel(Long modelId);
}
