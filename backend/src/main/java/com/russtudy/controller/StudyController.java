/*
 * 背诵检测 REST 控制器（Redis 版）
 * 提供 6 个 API：词书列表、随机抽词、核对答案、完成检测、活动/错词榜
 */
package com.russtudy.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.russtudy.dto.ActivityRecord;
import com.russtudy.dto.CheckAnswerRequest;
import com.russtudy.dto.CheckAnswerResponse;
import com.russtudy.dto.ErrorWordRecord;
import com.russtudy.dto.FinishSessionRequest;
import com.russtudy.dto.PickWordRequest;
import com.russtudy.dto.PickWordResponse;
import com.russtudy.dto.WordbookResponse;
import com.russtudy.model.Wordbook;
import com.russtudy.service.StudyService;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class StudyController {

    private final StudyService studyService;

    public StudyController(StudyService studyService) {
        this.studyService = studyService;
    }

    /*
     * GET /api/wordbooks
     * 获取所有站点内置词书的列表（从 Redis 读取）
     */
    @GetMapping("/wordbooks")
    public List<WordbookResponse> getWordbooks() {
        return studyService.getWordbooks()
                .stream()
                .map(wb -> new WordbookResponse(wb.getId(), wb.getName(), wb.getWordCount()))
                .toList();
    }

    /*
     * POST /api/study/pick
     * 从指定词书中随机抽取一个未答对的单词
     */
    @PostMapping("/study/pick")
    public ResponseEntity<PickWordResponse> pickWord(@RequestBody PickWordRequest request) {
        PickWordResponse result = studyService.pickWord(request.getWordbookId());
        if (result == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(result);
    }

    /*
     * POST /api/study/check
     * 核对用户输入的答案
     */
    @PostMapping("/study/check")
    public CheckAnswerResponse checkAnswer(@RequestBody CheckAnswerRequest request) {
        return studyService.checkAnswer(request);
    }

    /*
     * POST /api/study/finish
     * 结束本轮检测，记录活动到 Redis
     */
    @PostMapping("/study/finish")
    public void finishSession(@RequestBody FinishSessionRequest request) {
        studyService.finishSession(
                request.getWordbookId(), request.getWordbookName(),
                request.getTotal(), request.getCorrect()
        );
    }

    /*
     * POST /api/study/reset
     * 重置本轮检测状态
     */
    @PostMapping("/study/reset")
    public void reset() {
        studyService.reset();
    }

    /*
     * GET /api/study/activities?limit=5
     * 获取近期测试活动
     */
    @GetMapping("/study/activities")
    public List<ActivityRecord> getActivities(@RequestParam(defaultValue = "5") int limit) {
        return studyService.getRecentActivities(limit);
    }

    /*
     * GET /api/study/errors?limit=5
     * 获取高频错词榜
     */
    @GetMapping("/study/errors")
    public List<ErrorWordRecord> getErrors(@RequestParam(defaultValue = "5") int limit) {
        return studyService.getErrorWords(limit);
    }
}