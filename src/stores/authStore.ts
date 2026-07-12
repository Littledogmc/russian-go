/*
 * Auth store (Pinia).
 * Manages JWT token, current user info, login/logout actions.
 * Token is persisted in localStorage for session persistence.
 */
import { ref, computed } from 'vue'
import { defineStore } from 'pinia'
import { login as apiLogin, register as apiRegister, getMe } from '@/api/auth'

export const useAuthStore = defineStore('auth', () => {
  const token = ref<string | null>(localStorage.getItem('rg_token'))
  const userId = ref<number | null>(
    localStorage.getItem('rg_userId') ? Number(localStorage.getItem('rg_userId')) : null,
  )
  const username = ref<string | null>(localStorage.getItem('rg_username'))
  const role = ref<number | null>(
    localStorage.getItem('rg_role') ? Number(localStorage.getItem('rg_role')) : null,
  )

  const isLoggedIn = computed(() => token.value !== null)
  const isAdmin = computed(() => role.value === 0)
  const isBanned = computed(() => role.value === -1)

  function saveAuth(tok: string, uid: number, uname: string, r: number) {
    token.value = tok
    userId.value = uid
    username.value = uname
    role.value = r
    localStorage.setItem('rg_token', tok)
    localStorage.setItem('rg_userId', String(uid))
    localStorage.setItem('rg_username', uname)
    localStorage.setItem('rg_role', String(r))
  }

  function clearAuth() {
    token.value = null
    userId.value = null
    username.value = null
    role.value = null
    localStorage.removeItem('rg_token')
    localStorage.removeItem('rg_userId')
    localStorage.removeItem('rg_username')
    localStorage.removeItem('rg_role')
  }

  async function login(usernameVal: string, password: string) {
    const res = await apiLogin(usernameVal, password)
    saveAuth(res.token, res.userId, res.username, res.role)
  }

  async function register(usernameVal: string, password: string, email: string) {
    const res = await apiRegister(usernameVal, password, email)
    saveAuth(res.token, res.userId, res.username, res.role)
  }

  async function refreshUser() {
    try {
      const me = await getMe()
      role.value = me.role
      userId.value = me.userId
      username.value = me.username
    } catch {
      clearAuth()
    }
  }

  function logout() {
    clearAuth()
  }

  return {
    token,
    userId,
    username,
    role,
    isLoggedIn,
    isAdmin,
    isBanned,
    login,
    register,
    logout,
    refreshUser,
  }
})
