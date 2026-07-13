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
const licenseText = ref('')
const showLicenseModal = ref(false)

async function showLicense() {
  try {
    const resp = await fetch('/LICENSE')
    licenseText.value = await resp.text()
    showLicenseModal.value = true
  } catch {
    licenseText.value = 'MIT License'
    showLicenseModal.value = true
  }
}

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
        <div class="auth-brand">
          <span style="text-align: center">Здравствуйте.</span>
        </div>
      </div>
      <div class="oj-card__body">
        <div v-if="isError" class="auth-error">{{ errorMsg }}</div>
        <form @submit.prevent="submitLogin">
          <div class="auth-field">
            <label class="auth-label">用户名</label>
            <div class="auth-input-wrap">
              <span class="auth-input-icon">👤</span>
              <input
                v-model="username"
                type="text"
                class="oj-input auth-input"
                placeholder="输入用户名"
                required
              />
            </div>
          </div>
          <div class="auth-field">
            <label class="auth-label">密码</label>
            <div class="auth-input-wrap">
              <span class="auth-input-icon">🔒</span>
              <input
                v-model="password"
                type="password"
                class="oj-input auth-input"
                placeholder="输入密码"
                required
              />
            </div>
          </div>
          <button
            type="submit"
            class="oj-btn oj-btn--primary oj-btn--lg auth-btn"
            :disabled="!username || !password"
          >
            登录
          </button>
        </form>
        <p class="auth-switch">还没有账号？<RouterLink to="/register">立即注册</RouterLink></p>
      </div>
    </div>
    <!-- Footer -->
    <div class="auth-footer">
      <p>
        Copyright (C) Broadwell 2026.
        <a href="#" class="license-link" @click.prevent="showLicense">Check License</a>
      </p>
    </div>

    <!-- License modal -->
    <div v-if="showLicenseModal" class="license-overlay" @click.self="showLicenseModal = false">
      <div class="oj-card license-modal">
        <div class="oj-card__header flex-between">
          <h3>MIT License</h3>
          <button class="oj-btn oj-btn--sm oj-btn--outline" @click="showLicenseModal = false">
            关闭
          </button>
        </div>
        <div class="oj-card__body">
          <pre class="license-text">{{ licenseText }}</pre>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.auth-page {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  min-height: calc(100vh - 56px - 48px);
  padding: 20px;
}

.auth-footer {
  text-align: center;
  color: var(--oj-text-muted);
  padding: 24px 0 12px;
  font-size: 13px;
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

.license-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 999;
  padding: 20px;
}

.license-modal {
  max-width: 600px;
  width: 100%;
  max-height: 80vh;
  overflow-y: auto;
}

.license-text {
  white-space: pre-wrap;
  font-size: 13px;
  line-height: 1.7;
  color: var(--oj-text-secondary);
  margin: 0;
  font-family: var(--oj-font);
}

.license-link {
  color: var(--oj-text-muted);
  text-decoration: none;
}

.license-link:hover {
  color: var(--oj-primary);
  text-decoration: underline;
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
