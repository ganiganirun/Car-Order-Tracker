package com.example.osid.common.auth.authentication;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.osid.common.auth.CustomUserDetailsService;
import com.example.osid.common.auth.repository.RefreshTokenRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtUtil jwtUtil;
	private final CustomUserDetailsService userDetailsService;
	private final RefreshTokenRepository refreshTokenRepository;

	@Override
	protected void doFilterInternal(
		HttpServletRequest request,
		HttpServletResponse response,
		FilterChain filterChain
	)
		throws ServletException, IOException {
		String header = request.getHeader("Authorization");
		if (header != null && header.startsWith("Bearer ")) {
			String token = header.substring(7);
			if (jwtUtil.validateToken(token)) {
				String email = jwtUtil.extractEmail(token);
				boolean stillLoggedIn = refreshTokenRepository.existsById(email);

				if (!stillLoggedIn) {
					// 로그아웃된 상태: 401 반환하고 체인 중단
					response.setStatus(HttpStatus.UNAUTHORIZED.value());
					return;
				}
				
				String role = jwtUtil.extractRole(token);

				UserDetails userDetails = userDetailsService.loadUserByUsername(email);
				List<GrantedAuthority> authorities = List.of(
					new SimpleGrantedAuthority("ROLE_" + role)
				);

				UsernamePasswordAuthenticationToken authToken =
					new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
				authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

				SecurityContextHolder.getContext().setAuthentication(authToken);
			}
		}
		filterChain.doFilter(request, response);
	}
}
