package com.example.osid.domain.option.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.osid.domain.option.entity.Option;

import io.micrometer.common.lang.Nullable;

public interface OptionSearch {

	Page<Option> findAllOption(Pageable pageable, @Nullable String deletedFilter);
}
