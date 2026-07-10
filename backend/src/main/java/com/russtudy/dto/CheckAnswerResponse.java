/*
 * Check-answer response DTO.
 * Tells the frontend whether the answer was correct and provides the correct spelling.
 */
package com.russtudy.dto;

public class CheckAnswerResponse {

	private boolean correct;
	private String correctAnswer;

	public CheckAnswerResponse() {}

	public CheckAnswerResponse(boolean correct, String correctAnswer) {
		this.correct = correct;
		this.correctAnswer = correctAnswer;
	}

	public boolean isCorrect() { return correct; }
	public void setCorrect(boolean correct) { this.correct = correct; }

	public String getCorrectAnswer() { return correctAnswer; }
	public void setCorrectAnswer(String correctAnswer) { this.correctAnswer = correctAnswer; }
}