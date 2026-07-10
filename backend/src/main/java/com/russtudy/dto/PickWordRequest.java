/*
 * Pick-word request DTO.
 * Frontend specifies which wordbook to draw from.
 */
package com.russtudy.dto;

public class PickWordRequest {

	private Long wordbookId;

	public PickWordRequest() {}

	public Long getWordbookId() { return wordbookId; }
	public void setWordbookId(Long wordbookId) { this.wordbookId = wordbookId; }
}