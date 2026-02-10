package com.gameplatform.controller;

import java.util.Optional;

import com.gameplatform.entity.User;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.gameplatform.dto.AuthResponse;
import com.gameplatform.dto.LoginRequest;
import com.gameplatform.dto.RegisterRequest;
import com.gameplatform.repository.UserRepository;
import com.gameplatform.service.AuthService;

import jakarta.security.auth.message.callback.PrivateKeyCallback.Request;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {
	private final AuthService authService;
	private final UserRepository userRepository;
	public AuthController(AuthService authService,UserRepository userRepository)
	{
		this.authService = authService;
		this.userRepository = userRepository;
	}
	@PostMapping("/register")
	public ResponseEntity<String> register (@Valid @RequestBody RegisterRequest request)
	{
		authService.register(request);
		return ResponseEntity.ok("Registration successful");
	}
	@PostMapping("/login")
	public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request)
	{
		String token = authService.login(request);
		User user = userRepository.findByUsername(request.getUsername()).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
		return ResponseEntity.ok(new AuthResponse(
				token,
				user.getUsername(),
				user.getRole().name()
				));
	}
	//for testing
//	@PostMapping("/register-admin")
//	public ResponseEntity<String> registerAdmin(@Valid @RequestBody RegisterRequest request) {
//	    authService.registerAdmin(request);
//	    return ResponseEntity.ok("Admin registration successful");
//	}

}
