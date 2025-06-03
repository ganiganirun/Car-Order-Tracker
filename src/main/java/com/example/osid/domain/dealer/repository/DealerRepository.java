package com.example.osid.domain.dealer.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.osid.domain.dealer.entity.Dealer;
import com.example.osid.domain.master.entity.Master;

@Repository
public interface DealerRepository extends JpaRepository<Dealer, Long> {

	// 딜러에서 이메일 검색
	Optional<Dealer> findByEmail(String email);

	// 삭제되지 않는 Dealer 조회
	Optional<Dealer> findByEmailAndIsDeletedFalse(String email);

	// 특정 Master 엔티티에 속하면서, isDeleted = false인 딜러 목록 조회
	List<Dealer> findByMasterAndIsDeletedFalse(Master master);
}
