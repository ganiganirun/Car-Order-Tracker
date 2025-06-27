package com.example.osid.domain.option.service;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.osid.domain.option.dto.OptionMasterResponse;
import com.example.osid.domain.option.dto.OptionRequest;
import com.example.osid.domain.option.dto.OptionResponse;
import com.example.osid.domain.option.dto.OptionUpdateRequest;
import com.example.osid.domain.option.entity.Option;
import com.example.osid.domain.option.exception.OptionErrorCode;
import com.example.osid.domain.option.exception.OptionException;
import com.example.osid.domain.option.repository.OptionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OptionServiceImpl implements OptionService {

	private final OptionRepository optionRepository;

	@Override
	@Transactional(value = "dataTransactionManager")
	@PreAuthorize("hasRole('MASTER')")
	public void createOption(OptionRequest request) {
		Option option = new Option(request.getName(), request.getDescription(), request.getImage(),
			request.getCategory(), request.getPrice());
		optionRepository.save(option);
	}

	@Override
	@Transactional(value = "dataTransactionManager", readOnly = true)
	public OptionResponse findOption(Long optionId) {
		Option option = findActiveOption(optionId);
		return OptionResponse.from(option);
	}

	@Override
	@Transactional(value = "dataTransactionManager", readOnly = true)
	@Cacheable(cacheNames = "options", key = "#pageable.pageNumber + '-' + #pageable.pageSize")
	public Page<OptionResponse> findAllOption(Pageable pageable) {
		Page<Option> optionList = optionRepository.findAllByDeletedAtIsNull(pageable);
		return optionList.map(OptionResponse::from);
	}

	@Override
	@Transactional(value = "dataTransactionManager")
	@PreAuthorize("hasRole('MASTER')")
	public OptionResponse updateOption(Long optionId, OptionUpdateRequest request) {
		Option option = findActiveOption(optionId);
		option.updateOption(request);

		return OptionResponse.from(option);
	}

	@Override
	@Transactional(value = "dataTransactionManager")
	@PreAuthorize("hasRole('MASTER')")
	public void deleteOption(Long optionId) {
		Option option = findActiveOption(optionId);
		option.setDeletedAt();
	}

	//master 전용 옵션 단건 조회
	@Override
	@Transactional(value = "dataTransactionManager", readOnly = true)
	@PreAuthorize("hasRole('MASTER')")
	public OptionMasterResponse findOptionForMaster(Long modelId) {
		Option option = optionRepository.findById(modelId)
			.orElseThrow(() -> new OptionException(OptionErrorCode.OPTION_NOT_FOUND));
		return OptionMasterResponse.from(option);
	}

	//master 전용 옵션 전체 조회
	@Override
	@Transactional(value = "dataTransactionManager", readOnly = true)
	@PreAuthorize("hasRole('MASTER')")
	public Page<OptionMasterResponse> findAllOptionForMaster(Pageable pageable, String deletedFilter) {

		Page<Option> optionList = optionRepository.findAllOption(pageable, deletedFilter);
		return optionList.map(OptionMasterResponse::from);
	}

	//삭제되지 않은 모델만 조회
	private Option findActiveOption(Long optionId) {
		return optionRepository.findByIdAndDeletedAtIsNull(optionId)
			.orElseThrow(() -> new OptionException(OptionErrorCode.OPTION_NOT_FOUND));
	}
}
