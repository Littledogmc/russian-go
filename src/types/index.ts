/*
 * 全局 TypeScript 类型定义
 * 与后端 DTO 一一对应
 */

// 词书摘要（GET /api/wordbooks 返回）
export interface WordbookSummary {
  id: number
  name: string
  wordCount: number
}

// 随机抽词响应（POST /api/study/pick 返回）
export interface PickWordResult {
  wordId: number
  russian: string
  chinese: string
  english?: string
}

// 核对答案响应（POST /api/study/check 返回）
export interface CheckAnswerResult {
  correct: boolean
  correctAnswer: string
}

// 测试活动记录
export interface ActivityRecord {
  id: number
  wordbook: string
  wordCount: number
  correct: number
  date: string
  duration: string
}

// 错词条目
export interface ErrorWord {
  id: number
  russian: string
  chinese: string
  wrongCount: number
  wordbook: string
}
