package com.example.osid.domain.master.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.osid.domain.master.entity.Master;

@Repository
public interface MasterRepository extends JpaRepository<Master, Long> {

	// Master에서 이메일 검색
	Optional<Master> findByEmail(String email);

	// 삭제되지 않는 Master 조회
	Optional<Master> findByEmailAndIsDeletedFalse(String email);

	List<Master> findByBusinessNumberAndIsDeletedFalse(String businessNumber);

}
