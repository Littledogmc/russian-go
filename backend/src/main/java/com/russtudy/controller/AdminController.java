/*
 * Admin controller.
 * All endpoints require admin role (role = 0). Provides user and wordbook management.
 */
package com.russtudy.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.russtudy.config.JwtUtil;
import com.russtudy.model.User;
import com.russtudy.model.Wordbook;
import com.russtudy.service.AuthService;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

	private final JwtUtil jwtUtil;
	private final AuthService authService;
	private final com.russtudy.service.StudyService studyService;

	private static final String KEY_USER_IDS = "user:ids";
	private static final String KEY_USER_PREFIX = "user:";
	private static final String KEY_WB_PREFIX = "wordbook:";

	public AdminController(JwtUtil jwtUtil, AuthService authService,
			com.russtudy.service.StudyService studyService) {
		this.jwtUtil = jwtUtil;
		this.authService = authService;
		this.studyService = studyService;
	}

	private User requireAdmin(String authHeader) {
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			throw new RuntimeException("Missing or invalid token");
		}
		String token = authHeader.substring(7);
		if (!jwtUtil.isValid(token)) {
			throw new RuntimeException("Invalid or expired token");
		}
		Long userId = jwtUtil.getUserId(token);
		User user = authService.getUserById(userId);
		if (user == null || user.getRole() != 0) {
			throw new RuntimeException("Admin access required");
		}
		return user;
	}

	@GetMapping("/users")
	public ResponseEntity<?> listUsers(@RequestHeader("Authorization") String authHeader) {
		try {
			requireAdmin(authHeader);
			Set<Object> ids = studyService.getRedis().opsForSet().members(KEY_USER_IDS);
			List<Map<String, Object>> users = new ArrayList<>();
			if (ids != null) {
				for (Object idObj : ids) {
					User u = (User) studyService.getRedis().opsForValue().get(KEY_USER_PREFIX + idObj);
					if (u != null) {
						users.add(Map.of(
							"id", u.getId(),
							"username", u.getUsername(),
							"email", u.getEmail() != null ? u.getEmail() : "",
							"role", u.getRole(),
							"createdAt", u.getCreatedAt() != null ? u.getCreatedAt() : ""
						));
					}
				}
			}
			return ResponseEntity.ok(users);
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
		}
	}

	@PutMapping("/user/{userId}/role")
	public ResponseEntity<?> updateUserRole(
			@RequestHeader("Authorization") String authHeader,
			@PathVariable Long userId,
			@RequestBody Map<String, Integer> body) {
		try {
			requireAdmin(authHeader);
			Integer newRole = body.get("role");
			if (newRole == null || (newRole != 0 && newRole != 1 && newRole != -1)) {
				return ResponseEntity.badRequest().body(Map.of("error", "Invalid role value"));
			}
			User user = authService.getUserById(userId);
			if (user == null) {
				return ResponseEntity.badRequest().body(Map.of("error", "User not found"));
			}
			user.setRole(newRole);
			studyService.getRedis().opsForValue().set(KEY_USER_PREFIX + userId, user);
			return ResponseEntity.ok(Map.of("success", true, "userId", userId, "newRole", newRole));
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
		}
	}

	@GetMapping("/wordbooks")
	public ResponseEntity<?> listWordbooks(@RequestHeader("Authorization") String authHeader) {
		try {
			requireAdmin(authHeader);
			return ResponseEntity.ok(studyService.getWordbooks());
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
		}
	}

	@PutMapping("/wordbook/{wbId}")
	public ResponseEntity<?> updateWordbook(
			@RequestHeader("Authorization") String authHeader,
			@PathVariable Long wbId,
			@RequestBody Map<String, Object> body) {
		try {
			requireAdmin(authHeader);
			Object val = studyService.getRedis().opsForValue().get(KEY_WB_PREFIX + wbId);
			if (!(val instanceof Wordbook wb)) {
				return ResponseEntity.badRequest().body(Map.of("error", "Wordbook not found"));
			}
			if (body.containsKey("name")) {
				wb.setName((String) body.get("name"));
			}
			if (body.containsKey("wordCount")) {
				wb.setWordCount((Integer) body.get("wordCount"));
			}
			studyService.getRedis().opsForValue().set(KEY_WB_PREFIX + wbId, wb);
			return ResponseEntity.ok(Map.of("success", true, "id", wbId));
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
		}
	}
}