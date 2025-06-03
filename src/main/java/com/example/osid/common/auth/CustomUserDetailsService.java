package com.example.osid.common.auth;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.osid.domain.dealer.entity.Dealer;
import com.example.osid.domain.dealer.repository.DealerRepository;
import com.example.osid.domain.master.entity.Master;
import com.example.osid.domain.master.repository.MasterRepository;
import com.example.osid.domain.user.entity.User;
import com.example.osid.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
	private final MasterRepository masterRepository;
	private final DealerRepository dealerRepository;
	private final UserRepository userRepository;

	@Override
	public CustomUserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		// Master 조회
		Optional<Master> masterOpt = masterRepository.findByEmailAndIsDeletedFalse(email);
		if (masterOpt.isPresent()) {
			return CustomUserDetails.fromMaster(masterOpt.get());
		}
		// Dealer 조회
		Optional<Dealer> dealerOpt = dealerRepository.findByEmailAndIsDeletedFalse(email);
		if (dealerOpt.isPresent()) {
			return CustomUserDetails.fromDealer(dealerOpt.get());
		}
		// User 조회
		Optional<User> userOpt = userRepository.findByEmailAndIsDeletedFalse(email);
		if (userOpt.isPresent()) {
			return CustomUserDetails.fromUser(userOpt.get());
		}

		throw new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + email);
	}
}
