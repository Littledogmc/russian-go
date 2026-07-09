/*
 * 高频错词记录 DTO
 * 展示用户检测中错误次数最多的单词
 */
package com.russtudy.dto;

public class ErrorWordRecord {

    private long id;
    private String russian;
    private String chinese;
    private int wrongCount;
    private String wordbook;

    public ErrorWordRecord() {}

    public ErrorWordRecord(long id, String russian, String chinese, int wrongCount, String wordbook) {
        this.id = id;
        this.russian = russian;
        this.chinese = chinese;
        this.wrongCount = wrongCount;
        this.wordbook = wordbook;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getRussian() { return russian; }
    public void setRussian(String russian) { this.russian = russian; }

    public String getChinese() { return chinese; }
    public void setChinese(String chinese) { this.chinese = chinese; }

    public int getWrongCount() { return wrongCount; }
    public void setWrongCount(int wrongCount) { this.wrongCount = wrongCount; }

    public String getWordbook() { return wordbook; }
    public void setWordbook(String wordbook) { this.wordbook = wordbook; }
}