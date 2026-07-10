/*
 * Global type definitions.
 * Mirrors backend DTOs for API responses.
 */

// Wordbook summary returned by GET /api/wordbooks
export interface WordbookSummary {
  id: number
  name: string
  wordCount: number
}

// Single word picked for dictation from POST /api/study/pick
export interface PickWordResult {
  wordId: number
  russian: string
  chinese: string
  english?: string
}

// Answer check result from POST /api/study/check
export interface CheckAnswerResult {
  isCorrect: boolean
  correctAnswer: string
}

// Activity record for recent study sessions
export interface ActivityRecord {
  id: number
  wordbook: string
  wordCount: number
  correct: number
  date: string
  duration: string
}

// Error word entry for the error leaderboard
export interface ErrorWord {
  id: number
  russian: string
  chinese: string
  wrongCount: number
  wordbook: string
}
