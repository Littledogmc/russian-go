<script setup lang="ts">
/*
 * App shell.
 * Sticky navbar with navigation links, dark-mode toggle, and auth controls.
 * Theme preference persisted in localStorage.
 */
import { ref, onMounted } from 'vue'
import { RouterLink, RouterView, useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/authStore'

const route = useRoute()
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
        RussianGo <span> 单词自测 </span>
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
          <span
            class="oj-navbar__link"
            style="cursor: default; font-size: 13px; color: var(--oj-text-secondary)"
          >
            {{ authStore.username }}
          </span>
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
</style>
