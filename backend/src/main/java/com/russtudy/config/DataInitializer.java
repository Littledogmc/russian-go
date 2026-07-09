/*
 * 启动时数据初始化（Redis 版）
 * 每次启动自动清除旧数据并重新插入 3 本示例词书
 * 开发者在此修改或补充词书内容
 */
package com.russtudy.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.russtudy.model.Word;
import com.russtudy.model.Wordbook;

@Component
@Order(1)
public class DataInitializer implements CommandLineRunner {

    private final RedisTemplate<String, Object> redis;

    public DataInitializer(RedisTemplate<String, Object> redis) {
        this.redis = redis;
    }

    @Override
    public void run(String... args) {
        // ===== 每次启动清空旧数据，用新序列化格式重新写入 =====
        redis.getConnectionFactory().getConnection().serverCommands().flushAll();

        long wbId = 1, wordId = 1;

        // ========== 词书 1：常用动词300 ==========
        wordId = saveBookAndWords(wbId++, "常用动词300", wordId,
                w("говорить", "说，讲"),
                w("читать", "读，阅读"),
                w("писать", "写"),
                w("слушать", "听"),
                w("работать", "工作"),
                w("понимать", "明白，懂")
        );

        // ========== 词书 2：日常用语200 ==========
        wordId = saveBookAndWords(wbId++, "日常用语200", wordId,
                w("здравствуйте", "您好"),
                w("спасибо", "谢谢"),
                w("пожалуйста", "请"),
                w("извините", "对不起"),
                w("до свидания", "再见"),
                w("да", "是的")
        );

        // ========== 词书 3：形容词100 ==========
        wordId = saveBookAndWords(wbId++, "形容词100", wordId,
                w("хороший", "好的"),
                w("плохой", "坏的"),
                w("большой", "大的"),
                w("маленький", "小的"),
                w("красивый", "美丽的"),
                w("новый", "新的")
        );

        System.out.println(">>> RussianGo: Redis 数据已刷新，共 3 本词书, " + (wordId - 1) + " 个单词");
    }

    private long saveBookAndWords(long wbId, String name, long startWordId, WordDef... words) {
        // 词书
        redis.opsForValue().set("wordbook:" + wbId, new Wordbook(wbId, name, words.length));
        redis.opsForSet().add("wordbook:ids", String.valueOf(wbId));

        long wid = startWordId;
        for (WordDef p : words) {
            Word word = new Word(wid, wbId, p.russian, p.chinese);
            redis.opsForValue().set("word:" + wid, word);
            redis.opsForSet().add("wordbook:words:" + wbId, String.valueOf(wid));
            wid++;
        }
        return wid;
    }

    private static WordDef w(String r, String c) { return new WordDef(r, c); }

    private static class WordDef {
        String russian;
        String chinese;
        WordDef(String r, String c) { this.russian = r; this.chinese = c; }
    }
}