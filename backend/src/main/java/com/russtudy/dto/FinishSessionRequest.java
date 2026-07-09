/*
 * 完成检测请求 DTO
 * 前端结束本轮检测时提交统计数据
 */
package com.russtudy.dto;

public class FinishSessionRequest {

    private Long wordbookId;
    private String wordbookName;
    private int total;
    private int correct;

    public FinishSessionRequest() {}

    public Long getWordbookId() { return wordbookId; }
    public void setWordbookId(Long wordbookId) { this.wordbookId = wordbookId; }

    public String getWordbookName() { return wordbookName; }
    public void setWordbookName(String wordbookName) { this.wordbookName = wordbookName; }

    public int getTotal() { return total; }
    public void setTotal(int total) { this.total = total; }

    public int getCorrect() { return correct; }
    public void setCorrect(int correct) { this.correct = correct; }
}