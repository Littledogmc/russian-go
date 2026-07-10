/*
 * Wordbook API module.
 * GET /api/wordbooks — fetch all built-in wordbooks.
 */
import http from './index'
import type { WordbookSummary } from '@/types'

export async function getWordbooks(): Promise<WordbookSummary[]> {
  const resp = await http.get<WordbookSummary[]>('/wordbooks')
  return resp.data
}
