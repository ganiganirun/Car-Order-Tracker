package com.example.osid.common.auth.entity;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "refresh_tokens")
@Getter
@NoArgsConstructor
public class RefreshToken {

	@Id
	private String email;

	@Column(nullable = false, length = 500)
	private String token;

	@Column(nullable = false, length = 50)
	private String role;

	@Column(nullable = false)
	private Instant issuedAt;

	@Column(nullable = false)
	private Instant expiresAt;

	public RefreshToken(
		String email,
		String token,
		String role,
		Instant issuedAt,
		Instant expiresAt) {
		this.email = email;
		this.token = token;
		this.role = role;
		this.issuedAt = issuedAt;
		this.expiresAt = expiresAt;
	}

	public void updateToken(String token,
		Instant issuedAt,
		Instant expiresAt) {
		this.token = token;
		this.issuedAt = issuedAt;
		this.expiresAt = expiresAt;
	}
}
