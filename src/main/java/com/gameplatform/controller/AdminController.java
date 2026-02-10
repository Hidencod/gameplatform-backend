package com.gameplatform.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gameplatform.dto.RoleChangeRequest;
import com.gameplatform.dto.UserResponse;
import com.gameplatform.service.AdminService;

@RestController
@RequestMapping("/admin")
public class AdminController {
	private final AdminService adminService;
	
	public AdminController(AdminService adminService)
	{
		this.adminService = adminService;
	}
	@GetMapping("/users")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Page<UserResponse>> getUsers(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size
			)
	{
		return ResponseEntity.ok(adminService.getUsers(page,size));
	}
	
	@GetMapping("/users/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<UserResponse> getUserbyId(@PathVariable Long id){
		return ResponseEntity.ok(adminService.getUserById(id));
	}
	@DeleteMapping("/users/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<String> deleteUser(@PathVariable Long id)
	{
		adminService.deleteUser(id);
		return ResponseEntity.ok("User deleted successfully");
	}
	@PutMapping("/users/{id}/role")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<String> changeUserRole(@PathVariable Long id, @RequestBody RoleChangeRequest roleRequest)
	{
		adminService.changeUserRole(id, roleRequest.role());
		return ResponseEntity.ok("user role updated successfully");
	}
}
