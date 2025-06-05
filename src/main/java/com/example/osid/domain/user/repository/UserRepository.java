package com.example.osid.domain.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.osid.domain.user.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	// email로 유저 검색
	Optional<User> findByEmail(String email);

	// 삭제되지 않는 User 조회
	Optional<User> findByEmailAndIsDeletedFalse(String email);

}
