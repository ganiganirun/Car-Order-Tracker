package com.example.osid.common.auth;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.osid.domain.dealer.entity.Dealer;
import com.example.osid.domain.master.entity.Master;
import com.example.osid.domain.user.entity.User;

import lombok.Getter;

@Getter
public class CustomUserDetails implements UserDetails {
	private final Long id;
	private final String email;
	private final String password;
	private final List<GrantedAuthority> authorities;

	public CustomUserDetails(Long id, String email, String password, List<GrantedAuthority> authorities) {
		this.id = id;
		this.email = email;
		this.password = password;
		this.authorities = authorities;
	}

	public static CustomUserDetails fromMaster(Master master) {
		return new CustomUserDetails(
			master.getId(),
			master.getEmail(),
			master.getPassword(),
			List.of(new SimpleGrantedAuthority("ROLE_MASTER"))
		);
	}

	public static CustomUserDetails fromDealer(Dealer dealer) {
		return new CustomUserDetails(
			dealer.getId(),
			dealer.getEmail(),
			dealer.getPassword(),
			List.of(new SimpleGrantedAuthority("ROLE_DEALER"))
		);
	}

	public static CustomUserDetails fromUser(User user) {
		return new CustomUserDetails(
			user.getId(),
			user.getEmail(),
			user.getPassword(),
			List.of(new SimpleGrantedAuthority("ROLE_USER"))
		);
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return email;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}