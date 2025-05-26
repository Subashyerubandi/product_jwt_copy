
package com.subash.product_jwt_copy.security;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.subash.product_jwt_copy.model.User;
import com.subash.product_jwt_copy.repository.UserRepository;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	private final UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

		return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(),
				user.getRoles().stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
						.collect(Collectors.toList()));
	}
}
