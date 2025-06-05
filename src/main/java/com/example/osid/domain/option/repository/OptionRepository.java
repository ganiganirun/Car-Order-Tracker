package com.example.osid.domain.option.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.osid.domain.option.entity.Option;

@Repository
public interface OptionRepository extends JpaRepository<Option, Long>, OptionSearch {

	// option list 반환
	List<Option> findByIdIn(List<Long> ids);

	Optional<Option> findByIdAndDeletedAtIsNull(Long optionId);

	Page<Option> findAllByDeletedAtIsNull(Pageable pageable);
}
