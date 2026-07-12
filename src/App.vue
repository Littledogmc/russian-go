<script setup lang="ts">
/*
 * App shell.
 * Sticky navbar with navigation links, dark-mode toggle, auth controls, and user profile popup.
 */
import { ref, onMounted } from 'vue'
import { RouterLink, RouterView, useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/authStore'

const route = useRoute()
const goRouter = useRouter()
const authStore = useAuthStore()

interface NavItem {
  path: string
  name: string
  label: string
}

const lsNavLinks: NavItem[] = [
  { path: '/', name: 'home', label: '首页' },
  { path: '/study', name: 'study', label: '开始检测' },
  { path: '/statistics', name: 'statistics', label: '学习统计' },
  { path: '/updates', name: 'updates', label: '更新日志' },
]

function isActive(path: string): boolean {
  if (path === '/') return route.path === '/'
  return route.path.startsWith(path)
}

const isDark = ref(false)

function toggleTheme(): void {
  isDark.value = !isDark.value
  localStorage.setItem('russian-go-theme', isDark.value ? 'dark' : 'light')
  document.documentElement.setAttribute('data-theme', isDark.value ? 'dark' : 'light')
}

function doLogout(): void {
  authStore.logout()
  goRouter.push('/login')
}

function goProfile(): void {
  goRouter.push('/profile')
}

const isShowMiniProfile = ref(false)

function showMiniProfile(): void {
  isShowMiniProfile.value = true
}

function hideMiniProfile(): void {
  isShowMiniProfile.value = false
}

onMounted(() => {
  const saved = localStorage.getItem('russian-go-theme')
  if (saved === 'dark') {
    isDark.value = true
    document.documentElement.setAttribute('data-theme', 'dark')
  }
})
</script>

<template>
  <nav class="oj-navbar">
    <div class="oj-navbar__inner">
      <RouterLink to="/" class="oj-navbar__logo">
        <img src="/russian-flag.png" alt="flag" class="oj-navbar__flag" />
        RussianGo
      </RouterLink>

      <div class="oj-navbar__links">
        <RouterLink
          v-for="link in lsNavLinks"
          :key="link.path"
          :to="link.path"
          class="oj-navbar__link"
          :class="{ 'oj-navbar__link--active': isActive(link.path) }"
        >
          {{ link.label }}
        </RouterLink>
      </div>

      <div class="oj-navbar__right">
        <!-- Auth controls -->
        <template v-if="authStore.isLoggedIn">
          <div
            class="oj-navbar__user-wrap"
            @mouseenter="showMiniProfile"
            @mouseleave="hideMiniProfile"
          >
            <button class="oj-navbar__user-btn" @click="goProfile">
              {{ authStore.username }}
            </button>
            <!-- Mini profile popup -->
            <div v-if="isShowMiniProfile" class="mini-profile">
              <div class="mini-profile__header">
                <span class="mini-profile__avatar">👤</span>
                <div>
                  <p class="mini-profile__name">{{ authStore.username }}</p>
                  <p class="mini-profile__role">
                    {{ authStore.isAdmin ? '管理员' : authStore.isBanned ? '已封禁' : '普通用户' }}
                  </p>
                </div>
              </div>
              <div class="mini-profile__body">
                <p class="text-muted" style="font-size: 13px">🚧 个人资料功能开发中</p>
              </div>
            </div>
          </div>
          <button class="oj-btn oj-btn--sm oj-btn--outline" @click="doLogout">登出</button>
        </template>
        <template v-else>
          <RouterLink to="/login" class="oj-btn oj-btn--sm oj-btn--outline">登录</RouterLink>
          <RouterLink to="/register" class="oj-btn oj-btn--sm oj-btn--primary">注册</RouterLink>
        </template>

        <button
          class="oj-navbar__theme-btn"
          :title="isDark ? '切换日间模式' : '切换夜间模式'"
          @click="toggleTheme"
        >
          {{ isDark ? '☀️' : '🌙' }}
        </button>
      </div>
    </div>
  </nav>

  <main class="oj-container">
    <RouterView />
  </main>
</template>

<style scoped>
.oj-navbar__flag {
  height: 13px;
  width: auto;
  margin-right: 6px;
  vertical-align: 0px;
}

.oj-navbar__theme-btn {
  background: none;
  border: 1px solid var(--oj-card-border);
  border-radius: var(--oj-radius);
  cursor: pointer;
  font-size: 16px;
  padding: 4px 8px;
  line-height: 1;
  transition: background 0.15s;
}

.oj-navbar__theme-btn:hover {
  background: var(--oj-nav-link-hover);
}

/* User button */
.oj-navbar__user-wrap {
  position: relative;
}

.oj-navbar__user-btn {
  background: none;
  border: none;
  cursor: pointer;
  padding: 6px 14px;
  border-radius: var(--oj-radius);
  color: var(--oj-text-secondary);
  font-size: 14px;
  font-weight: 500;
  font-family: var(--oj-font);
  transition: background 0.15s;
}

.oj-navbar__user-btn:hover {
  background: var(--oj-nav-link-hover);
  color: var(--oj-text);
}

/* Mini profile popup */
.mini-profile {
  position: absolute;
  top: calc(100% + 6px);
  right: 0;
  width: 240px;
  background: var(--oj-card-bg);
  border: 1px solid var(--oj-card-border);
  border-radius: var(--oj-radius);
  box-shadow: var(--oj-shadow);
  z-index: 200;
}

.mini-profile__header {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  border-bottom: 1px solid var(--oj-card-border);
}

.mini-profile__avatar {
  font-size: 28px;
}

.mini-profile__name {
  font-size: 14px;
  font-weight: 600;
  color: var(--oj-text);
}

.mini-profile__role {
  font-size: 12px;
  color: var(--oj-text-muted);
  margin-top: 2px;
}

.mini-profile__body {
  padding: 14px 16px;
}
</style>
