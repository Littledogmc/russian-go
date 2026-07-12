<script setup lang="ts">
/*
 * Profile page placeholder.
 * Reserved for future user profile features (avatar, stats, settings).
 */
import { useAuthStore } from '@/stores/authStore'

const authStore = useAuthStore()
</script>

<template>
  <div class="profile-page">
    <div class="oj-card">
      <div class="oj-card__header">
        <h3>👤 {{ authStore.username }} 的个人中心</h3>
      </div>
      <div class="oj-card__body">
        <div class="profile-placeholder">
          <p style="font-size: 36px; margin-bottom: 12px">🚧</p>
          <p class="text-muted">个人资料页面开发中，敬请期待…</p>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.profile-page {
  max-width: 700px;
  margin: 0 auto;
}
.profile-placeholder {
  text-align: center;
  padding: 60px 20px;
}
</style>
