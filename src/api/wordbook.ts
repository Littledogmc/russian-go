/*
 * 词书相关 API
 * GET /api/wordbooks — 获取站点内置词书列表
 */
import http from './index'
import type { WordbookSummary } from '@/types'

export async function fetchWordbooks(): Promise<WordbookSummary[]> {
  const resp = await http.get<WordbookSummary[]>('/wordbooks')
  return resp.data
}
