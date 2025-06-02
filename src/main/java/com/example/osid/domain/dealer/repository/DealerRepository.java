package com.example.osid.domain.dealer.repository;

import java.util.Optional;

import com.example.osid.domain.dealer.entity.Dealer;
import com.example.osid.domain.user.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DealerRepository extends JpaRepository<Dealer, Long> {

	// 딜러에서 이메일 검색
	Optional<Dealer> findByEmail(String email);
}
