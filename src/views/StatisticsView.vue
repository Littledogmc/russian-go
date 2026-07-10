<script setup lang="ts">
/*
 * Statistics page.
 * Displays current session results and wrong-word review list.
 */
import { useRouter } from 'vue-router'
import { useStudyStore } from '@/stores/study'

const router = useRouter()
const store = useStudyStore()

function goStudy(): void {
  router.push('/study')
}
</script>

<template>
  <div class="statistics-page">
    <!-- Session stats -->
    <div class="oj-card mb-6">
      <div class="oj-card__header">
        <h3>📊 本轮检测结果</h3>
      </div>
      <div class="oj-card__body">
        <div v-if="store.totalQuestions > 0" class="stats-grid">
          <div class="stat-item">
            <span class="stat-value">{{ store.totalQuestions }}</span>
            <span class="stat-label">总题数</span>
          </div>
          <div class="stat-item stat-correct">
            <span class="stat-value">{{ store.correctCount }}</span>
            <span class="stat-label">正确</span>
          </div>
          <div class="stat-item stat-wrong">
            <span class="stat-value">{{ store.wrongCount }}</span>
            <span class="stat-label">错误</span>
          </div>
          <div class="stat-item">
            <span
              class="stat-value"
              :style="{
                color:
                  store.accuracy >= 80
                    ? 'var(--oj-success)'
                    : store.accuracy >= 50
                      ? 'var(--oj-warning)'
                      : 'var(--oj-danger)',
              }"
            >
              {{ store.accuracy }}%
            </span>
            <span class="stat-label">正确率</span>
          </div>
        </div>
        <div v-else class="text-center" style="padding: 40px 0">
          <p style="font-size: 36px; margin-bottom: 12px">📝</p>
          <p class="text-secondary">暂无检测数据</p>
          <button class="oj-btn oj-btn--primary mt-4" @click="goStudy">开始检测</button>
        </div>
      </div>
    </div>

    <!-- Wrong word review -->
    <div class="oj-card">
      <div class="oj-card__header">
        <h3>❌ 本轮错词回顾</h3>
        <span class="oj-badge oj-badge--danger" v-if="store.lsWrongWords.length > 0">
          {{ store.lsWrongWords.length }} 词
        </span>
      </div>
      <div class="oj-card__body" style="padding: 0">
        <table class="oj-table" v-if="store.lsWrongWords.length > 0">
          <thead>
            <tr>
              <th>#</th>
              <th>俄文</th>
              <th>中文</th>
              <th>你的输入</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(item, index) in store.lsWrongWords" :key="item.wordId">
              <td class="text-muted" style="font-size: 13px; width: 32px">{{ index + 1 }}</td>
              <td style="font-weight: 600; font-family: 'Segoe UI', 'Times New Roman', serif">
                {{ item.russian }}
              </td>
              <td class="text-secondary">{{ item.chinese }}</td>
              <td>
                <span class="oj-badge oj-badge--danger">{{ item.userAnswer }}</span>
              </td>
            </tr>
          </tbody>
        </table>
        <div v-else class="empty-state">
          <p style="font-size: 36px; margin-bottom: 12px">🎉</p>
          <p>本轮没有错词，继续保持！</p>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.statistics-page {
  max-width: 700px;
  margin: 0 auto;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.stat-item {
  text-align: center;
  padding: 20px 12px;
  border-radius: var(--oj-radius);
  background: var(--oj-stat-item-bg);
}

.stat-value {
  display: block;
  font-size: 28px;
  font-weight: 700;
  color: var(--oj-text);
}

.stat-label {
  display: block;
  margin-top: 4px;
  font-size: 13px;
  color: var(--oj-text-muted);
}

.stat-correct .stat-value {
  color: var(--oj-success);
}

.stat-wrong .stat-value {
  color: var(--oj-danger);
}

.empty-state {
  padding: 40px 20px;
  text-align: center;
  color: var(--oj-text-muted);
}

@media (max-width: 500px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
