/*
 * 检测相关 API
 * pick — 随机抽词  check — 核对答案  reset — 重置本轮
 * finish — 结束检测记录活动  activities — 近期活动  errors — 错词榜
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

export async function fetchActivities(limit = 5): Promise<ActivityRecord[]> {
  const resp = await http.get<ActivityRecord[]>('/study/activities', { params: { limit } })
  return resp.data
}

export async function fetchErrorWords(limit = 5): Promise<ErrorWord[]> {
  const resp = await http.get<ErrorWord[]>('/study/errors', { params: { limit } })
  return resp.data
}
