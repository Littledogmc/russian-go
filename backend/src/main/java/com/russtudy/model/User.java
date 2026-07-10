/*
 * User POJO.
 * Role determines access level: 0 = ADMIN, 1 = USER, -1 = BANNED.
 * Stored as a JSON value in Redis via Jackson serialization.
 */
package com.russtudy.model;

import java.time.LocalDateTime;

public class User {

	private Long id;
	private String username;
	private String passwordHash;
	private int role;
	private String createdAt;

	public User() {}

	public User(Long id, String username, String passwordHash, int role) {
		this.id = id;
		this.username = username;
		this.passwordHash = passwordHash;
		this.role = role;
		this.createdAt = LocalDateTime.now().toString().substring(0, 19);
	}

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }

	public String getUsername() { return username; }
	public void setUsername(String username) { this.username = username; }

	public String getPasswordHash() { return passwordHash; }
	public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

	public int getRole() { return role; }
	public void setRole(int role) { this.role = role; }

	public String getCreatedAt() { return createdAt; }
	public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}