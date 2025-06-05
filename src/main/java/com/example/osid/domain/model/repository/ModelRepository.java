package com.example.osid.domain.model.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.osid.domain.model.entity.Model;

@Repository
public interface ModelRepository extends JpaRepository<Model, Long>, ModelSearch {

	Page<Model> findAllByDeletedAtIsNull(Pageable pageable);

	Optional<Model> findByIdAndDeletedAtIsNull(Long id);
}
