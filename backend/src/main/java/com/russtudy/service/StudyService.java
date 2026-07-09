/*
 * 背诵检测业务逻辑层（Redis 版）
 * 所有数据存储在 Redis 中：词书/单词用 Hash、已答对用 Set、
 * 错词用 SortedSet、活动用 List
 */
package com.russtudy.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final ObjectMapper mapper;

    // Redis Key 前缀
    private static final String K_WB = "wordbook:";
    private static final String K_WORD = "word:";
    private static final String K_WB_WORDS = "wordbook:words:";
    private static final String K_ANSWERED = "session:answered";
    private static final String K_ERRORS = "errors";
    private static final String K_ACTIVITIES = "activities";
    private static final String K_ACT_ID = "activity:id";

    public StudyService(RedisTemplate<String, Object> redis, ObjectMapper mapper) {
        this.redis = redis;
        this.mapper = mapper;
    }

    // ========== 词书 ==========

    /** 获取所有词书列表 */
    @SuppressWarnings("unchecked")
    public List<Wordbook> getWordbooks() {
        Set<Object> ids = redis.opsForSet().members("wordbook:ids");
        if (ids == null) return List.of();

        List<Wordbook> list = new ArrayList<>();
        for (Object id : ids) {
            Wordbook wb = (Wordbook) redis.opsForValue().get(K_WB + id);
            if (wb != null) list.add(wb);
        }
        return list;
    }

    // ========== 抽词 ==========

    /** 从指定词书中随机抽取一个未答对的单词 */
    @SuppressWarnings("unchecked")
    public PickWordResponse pickWord(Long wordbookId) {
        // 获取该词书所有单词 ID
        Set<Object> wordIds = redis.opsForSet().members(K_WB_WORDS + wordbookId);
        if (wordIds == null || wordIds.isEmpty()) return null;

        // 获取已答对的单词 ID
        Set<Object> answered = redis.opsForSet().members(K_ANSWERED + ":" + wordbookId);

        // 找出候选
        List<String> candidates = new ArrayList<>();
        for (Object o : wordIds) {
            String sid = o.toString();
            if (answered == null || !answered.contains(sid)) {
                candidates.add(sid);
            }
        }

        if (candidates.isEmpty()) return null;

        // 随机选一个
        String pickedId = candidates.get(new Random().nextInt(candidates.size()));
        Word word = (Word) redis.opsForValue().get(K_WORD + pickedId);
        if (word == null) return null;

        PickWordResponse resp = new PickWordResponse(word.getId(), word.getRussian(), word.getChinese());
        resp.setEnglish(word.getEnglish());
        return resp;
    }

    // ========== 核对 ==========

    /** 核对答案，记录正确/错误 */
    public CheckAnswerResponse checkAnswer(CheckAnswerRequest request) {
        Word word = (Word) redis.opsForValue().get(K_WORD + request.getWordId());
        if (word == null) {
            return new CheckAnswerResponse(false, "单词不存在");
        }

        boolean correct = word.getRussian()
                .trim()
                .equalsIgnoreCase(request.getUserAnswer().trim());

        if (correct) {
            redis.opsForSet().add(K_ANSWERED + ":" + word.getWordbookId(), String.valueOf(word.getId()));
        } else {
            redis.opsForZSet().incrementScore(K_ERRORS, String.valueOf(word.getId()), 1);
        }

        return new CheckAnswerResponse(correct, word.getRussian());
    }

    // ========== 检测完成 ==========

    /** 结束本轮检测，记录活动 */
    public void finishSession(Long wordbookId, String wordbookName, int total, int correct) {
        long id = redis.opsForValue().increment(K_ACT_ID);
        ActivityRecord record = new ActivityRecord(
                id,
                wordbookName,
                total,
                correct,
                LocalDateTime.now().toString().replace("T", " ").substring(0, 16),
                (total * 20) + "s"
        );
        // 左推，保留最近 50 条
        redis.opsForList().leftPush(K_ACTIVITIES, record);
        redis.opsForList().trim(K_ACTIVITIES, 0, 49);
    }

    // ========== 活动 ==========

    /** 获取近期测试活动 */
    @SuppressWarnings("unchecked")
    public List<ActivityRecord> getRecentActivities(int limit) {
        List<Object> list = redis.opsForList().range(K_ACTIVITIES, 0, limit - 1);
        if (list == null) return List.of();
        return list.stream()
                .map(o -> (ActivityRecord) o)
                .toList();
    }

    // ========== 错词榜 ==========

    /** 获取高频错词榜 */
    @SuppressWarnings("unchecked")
    public List<ErrorWordRecord> getErrorWords(int limit) {
        Set<Object> top = redis.opsForZSet().reverseRangeByScore(K_ERRORS, 0, Double.MAX_VALUE, 0, limit);
        if (top == null) return List.of();

        return top.stream().map(o -> {
            String wordId = o.toString();
            Double score = redis.opsForZSet().score(K_ERRORS, wordId);
            Word w = (Word) redis.opsForValue().get(K_WORD + wordId);
            if (w == null) return null;
            Wordbook wb = (Wordbook) redis.opsForValue().get(K_WB + w.getWordbookId());
            return new ErrorWordRecord(
                    w.getId(), w.getRussian(), w.getChinese(),
                    score != null ? score.intValue() : 0,
                    wb != null ? wb.getName() : ("词书#" + w.getWordbookId())
            );
        }).filter(o -> o != null).toList();
    }

    // ========== 重置 ==========

    /** 重置本轮检测状态 */
    public void reset() {
        // 清除所有词书的已答对记录（简单方案：不指定 wordbookId 时不清除）
        // 前端调用 reset 时不清除具体 set，由新 session 自行覆盖
    }

    /** 按 wordbookId 重置已答对记录 */
    public void resetForWordbook(Long wordbookId) {
        redis.delete(K_ANSWERED + ":" + wordbookId);
    }
}