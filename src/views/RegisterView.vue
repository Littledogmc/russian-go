<script setup lang="ts">
/*
 * Register page.
 * Creates a new account and automatically logs in.
 */
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/authStore'

const router = useRouter()
const authStore = useAuthStore()

const username = ref('')
const email = ref('')
const password = ref('')
const confirmPassword = ref('')
const isError = ref(false)
const errorMsg = ref('')

const isPasswordMatch = computed(() => {
  return password.value === confirmPassword.value
})

const isFormValid = computed(() => {
  return (
    username.value.length > 0 &&
    email.value.length > 0 &&
    password.value.length >= 3 &&
    isPasswordMatch.value
  )
})

async function submitRegister() {
  isError.value = false
  errorMsg.value = ''

  if (!isPasswordMatch.value) {
    isError.value = true
    errorMsg.value = 'Passwords do not match'
    return
  }

  try {
    await authStore.register(username.value, password.value, email.value)
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
          <span style="text-align: center">Здравствуйте.</span>
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
            <label class="auth-label">邮箱</label>
            <div class="auth-input-wrap">
              <span class="auth-input-icon">📧</span>
              <input
                v-model="email"
                type="email"
                class="oj-input auth-input"
                placeholder="请输入邮箱"
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
          <div class="auth-field">
            <label class="auth-label">确认密码</label>
            <div class="auth-input-wrap">
              <span class="auth-input-icon">🔒</span>
              <input
                v-model="confirmPassword"
                type="password"
                class="oj-input auth-input"
                :class="{ 'input-mismatch': confirmPassword && !isPasswordMatch }"
                placeholder="再次输入密码"
                required
              />
            </div>
            <p v-if="confirmPassword && !isPasswordMatch" class="auth-field-hint">
              Passwords do not match
            </p>
          </div>
          <button
            type="submit"
            class="oj-btn oj-btn--primary oj-btn--lg auth-btn"
            :disabled="!isFormValid"
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
  font-weight: 900;
  font-family: 'Microsoft YaHei';
  color: var(--oj-primary);
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

.input-mismatch {
  border-color: var(--oj-danger) !important;
}

.auth-field-hint {
  color: var(--oj-danger);
  font-size: 12px;
  margin-top: 4px;
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
