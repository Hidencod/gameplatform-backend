package com.gameplatform.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.gameplatform.entity.User;
import com.gameplatform.repository.*;
import com.gameplatform.security.CustomUserDetails;

@Service
public class CustomUserDetailsService implements UserDetailsService{

	private final UserRepository userRepository;
	public CustomUserDetailsService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user =  userRepository.findByUsername(username).orElseThrow(()->new UsernameNotFoundException("user not found"));
		return new CustomUserDetails(user);
	}

	

}
