/*
 * Check-answer request DTO.
 * Carries the word ID and the user's typed answer from the frontend.
 */
package com.russtudy.dto;

public class CheckAnswerRequest {

	private Long wordId;
	private String userAnswer;

	public CheckAnswerRequest() {}

	public Long getWordId() { return wordId; }
	public void setWordId(Long wordId) { this.wordId = wordId; }

	public String getUserAnswer() { return userAnswer; }
	public void setUserAnswer(String userAnswer) { this.userAnswer = userAnswer; }
}