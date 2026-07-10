/*
 * Study service (multi-user).
 * All per-user data keys are prefixed with user:{userId}: to isolate data between users.
 */
package com.russtudy.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.russtudy.dto.ActivityRecord;
import com.russtudy.dto.CheckAnswerRequest;
import com.russtudy.dto.CheckAnswerResponse;
import com.russtudy.dto.ErrorWordRecord;
import com.russtudy.dto.PickWordResponse;
import com.russtudy.model.Word;
import com.russtudy.model.Wordbook;

@Service
public class StudyService {

	private final RedisTemplate<String, Object> redis;

	private static final String K_WB = "wordbook:";
	private static final String K_WORD = "word:";
	private static final String K_WB_WORDS = "wordbook:words:";

	public StudyService(RedisTemplate<String, Object> redis) {
		this.redis = redis;
	}

	private String answeredKey(Long userId, Long wbId) {
		return "user:" + userId + ":session:answered:" + wbId;
	}

	private String errorsKey(Long userId) {
		return "user:" + userId + ":errors";
	}

	private String activitiesKey(Long userId) {
		return "user:" + userId + ":activities";
	}

	public List<Wordbook> getWordbooks() {
		Set<Object> ids = redis.opsForSet().members("wordbook:ids");
		if (ids == null) return List.of();

		List<Wordbook> list = new ArrayList<>();
		for (Object id : ids) {
			Object val = redis.opsForValue().get(K_WB + id);
			if (val instanceof Wordbook wb) list.add(wb);
		}
		return list;
	}

	public PickWordResponse pickWord(Long userId, Long wordbookId) {
		Set<Object> wordIds = redis.opsForSet().members(K_WB_WORDS + wordbookId);
		if (wordIds == null || wordIds.isEmpty()) return null;

		Set<Object> answered = redis.opsForSet().members(answeredKey(userId, wordbookId));

		List<String> candidates = new ArrayList<>();
		for (Object o : wordIds) {
			String sid = o.toString();
			if (answered == null || !answered.contains(sid)) {
				candidates.add(sid);
			}
		}

		if (candidates.isEmpty()) return null;

		String pickedId = candidates.get(new Random().nextInt(candidates.size()));
		Object val = redis.opsForValue().get(K_WORD + pickedId);
		if (!(val instanceof Word word)) return null;

		PickWordResponse resp = new PickWordResponse(word.getId(), word.getRussian(), word.getChinese());
		resp.setEnglish(word.getEnglish());
		return resp;
	}

	public CheckAnswerResponse checkAnswer(Long userId, CheckAnswerRequest req) {
		Object val = redis.opsForValue().get(K_WORD + req.getWordId());
		if (!(val instanceof Word word)) {
			return new CheckAnswerResponse(false, "Word data error");
		}

		boolean isCorrect = word.getRussian()
			.trim()
			.equalsIgnoreCase(req.getUserAnswer().trim());

		if (isCorrect) {
			redis.opsForSet().add(answeredKey(userId, word.getWordbookId()), String.valueOf(word.getId()));
		} else {
			redis.opsForZSet().incrementScore(errorsKey(userId), String.valueOf(word.getId()), 1);
		}

		return new CheckAnswerResponse(isCorrect, word.getRussian());
	}

	public void finishSession(Long userId, Long wordbookId, String wordbookName, int total, int correct) {
		long id = System.currentTimeMillis();
		ActivityRecord record = new ActivityRecord(
			id, wordbookName, total, correct,
			LocalDateTime.now().toString().replace("T", " ").substring(0, 16),
			(total * 20) + "s"
		);
		redis.opsForList().leftPush(activitiesKey(userId), record);
		redis.opsForList().trim(activitiesKey(userId), 0, 49);
	}

	public List<ActivityRecord> getRecentActivities(Long userId, int limit) {
		List<Object> list = redis.opsForList().range(activitiesKey(userId), 0, limit - 1);
		if (list == null) return List.of();
		return list.stream()
			.filter(o -> o instanceof ActivityRecord)
			.map(o -> (ActivityRecord) o)
			.toList();
	}

	public List<ErrorWordRecord> getErrorWords(Long userId, int limit) {
		Set<Object> top = redis.opsForZSet().reverseRangeByScore(errorsKey(userId), 0, Double.MAX_VALUE, 0, limit);
		if (top == null) return List.of();

		return top.stream().map(o -> {
			String wordId = o.toString();
			Double score = redis.opsForZSet().score(errorsKey(userId), wordId);
			Object wv = redis.opsForValue().get(K_WORD + wordId);
			if (!(wv instanceof Word w)) return null;
			Object wbv = redis.opsForValue().get(K_WB + w.getWordbookId());
			String wbName = (wbv instanceof Wordbook wb) ? wb.getName() : ("Wordbook#" + w.getWordbookId());
			return new ErrorWordRecord(
				w.getId(), w.getRussian(), w.getChinese(),
				score != null ? score.intValue() : 0, wbName
			);
		}).filter(o -> o != null).toList();
	}

	public void resetForUser(Long userId, Long wordbookId) {
		redis.delete(answeredKey(userId, wordbookId));
	}
}