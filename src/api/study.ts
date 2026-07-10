/*
 * Study API module.
 * pick — draw a random word from a wordbook
 * check — verify user answer against the correct one
 * reset — clear answered set for current session
 * finish — persist session stats as an activity record
 * getActivities — fetch recent study activity logs
 * getErrorWords — fetch error leaderboard
 */
import http from './index'
import type { PickWordResult, CheckAnswerResult, ActivityRecord, ErrorWord } from '@/types'

export async function pickWord(wordbookId: number): Promise<PickWordResult | null> {
  const resp = await http.post<PickWordResult>('/study/pick', { wordbookId })
  return resp.data
}

export async function checkAnswer(wordId: number, userAnswer: string): Promise<CheckAnswerResult> {
  const resp = await http.post<CheckAnswerResult>('/study/check', { wordId, userAnswer })
  return resp.data
}

export async function resetSession(): Promise<void> {
  await http.post('/study/reset')
}

export async function finishSession(
  wordbookId: number,
  wordbookName: string,
  total: number,
  correct: number,
): Promise<void> {
  await http.post('/study/finish', { wordbookId, wordbookName, total, correct })
}

export async function getActivities(limit = 5): Promise<ActivityRecord[]> {
  const resp = await http.get<ActivityRecord[]>('/study/activities', { params: { limit } })
  return resp.data
}

export async function getErrorWords(limit = 5): Promise<ErrorWord[]> {
  const resp = await http.get<ErrorWord[]>('/study/errors', { params: { limit } })
  return resp.data
}
