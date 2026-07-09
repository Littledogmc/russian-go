<script setup lang="ts">
/*
 * 更新日志页面
 * 展示项目的版本迭代记录
 */

interface UpdateItem {
  version: string
  date: string
  items: string[]
}

const updates: UpdateItem[] = [
  {
    version: 'build 3',
    date: '2026-07-10',
    items: ['Backend rework with Redis integration for improved performance.'],
  },

  {
    version: 'build 2',
    date: '2026-07-09',
    items: ['Implemented new methods.'],
  },

  {
    version: 'build 1',
    date: '2026-07-08',
    items: [
      'Initial build, basic functionality implemented.',
      'Added wordbook selection and study session.',
      'Implemented statistics tracking and display.',
      'Created updates page to log version changes.',
    ],
  },
]

// 后续版本在此追加
// {
//   version: 'v1.1.0',
//   date: '2026-xx-xx',
//   items: [
//     '...',
//   ],
// },
</script>

<template>
  <div class="updates-page">
    <div class="oj-page-header">
      <h1>📋 更新日志</h1>
      <p class="text-secondary">记录 RussianGo 的每一个版本变化</p>
    </div>

    <div v-for="upd in updates" :key="upd.version" class="oj-card mb-6">
      <div class="oj-card__header">
        <h3>
          {{ upd.version }}
          <span class="text-muted" style="font-size: 13px; font-weight: 400; margin-left: 12px">
            {{ upd.date }}
          </span>
        </h3>
      </div>
      <div class="oj-card__body">
        <ul class="update-list">
          <li v-for="(item, i) in upd.items" :key="i">{{ item }}</li>
        </ul>
      </div>
    </div>

    <div class="text-center text-muted" style="padding: 20px 0">
      <p>🚧 更多功能开发中…</p>
    </div>
  </div>
</template>

<style scoped>
.updates-page {
  max-width: 720px;
  margin: 0 auto;
}

.update-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.update-list li {
  position: relative;
  padding: 8px 0 8px 24px;
  font-size: 14px;
  line-height: 1.6;
  color: var(--oj-text);
}

.update-list li::before {
  content: '◆';
  position: absolute;
  left: 0;
  color: var(--oj-primary);
  font-size: 10px;
  top: 11px;
}

.update-list li + li {
  border-top: 1px solid var(--oj-card-border);
}
</style>
