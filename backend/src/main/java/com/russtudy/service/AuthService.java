/*
 * Auth service.
 * Register: validates input, BCrypt-hashes password, stores user in Redis.
 * Login: verifies credentials and role, returns JWT on success.
 */
package com.russtudy.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.russtudy.config.JwtUtil;
import com.russtudy.dto.AuthResponse;
import com.russtudy.dto.LoginRequest;
import com.russtudy.dto.RegisterRequest;
import com.russtudy.model.User;

@Service
public class AuthService {

	private static final int ROLE_USER = 1;
	private static final int ROLE_BANNED = -1;

	private final RedisTemplate<String, Object> redis;
	private final JwtUtil jwtUtil;
	private final BCryptPasswordEncoder encoder;

	private static final String KEY_USER_PREFIX = "user:";
	private static final String KEY_NAME_INDEX = "user:name:";
	private static final String KEY_ID_COUNTER = "user:id_counter";

	public AuthService(RedisTemplate<String, Object> redis, JwtUtil jwtUtil) {
		this.redis = redis;
		this.jwtUtil = jwtUtil;
		this.encoder = new BCryptPasswordEncoder();
	}

	public AuthResponse register(RegisterRequest req) {
		String username = req.getUsername().trim().toLowerCase();
		String email = req.getEmail() != null ? req.getEmail().trim().toLowerCase() : "";
		if (username.isEmpty() || req.getPassword().length() < 3) {
			throw new RuntimeException("Username or password too short");
		}
		if (redis.opsForValue().get(KEY_NAME_INDEX + username) != null) {
			throw new RuntimeException("Username already exists");
		}

		Long id = redis.opsForValue().increment(KEY_ID_COUNTER);
		String hash = encoder.encode(req.getPassword());
		User user = new User(id, username, hash, email, ROLE_USER);

		redis.opsForValue().set(KEY_USER_PREFIX + id, user);
		redis.opsForValue().set(KEY_NAME_INDEX + username, String.valueOf(id));

		String token = jwtUtil.generateToken(id, username);
		return new AuthResponse(token, id, username, ROLE_USER);
	}

	public AuthResponse login(LoginRequest req) {
		String username = req.getUsername().trim().toLowerCase();
		Object idObj = redis.opsForValue().get(KEY_NAME_INDEX + username);
		if (idObj == null) {
			throw new RuntimeException("Invalid username or password");
		}

		Long id = Long.valueOf(idObj.toString());
		User user = (User) redis.opsForValue().get(KEY_USER_PREFIX + id);
		if (user == null) {
			throw new RuntimeException("User not found");
		}
		if (user.getRole() == ROLE_BANNED) {
			throw new RuntimeException("Account has been banned");
		}
		if (!encoder.matches(req.getPassword(), user.getPasswordHash())) {
			throw new RuntimeException("Invalid username or password");
		}

		String token = jwtUtil.generateToken(id, username);
		return new AuthResponse(token, id, username, user.getRole());
	}

	public User getUserById(Long userId) {
		return (User) redis.opsForValue().get(KEY_USER_PREFIX + userId);
	}
}