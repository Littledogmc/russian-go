/*
 * Pick-word response DTO.
 * Returned after a random word is drawn — contains the word text and its meaning.
 */
package com.russtudy.dto;

public class PickWordResponse {

	private Long wordId;
	private String russian;
	private String chinese;
	private String english;

	public PickWordResponse() {}

	public PickWordResponse(Long wordId, String russian, String chinese) {
		this.wordId = wordId;
		this.russian = russian;
		this.chinese = chinese;
	}

	public Long getWordId() { return wordId; }
	public void setWordId(Long wordId) { this.wordId = wordId; }

	public String getRussian() { return russian; }
	public void setRussian(String russian) { this.russian = russian; }

	public String getChinese() { return chinese; }
	public void setChinese(String chinese) { this.chinese = chinese; }

	public String getEnglish() { return english; }
	public void setEnglish(String english) { this.english = english; }
}