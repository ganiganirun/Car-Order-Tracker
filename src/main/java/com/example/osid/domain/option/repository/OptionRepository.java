package com.example.osid.domain.option.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.osid.domain.option.entity.Option;

@Repository
public interface OptionRepository extends JpaRepository<Option, Long> {

	// option list 반환
	List<Option> findByIdIn(List<Long> ids);

}
