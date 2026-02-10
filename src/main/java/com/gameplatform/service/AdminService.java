package com.gameplatform.service;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.gameplatform.dto.UserResponse;
import com.gameplatform.entity.Role;
import com.gameplatform.entity.User;
import com.gameplatform.repository.UserRepository;

@Service
public class AdminService {
	private UserRepository userRepository;
	
	public AdminService(UserRepository userRepository)
	{
		this.userRepository = userRepository;
	}
	
//	public List<UserResponse> getAllUsers()
//	{
//		return userRepository.findAll().stream()
//				.map(user -> new UserResponse(
//						user.getId(),
//						user.getUsername(),
//						user.getRole()
//						)).collect(Collectors.toList());
//	}
	public Page<UserResponse> getUsers(int page, int size)
	{
		Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
		return userRepository.findAll(pageable).map(user-> new UserResponse(user.getId(), user.getUsername(), user.getRole()));
	}
	public UserResponse getUserById(Long id)
	{
		User user = userRepository.findById(id).orElseThrow(()->new RuntimeException("User not found"));
		return new UserResponse(user.getId(), user.getUsername(), user.getRole());
	}
	public void deleteUser(Long id)
	{
		if(!userRepository.existsById(id))
		{
			throw new RuntimeException("User not found in db");
		}
		userRepository.deleteById(id);
	}
	public void changeUserRole(Long id, String roleStr)
	{
		User user = userRepository.findById(id).orElseThrow(()->new RuntimeException("User not found"));
		try {
			Role role = Role.valueOf(roleStr);
			user.setRole(role);
			userRepository.save(user);
		}catch (IllegalArgumentException e) {
			throw new RuntimeException("Invalide role "+e);
		}
	}
}
