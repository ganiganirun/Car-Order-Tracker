package com.example.osid.domain.model.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.osid.domain.model.entity.Model;

import io.micrometer.common.lang.Nullable;

public interface ModelSearch {

	Page<Model> findAllModel(Pageable pageable, @Nullable String deletedFilter);
}
