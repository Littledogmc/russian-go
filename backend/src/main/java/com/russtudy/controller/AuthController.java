/*
 * Auth controller.
 * Endpoints for user registration, login, and fetching the current user profile.
 */
package com.russtudy.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.russtudy.config.JwtUtil;
import com.russtudy.dto.AuthResponse;
import com.russtudy.dto.LoginRequest;
import com.russtudy.dto.RegisterRequest;
import com.russtudy.model.User;
import com.russtudy.service.AuthService;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

	private final AuthService authService;
	private final JwtUtil jwtUtil;

	public AuthController(AuthService authService, JwtUtil jwtUtil) {
		this.authService = authService;
		this.jwtUtil = jwtUtil;
	}

	@PostMapping("/register")
	public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
		try {
			AuthResponse resp = authService.register(req);
			return ResponseEntity.ok(resp);
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
		}
	}

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody LoginRequest req) {
		try {
			AuthResponse resp = authService.login(req);
			return ResponseEntity.ok(resp);
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
		}
	}

	@GetMapping("/me")
	public ResponseEntity<?> getMe(@RequestHeader("Authorization") String authHeader) {
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
				.body(Map.of("error", "Missing or invalid token"));
		}
		String token = authHeader.substring(7);
		if (!jwtUtil.isValid(token)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
				.body(Map.of("error", "Invalid or expired token"));
		}
		Long userId = jwtUtil.getUserId(token);
		User user = authService.getUserById(userId);
		if (user == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
				.body(Map.of("error", "User not found"));
		}
		return ResponseEntity.ok(Map.of(
			"userId", user.getId(),
			"username", user.getUsername(),
			"role", user.getRole()
		));
	}
}