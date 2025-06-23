package com.example.osid.domain.history.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.osid.domain.history.entity.History;

@Repository
public interface HistoryRepository extends JpaRepository<History, Long> {

	Optional<History> findByBodyNumber(String bodyNumber);
}
