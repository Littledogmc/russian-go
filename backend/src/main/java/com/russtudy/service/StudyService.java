/*
 * 背诵检测业务逻辑层（Redis 版）
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

    // Redis Key 前缀
    private static final String K_WB = "wordbook:";
    private static final String K_WORD = "word:";
    private static final String K_WB_WORDS = "wordbook:words:";
    private static final String K_ANSWERED = "session:answered:";
    private static final String K_ERRORS = "errors";
    private static final String K_ACTIVITIES = "activities";
    private static final String K_ACT_ID = "activity:id";

    public StudyService(RedisTemplate<String, Object> redis) {
        this.redis = redis;
    }

    // ========== 词书 ==========

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

    // ========== 抽词 ==========

    public PickWordResponse pickWord(Long wordbookId) {
        Set<Object> wordIds = redis.opsForSet().members(K_WB_WORDS + wordbookId);
        if (wordIds == null || wordIds.isEmpty()) return null;

        // 已答对的单词 ID 集合
        Set<Object> answered = redis.opsForSet().members(K_ANSWERED + wordbookId);

        // 候选列表
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

        if (!(val instanceof Word word)) {
            // 诊断：打印实际类型
            System.err.println(">>> BUG: word:" + pickedId + " 反序列化失败, 实际类型="
                    + (val == null ? "null" : val.getClass().getName()));
            return null;
        }

        PickWordResponse resp = new PickWordResponse(word.getId(), word.getRussian(), word.getChinese());
        resp.setEnglish(word.getEnglish());
        return resp;
    }

    // ========== 核对 ==========

    public CheckAnswerResponse checkAnswer(CheckAnswerRequest request) {
        Object val = redis.opsForValue().get(K_WORD + request.getWordId());
        if (!(val instanceof Word word)) {
            return new CheckAnswerResponse(false, "单词数据异常");
        }

        boolean correct = word.getRussian()
                .trim()
                .equalsIgnoreCase(request.getUserAnswer().trim());

        if (correct) {
            redis.opsForSet().add(K_ANSWERED + word.getWordbookId(), String.valueOf(word.getId()));
        } else {
            redis.opsForZSet().incrementScore(K_ERRORS, String.valueOf(word.getId()), 1);
        }

        return new CheckAnswerResponse(correct, word.getRussian());
    }

    // ========== 检测完成 ==========

    public void finishSession(Long wordbookId, String wordbookName, int total, int correct) {
        long id = redis.opsForValue().increment(K_ACT_ID);
        ActivityRecord record = new ActivityRecord(
                id, wordbookName, total, correct,
                LocalDateTime.now().toString().replace("T", " ").substring(0, 16),
                (total * 20) + "s"
        );
        redis.opsForList().leftPush(K_ACTIVITIES, record);
        redis.opsForList().trim(K_ACTIVITIES, 0, 49);
    }

    // ========== 活动 ==========

    public List<ActivityRecord> getRecentActivities(int limit) {
        List<Object> list = redis.opsForList().range(K_ACTIVITIES, 0, limit - 1);
        if (list == null) return List.of();
        return list.stream()
                .filter(o -> o instanceof ActivityRecord)
                .map(o -> (ActivityRecord) o)
                .toList();
    }

    // ========== 错词榜 ==========

    public List<ErrorWordRecord> getErrorWords(int limit) {
        Set<Object> top = redis.opsForZSet().reverseRangeByScore(K_ERRORS, 0, Double.MAX_VALUE, 0, limit);
        if (top == null) return List.of();

        return top.stream().map(o -> {
            String wordId = o.toString();
            Double score = redis.opsForZSet().score(K_ERRORS, wordId);
            Object wv = redis.opsForValue().get(K_WORD + wordId);
            if (!(wv instanceof Word w)) return null;
            Object wbv = redis.opsForValue().get(K_WB + w.getWordbookId());
            String wbName = (wbv instanceof Wordbook wb) ? wb.getName() : ("词书#" + w.getWordbookId());
            return new ErrorWordRecord(
                    w.getId(), w.getRussian(), w.getChinese(),
                    score != null ? score.intValue() : 0, wbName
            );
        }).filter(o -> o != null).toList();
    }

    // ========== 重置 ==========

    public void reset() {}

    public void resetForWordbook(Long wordbookId) {
        redis.delete(K_ANSWERED + wordbookId);
    }
}