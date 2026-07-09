/*
 * 测试活动记录 DTO
 * 记录每次检测的摘要信息
 */
package com.russtudy.dto;

public class ActivityRecord {

    private long id;
    private String wordbook;
    private int wordCount;
    private int correct;
    private String date;
    private String duration;

    public ActivityRecord() {}

    public ActivityRecord(long id, String wordbook, int wordCount, int correct, String date, String duration) {
        this.id = id;
        this.wordbook = wordbook;
        this.wordCount = wordCount;
        this.correct = correct;
        this.date = date;
        this.duration = duration;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getWordbook() { return wordbook; }
    public void setWordbook(String wordbook) { this.wordbook = wordbook; }

    public int getWordCount() { return wordCount; }
    public void setWordCount(int wordCount) { this.wordCount = wordCount; }

    public int getCorrect() { return correct; }
    public void setCorrect(int correct) { this.correct = correct; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }
}