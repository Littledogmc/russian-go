/*
 * 词书 POJO（无 JPA 注解，用于 Redis 序列化）
 * 存储词书基本信息：ID、名称、单词总数
 */
package com.russtudy.model;

public class Wordbook {

    private Long id;
    private String name;
    private int wordCount;

    public Wordbook() {}

    public Wordbook(Long id, String name, int wordCount) {
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