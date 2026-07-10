/*
 * Auth response DTO.
 * Returned after successful login or registration — carries the JWT and user info.
 */
package com.russtudy.dto;

public class AuthResponse {

	private String token;
	private long userId;
	private String username;
	private int role;

	public AuthResponse() {}

	public AuthResponse(String token, long userId, String username, int role) {
		this.token = token;
		this.userId = userId;
		this.username = username;
		this.role = role;
	}

	public String getToken() { return token; }
	public void setToken(String token) { this.token = token; }

	public long getUserId() { return userId; }
	public void setUserId(long userId) { this.userId = userId; }

	public String getUsername() { return username; }
	public void setUsername(String username) { this.username = username; }

	public int getRole() { return role; }
	public void setRole(int role) { this.role = role; }
}