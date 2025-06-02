package com.example.osid.domain.master.repository;

import java.util.Optional;

import com.example.osid.domain.master.entity.Master;
import com.example.osid.domain.user.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MasterRepository extends JpaRepository<Master, Long> {

	// Master에서 이메일 검색
	Optional<Master> findByEmail(String email);
}
