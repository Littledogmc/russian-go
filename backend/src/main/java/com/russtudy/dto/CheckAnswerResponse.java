/*
 * 核对答案响应 DTO
 * 返回给前端本次作答是否正确，以及标准答案
 */
package com.russtudy.dto;

public class CheckAnswerResponse {

    private boolean correct;         // 是否正确
    private String correctAnswer;    // 标准俄文答案

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