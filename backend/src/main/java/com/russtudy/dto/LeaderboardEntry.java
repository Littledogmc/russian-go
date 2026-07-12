/*
 * Leaderboard entry DTO.
 * Represents a user's activity count for the daily leaderboard.
 */
package com.russtudy.dto;

public class LeaderboardEntry {

	private String username;
	private long activityCount;

	public LeaderboardEntry() {}

	public LeaderboardEntry(String username, long activityCount) {
		this.username = username;
		this.activityCount = activityCount;
	}

	public String getUsername() { return username; }
	public void setUsername(String username) { this.username = username; }

	public long getActivityCount() { return activityCount; }
	public void setActivityCount(long activityCount) { this.activityCount = activityCount; }
}