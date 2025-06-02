package com.example.osid.domain.model.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.osid.domain.model.dto.ModelCreateRequest;
import com.example.osid.domain.model.dto.ModelResponse;
import com.example.osid.domain.model.dto.ModelUpdateRequest;
import com.example.osid.domain.model.entity.Model;
import com.example.osid.domain.model.exception.ModelErrorCode;
import com.example.osid.domain.model.exception.ModelException;
import com.example.osid.domain.model.repository.ModelRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ModelServiceImpl implements ModelService {

	private final ModelRepository modelRepository;

	//모델 생성
	@Override
	@Transactional
	public void createModel(ModelCreateRequest request) {
		Model model = new Model(request.getName(), request.getColor(), request.getDescription(), request.getImage(),
			request.getCategory(),
			request.getSeatCount(), request.getPrice());

		modelRepository.save(model);
	}

	//모델 단건 조회
	@Transactional(readOnly = true)
	public ModelResponse findModel(Long modelId) {

		Model model = findActiveModel(modelId);
		return ModelResponse.from(model);
	}

	//모델 전체 조회
	@Transactional(readOnly = true)
	public Page<ModelResponse> findAllModel(int page, int size) {
		Pageable pageable = PageRequest.of(page - 1, size);
		Page<Model> modelList = modelRepository.findAllByDeletedAtIsNull(pageable);
		return modelList.map(ModelResponse::from);
	}

	//모델 수정
	@Transactional
	public ModelResponse updateModel(Long modelId, ModelUpdateRequest request) {

		Model model = findActiveModel(modelId);
		model.updateModel(request);

		return ModelResponse.from(model);
	}

	//모델 삭제 조회
	@Transactional
	public void deleteModel(Long modelId) {
		Model model = findActiveModel(modelId);
		model.setDeletedAt();
	}

	//삭제되지 않은 모델만 조회
	private Model findActiveModel(Long modelId) {
		return modelRepository.findByIdAndDeletedAtIsNull(modelId)
			.orElseThrow(() -> new ModelException(ModelErrorCode.MODEL_NOT_FOUND));
	}
}
