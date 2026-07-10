/*
 * Wordbook response DTO.
 * Summary data returned to the frontend for the wordbook list.
 */
package com.russtudy.dto;

public class WordbookResponse {

	private Long id;
	private String name;
	private int wordCount;

	public WordbookResponse() {}

	public WordbookResponse(Long id, String name, int wordCount) {
		this.id = id;
		this.name = name;
		this.wordCount = wordCount;
	}

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }

	public String getName() { return name; }
	public void setName(String name) { this.name = name; }

	public int getWordCount() { return wordCount; }
	public void setWordCount(int wordCount) { this.wordCount = wordCount; }
}