/*
 * Study controller.
 * All study endpoints require a valid JWT in the Authorization header.
 * The userId is extracted from the token and passed to StudyService for data isolation.
 */
package com.russtudy.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.russtudy.config.JwtUtil;
import com.russtudy.dto.CheckAnswerRequest;
import com.russtudy.dto.FinishSessionRequest;
import com.russtudy.dto.LeaderboardEntry;
import com.russtudy.dto.PickWordRequest;
import com.russtudy.dto.PickWordResponse;
import com.russtudy.dto.WordbookResponse;
import com.russtudy.service.StudyService;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class StudyController {

	private final StudyService studyService;
	private final JwtUtil jwtUtil;

	public StudyController(StudyService studyService, JwtUtil jwtUtil) {
		this.studyService = studyService;
		this.jwtUtil = jwtUtil;
	}

	private Long extractUserId(String authHeader) {
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			throw new RuntimeException("Missing or invalid token");
		}
		String token = authHeader.substring(7);
		if (!jwtUtil.isValid(token)) {
			throw new RuntimeException("Invalid or expired token");
		}
		return jwtUtil.getUserId(token);
	}

	@GetMapping("/wordbooks")
	public List<WordbookResponse> getWordbooks() {
		return studyService.getWordbooks()
			.stream()
			.map(wb -> new WordbookResponse(wb.getId(), wb.getName(), wb.getWordCount()))
			.toList();
	}

	@PostMapping("/study/pick")
	public ResponseEntity<?> pickWord(
		@RequestHeader("Authorization") String authHeader,
		@RequestBody PickWordRequest req
	) {
		try {
			Long userId = extractUserId(authHeader);
			PickWordResponse result = studyService.pickWord(userId, req.getWordbookId());
			if (result == null) {
				return ResponseEntity.noContent().build();
			}
			return ResponseEntity.ok(result);
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
				.body(Map.of("error", e.getMessage()));
		}
	}

	@PostMapping("/study/check")
	public ResponseEntity<?> checkAnswer(
		@RequestHeader("Authorization") String authHeader,
		@RequestBody CheckAnswerRequest req
	) {
		try {
			Long userId = extractUserId(authHeader);
			return ResponseEntity.ok(studyService.checkAnswer(userId, req));
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
				.body(Map.of("error", e.getMessage()));
		}
	}

	@PostMapping("/study/finish")
	public ResponseEntity<?> finishSession(
		@RequestHeader("Authorization") String authHeader,
		@RequestBody FinishSessionRequest req
	) {
		try {
			Long userId = extractUserId(authHeader);
			studyService.finishSession(userId, req.getWordbookId(), req.getWordbookName(),
				req.getTotal(), req.getCorrect());
			return ResponseEntity.ok().build();
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
				.body(Map.of("error", e.getMessage()));
		}
	}

	@PostMapping("/study/reset")
	public ResponseEntity<?> reset(
		@RequestHeader("Authorization") String authHeader,
		@RequestBody PickWordRequest req
	) {
		try {
			Long userId = extractUserId(authHeader);
			studyService.resetForUser(userId, req.getWordbookId());
			return ResponseEntity.ok().build();
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
				.body(Map.of("error", e.getMessage()));
		}
	}

	@GetMapping("/study/activities")
	public ResponseEntity<?> getActivities(
		@RequestHeader("Authorization") String authHeader,
		@RequestParam(defaultValue = "5") int limit
	) {
		try {
			Long userId = extractUserId(authHeader);
			return ResponseEntity.ok(studyService.getRecentActivities(userId, limit));
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
				.body(Map.of("error", e.getMessage()));
		}
	}

	@GetMapping("/study/errors")
	public ResponseEntity<?> getErrors(
		@RequestHeader("Authorization") String authHeader,
		@RequestParam(defaultValue = "5") int limit
	) {
		try {
			Long userId = extractUserId(authHeader);
			return ResponseEntity.ok(studyService.getErrorWords(userId, limit));
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
				.body(Map.of("error", e.getMessage()));
		}
	}

	@GetMapping("/study/leaderboard")
	public ResponseEntity<?> getLeaderboard(
		@RequestHeader("Authorization") String authHeader,
		@RequestParam(defaultValue = "10") int limit
	) {
		try {
			Long userId = extractUserId(authHeader);
			return ResponseEntity.ok(studyService.getLeaderboard(userId, limit));
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
				.body(Map.of("error", e.getMessage()));
		}
	}
}