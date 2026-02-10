package com.gameplatform.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

import com.gameplatform.dto.LoginRequest;
import com.gameplatform.dto.RegisterRequest;
import com.gameplatform.entity.Role;
import com.gameplatform.entity.User;
import com.gameplatform.exception.UserAlreadyExistsException;
import com.gameplatform.repository.UserRepository;
import com.gameplatform.security.jwt.JWTUtil;

@Service
public class AuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JWTUtil jwtutil;
	 public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JWTUtil jwtUtil)
	 {
		 this.userRepository = userRepository;
		 this.passwordEncoder = passwordEncoder;
		 this.jwtutil = jwtUtil;
	 }
	 public void register(RegisterRequest request)
	 {
		 if(userRepository.findByUsername(request.username).isPresent())
		 {
			 throw new UserAlreadyExistsException("User already exists!");
		 }
		 User user = new User();
		 user.setUsername(request.username);
		 user.setPassword(passwordEncoder.encode(request.password));
		 user.setRole(Role.ROLE_USER);
		 userRepository.save(user);
	
	 }
	 public String login(LoginRequest request)
	 {
		 User user = userRepository.findByUsername(request.username)
				 .orElseThrow(()-> new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Invalid Credentials"));
		 if(!passwordEncoder.matches(request.password,user.getPassword()))
		 {
			 throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Invalid unsername or password");
		 }
		 return jwtutil.generateToken(user.getUsername());
	 }
	 public void registerAdmin(RegisterRequest request)
	 {
		 if(userRepository.findByUsername(request.getUsername()).isPresent())
		 {
			 throw new UserAlreadyExistsException("username already exists!");
		 }
		 User user = new User();
		 user.setUsername(request.getUsername());
		 user.setPassword(passwordEncoder.encode(request.getPassword()));
		 user.setRole(Role.ROLE_ADMIN);
		 userRepository.save(user);
	 }
	 
}
