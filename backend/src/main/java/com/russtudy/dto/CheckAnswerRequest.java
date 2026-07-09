/*
 * 核对答案请求 DTO
 * 前端提交用户输入的俄文答案时使用
 */
package com.russtudy.dto;

public class CheckAnswerRequest {

    private Long wordId;         // 当前检测的单词 ID
    private String userAnswer;   // 用户输入的俄文

    public CheckAnswerRequest() {}

    public Long getWordId() { return wordId; }
    public void setWordId(Long wordId) { this.wordId = wordId; }

    public String getUserAnswer() { return userAnswer; }
    public void setUserAnswer(String userAnswer) { this.userAnswer = userAnswer; }
}