/*
 * Admin API module.
 * Requires role = 0. Manages users and wordbooks.
 */
import http from './index'

export interface AdminUser {
  id: number
  username: string
  email: string
  role: number
  createdAt: string
}

export interface AdminWordbook {
  id: number
  name: string
  wordCount: number
}

export async function getUsers(): Promise<AdminUser[]> {
  const resp = await http.get<AdminUser[]>('/admin/users')
  return resp.data
}

export async function updateUserRole(userId: number, role: number): Promise<void> {
  await http.put('/admin/user/' + userId + '/role', { role })
}

export async function getAdminWordbooks(): Promise<AdminWordbook[]> {
  const resp = await http.get<AdminWordbook[]>('/admin/wordbooks')
  return resp.data
}

export async function updateWordbook(wbId: number, name: string, wordCount: number): Promise<void> {
  await http.put('/admin/wordbook/' + wbId, { name, wordCount })
}
