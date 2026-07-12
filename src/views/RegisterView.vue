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
        <div class="auth-brand">
          <img src="/russian-flag.png" alt="flag" class="auth-flag" />
          <span>RussianGo</span>
          <span class="text-muted" style="font-weight: 300; font-size: 14px; margin-left: 6px"
            >单词自测</span
          >
        </div>
      </div>
      <div class="oj-card__body">
        <div v-if="isError" class="auth-error">{{ errorMsg }}</div>
        <form @submit.prevent="submitRegister">
          <div class="auth-field">
            <label class="auth-label">用户名</label>
            <div class="auth-input-wrap">
              <span class="auth-input-icon">👤</span>
              <input
                v-model="username"
                type="text"
                class="oj-input auth-input"
                placeholder="设置用户名"
                required
              />
            </div>
          </div>
          <div class="auth-field">
            <label class="auth-label">密码（至少3位）</label>
            <div class="auth-input-wrap">
              <span class="auth-input-icon">🔒</span>
              <input
                v-model="password"
                type="password"
                class="oj-input auth-input"
                placeholder="设置密码"
                required
              />
            </div>
          </div>
          <button
            type="submit"
            class="oj-btn oj-btn--primary oj-btn--lg auth-btn"
            :disabled="!username || password.length < 3"
          >
            注册
          </button>
        </form>
        <p class="auth-switch">已有账号？<RouterLink to="/login">去登录</RouterLink></p>
      </div>
    </div>
  </div>
</template>

<style scoped>
.auth-page {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: calc(100vh - 56px - 48px);
  padding: 20px;
}

.auth-card {
  width: 100%;
  max-width: 420px;
}

.auth-brand {
  display: flex;
  align-items: center;
  font-size: 17px;
  font-weight: 700;
  color: var(--oj-primary);
}

.auth-flag {
  height: 18px;
  width: auto;
  margin-right: 8px;
  border-radius: 2px;
}

.auth-field {
  margin-bottom: 18px;
}

.auth-label {
  display: block;
  font-size: 13px;
  font-weight: 600;
  margin-bottom: 6px;
  color: var(--oj-text-secondary);
}

.auth-input-wrap {
  position: relative;
}

.auth-input-icon {
  position: absolute;
  left: 12px;
  top: 50%;
  transform: translateY(-50%);
  font-size: 15px;
  pointer-events: none;
}

.auth-input {
  padding-left: 36px !important;
  padding-top: 12px !important;
  padding-bottom: 12px !important;
  font-size: 15px !important;
}

.auth-btn {
  width: 100%;
  margin-top: 4px;
  padding: 12px 0 !important;
  font-size: 15px !important;
  justify-content: center;
}

.auth-switch {
  text-align: center;
  color: var(--oj-text-muted);
  margin-top: 18px;
  font-size: 14px;
}

.auth-error {
  background: var(--oj-result-wrong-bg);
  color: var(--oj-danger);
  padding: 10px 14px;
  border-radius: var(--oj-radius);
  margin-bottom: 18px;
  font-size: 14px;
  text-align: center;
}
</style>
