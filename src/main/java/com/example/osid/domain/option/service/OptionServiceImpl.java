package com.example.osid.domain.option.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
	@Transactional
	public void createOption(OptionRequest request) {
		Option option = new Option(request.getName(), request.getDescription(), request.getImage(),
			request.getCategory(), request.getPrice());
		optionRepository.save(option);
	}

	@Override
	@Transactional(readOnly = true)
	public OptionResponse findOption(Long optionId) {
		Option option = findActiveOption(optionId);
		return OptionResponse.from(option);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<OptionResponse> findAllOption(Pageable pageable) {
		Page<Option> optionList = optionRepository.findAllByDeletedAtIsNull(pageable);
		return optionList.map(OptionResponse::from);
	}

	@Override
	@Transactional
	public OptionResponse updateOption(Long optionId, OptionUpdateRequest request) {
		Option option = findActiveOption(optionId);
		option.updateOption(request);

		return OptionResponse.from(option);
	}

	@Override
	@Transactional
	public void deleteOption(Long optionId) {
		Option option = findActiveOption(optionId);
		option.setDeletedAt();
	}

	//삭제되지 않은 모델만 조회
	private Option findActiveOption(Long optionId) {
		return optionRepository.findByIdAndDeletedAtIsNull(optionId)
			.orElseThrow(() -> new OptionException(OptionErrorCode.OPTION_NOT_FOUND));
	}
}
