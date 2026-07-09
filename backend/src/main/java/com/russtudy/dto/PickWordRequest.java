/*
 * 随机抽词请求 DTO
 * 前端请求抽取单词时，传入目标词书 ID
 */
package com.russtudy.dto;

public class PickWordRequest {

    private Long wordbookId;   // 要抽取单词的词书 ID

    public PickWordRequest() {}

    public Long getWordbookId() { return wordbookId; }
    public void setWordbookId(Long wordbookId) { this.wordbookId = wordbookId; }
}