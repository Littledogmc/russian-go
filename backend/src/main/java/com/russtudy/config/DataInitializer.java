/*
 * Data initializer.
 * Flushes Redis on every startup and re-inserts 3 built-in wordbooks with sample words.
 * Edit this file to add or modify seed data.
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
		// Only insert wordbook data if it doesn't already exist (survives restarts)
		Long existingCount = redis.opsForSet().size("wordbook:ids");
		if (existingCount != null && existingCount > 0) {
			System.out.println(">>> RussianGo: " + existingCount + " wordbooks already exist, skipping init");
			return;
		}

		long wbId = 1, wordId = 1;

		wordId = saveBookAndWords(wbId++, "Common Verbs 300", wordId,
			w("говорить", "说，讲"),
			w("читать", "读，阅读"),
			w("писать", "写"),
			w("слушать", "听"),
			w("работать", "工作"),
			w("понимать", "明白，懂")
		);

		wordId = saveBookAndWords(wbId++, "Everyday Phrases 200", wordId,
			w("здравствуйте", "您好"),
			w("спасибо", "谢谢"),
			w("пожалуйста", "请"),
			w("извините", "对不起"),
			w("до свидания", "再见"),
			w("да", "是的")
		);

		wordId = saveBookAndWords(wbId++, "Adjectives 100", wordId,
			w("хороший", "好的"),
			w("плохой", "坏的"),
			w("большой", "大的"),
			w("маленький", "小的"),
			w("красивый", "美丽的"),
			w("новый", "新的")
		);

		System.out.println(">>> RussianGo: initialized " + (wbId - 1) + " wordbooks, " + (wordId - 1) + " words");
	}

	private long saveBookAndWords(long wbId, String name, long startWordId, WordDef... words) {
		redis.opsForValue().set("wordbook:" + wbId, new Wordbook(wbId, name, words.length));
		redis.opsForSet().add("wordbook:ids", String.valueOf(wbId));

		long wid = startWordId;
		for (WordDef p : words) {
			redis.opsForValue().set("word:" + wid, new Word(wid, wbId, p.russian, p.chinese));
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