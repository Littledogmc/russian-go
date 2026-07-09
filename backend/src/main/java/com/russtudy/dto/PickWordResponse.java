/*
 * 随机抽词响应 DTO
 * 后端随机抽取出一个单词后，返回给前端用于展示提示
 */
package com.russtudy.dto;

public class PickWordResponse {

    private Long wordId;        // 单词 ID，提交答案时需传回
    private String russian;     // 俄文单词（答案）
    private String chinese;     // 中文释义（提示）
    private String english;     // 英文释义（可选提示）

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