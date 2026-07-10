/*
 * JWT utility.
 * Generates HMAC-SHA256 tokens embedding userId and username. Tokens expire after 7 days.
 */
package com.russtudy.config;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

	private static final SecretKey SECRET = Keys.hmacShaKeyFor(
		"RussianGo_JWT_Secret_Key_2026_Should_Be_Long_Enough!".getBytes()
	);

	private static final long EXPIRATION_MS = 7 * 24 * 60 * 60 * 1000L;

	public String generateToken(Long userId, String username) {
		return Jwts.builder()
			.claim("userId", userId)
			.claim("username", username)
			.setIssuedAt(new Date())
			.setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
			.signWith(SECRET)
			.compact();
	}

	public Jws<Claims> parseToken(String token) {
		return Jwts.parser()
			.verifyWith(SECRET)
			.build()
			.parseSignedClaims(token);
	}

	public Long getUserId(String token) {
		return parseToken(token).getPayload().get("userId", Long.class);
	}

	public String getUsername(String token) {
		return parseToken(token).getPayload().get("username", String.class);
	}

	public boolean isValid(String token) {
		try {
			parseToken(token);
			return true;
		} catch (JwtException | IllegalArgumentException e) {
			return false;
		}
	}
}