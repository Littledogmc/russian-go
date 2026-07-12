/*
 * Auth API module.
 * register — create a new account
 * login — authenticate and receive JWT token
 * getMe — fetch current user info from token
 */
import http from './index'

export interface AuthResult {
  token: string
  userId: number
  username: string
  role: number
}

export async function register(
  username: string,
  password: string,
  email: string,
): Promise<AuthResult> {
  const resp = await http.post<AuthResult>('/auth/register', { username, password, email })
  return resp.data
}

export async function login(username: string, password: string): Promise<AuthResult> {
  const resp = await http.post<AuthResult>('/auth/login', { username, password })
  return resp.data
}

export async function getMe(): Promise<{ userId: number; username: string; role: number }> {
  const resp = await http.get('/auth/me')
  return resp.data
}
