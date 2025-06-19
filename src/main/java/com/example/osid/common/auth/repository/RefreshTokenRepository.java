package com.example.osid.common.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.osid.common.auth.entity.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
}
