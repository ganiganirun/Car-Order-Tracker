package com.example.osid.domain.option.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.osid.domain.option.dto.OptionMasterResponse;
import com.example.osid.domain.option.dto.OptionRequest;
import com.example.osid.domain.option.dto.OptionResponse;
import com.example.osid.domain.option.dto.OptionUpdateRequest;

public interface OptionService {

	void createOption(OptionRequest request);

	OptionResponse findOption(Long optionId);

	Page<OptionResponse> findAllOption(Pageable pageable);

	OptionResponse updateOption(Long optionId, OptionUpdateRequest request);

	void deleteOption(Long optionId);

	OptionMasterResponse findOptionForMaster(Long optionId);

	Page<OptionMasterResponse> findAllOptionForMaster(Pageable pageable, String deletedFilter);
}
