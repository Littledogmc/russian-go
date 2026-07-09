<script setup lang="ts">
import { RouterLink, RouterView, useRoute } from 'vue-router'

const route = useRoute()

const navLinks = [
  { path: '/', name: 'home', label: '首页' },
  { path: '/study', name: 'study', label: '开始检测' },
  { path: '/statistics', name: 'statistics', label: '学习统计' },
  { path: '/updates', name: 'updates', label: '更新日志' },
]

const isActive = (path: string) => {
  if (path === '/') return route.path === '/'
  return route.path.startsWith(path)
}
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
          v-for="link in navLinks"
          :key="link.path"
          :to="link.path"
          class="oj-navbar__link"
          :class="{ 'oj-navbar__link--active': isActive(link.path) }"
        >
          {{ link.label }}
        </RouterLink>
      </div>

      <div class="oj-navbar__right">
        <span
          class="oj-navbar__link"
          style="cursor: default; color: var(--oj-text-muted); font-size: 13px"
        >
          今日学习 0 词
        </span>
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
</style>
