/*
 * 单词 POJO（无 JPA 注解，用于 Redis 序列化）
 * 存储每个单词的俄文、中文释义及其所属词书
 */
package com.russtudy.model;

public class Word {

    private Long id;
    private Long wordbookId;
    private String russian;
    private String chinese;
    private String english;

    public Word() {}

    public Word(Long id, Long wordbookId, String russian, String chinese) {
        this.id = id;
        this.wordbookId = wordbookId;
        this.russian = russian;
        this.chinese = chinese;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getWordbookId() { return wordbookId; }
    public void setWordbookId(Long wordbookId) { this.wordbookId = wordbookId; }

    public String getRussian() { return russian; }
    public void setRussian(String russian) { this.russian = russian; }

    public String getChinese() { return chinese; }
    public void setChinese(String chinese) { this.chinese = chinese; }

    public String getEnglish() { return english; }
    public void setEnglish(String english) { this.english = english; }
}