<script setup lang="ts">
/*
 * Register page.
 * Creates a new account and automatically logs in.
 */
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/authStore'

const router = useRouter()
const authStore = useAuthStore()

const username = ref('')
const password = ref('')
const isError = ref(false)
const errorMsg = ref('')

async function submitRegister() {
  isError.value = false
  errorMsg.value = ''
  try {
    await authStore.register(username.value, password.value)
    router.push('/')
  } catch (err: unknown) {
    isError.value = true
    const axiosErr = err as { response?: { data?: { error?: string } } }
    errorMsg.value = axiosErr?.response?.data?.error || 'Registration failed'
  }
}
</script>

<template>
  <div class="auth-page">
    <div class="oj-card auth-card">
      <div class="oj-card__header">
        <h3>📝 注册</h3>
      </div>
      <div class="oj-card__body">
        <div v-if="isError" class="auth-error">{{ errorMsg }}</div>
        <form @submit.prevent="submitRegister">
          <div class="auth-field">
            <label>用户名</label>
            <input
              v-model="username"
              type="text"
              class="oj-input"
              placeholder="设置用户名"
              required
            />
          </div>
          <div class="auth-field">
            <label>密码（至少3位）</label>
            <input
              v-model="password"
              type="password"
              class="oj-input"
              placeholder="设置密码"
              required
            />
          </div>
          <button
            type="submit"
            class="oj-btn oj-btn--primary oj-btn--lg auth-btn"
            :disabled="!username || password.length < 3"
          >
            注册
          </button>
        </form>
        <p class="text-center text-muted mt-4">
          已有账号？<RouterLink to="/login">去登录</RouterLink>
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
