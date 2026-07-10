<script setup lang="ts">
/*
 * Login page.
 * Authenticates user and redirects to the page they came from (or home).
 */
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/authStore'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const username = ref('')
const password = ref('')
const isError = ref(false)
const errorMsg = ref('')

async function submitLogin() {
  isError.value = false
  errorMsg.value = ''
  try {
    await authStore.login(username.value, password.value)
    const redirect = (route.query.redirect as string) || '/'
    router.push(redirect)
  } catch (err: unknown) {
    isError.value = true
    const axiosErr = err as { response?: { data?: { error?: string } } }
    errorMsg.value = axiosErr?.response?.data?.error || 'Login failed'
  }
}
</script>

<template>
  <div class="auth-page">
    <div class="oj-card auth-card">
      <div class="oj-card__header">
        <h3>🔑 登录</h3>
      </div>
      <div class="oj-card__body">
        <div v-if="isError" class="auth-error">{{ errorMsg }}</div>
        <form @submit.prevent="submitLogin">
          <div class="auth-field">
            <label>用户名</label>
            <input
              v-model="username"
              type="text"
              class="oj-input"
              placeholder="输入用户名"
              required
            />
          </div>
          <div class="auth-field">
            <label>密码</label>
            <input
              v-model="password"
              type="password"
              class="oj-input"
              placeholder="输入密码"
              required
            />
          </div>
          <button
            type="submit"
            class="oj-btn oj-btn--primary oj-btn--lg auth-btn"
            :disabled="!username || !password"
          >
            登录
          </button>
        </form>
        <p class="text-center text-muted mt-4">
          还没有账号？
          <RouterLink to="/register">立即注册</RouterLink>
        </p>
      </div>
    </div>
  </div>
</template>

<style scoped>
.auth-page {
  display: flex;
  justify-content: center;
  padding-top: 60px;
}
.auth-card {
  width: 100%;
  max-width: 400px;
}
.auth-field {
  margin-bottom: 16px;
}
.auth-field label {
  display: block;
  font-size: 13px;
  font-weight: 600;
  margin-bottom: 4px;
  color: var(--oj-text-secondary);
}
.auth-field .oj-input {
  width: 100%;
}
.auth-btn {
  width: 100%;
  margin-top: 8px;
}
.auth-error {
  background: var(--oj-result-wrong-bg);
  color: var(--oj-danger);
  padding: 10px 14px;
  border-radius: var(--oj-radius);
  margin-bottom: 16px;
  font-size: 14px;
  text-align: center;
}
</style>
