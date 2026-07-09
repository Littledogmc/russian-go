<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { fetchWordbooks } from '@/api/wordbook'
import { fetchActivities, fetchErrorWords } from '@/api/study'
import type { WordbookSummary, ActivityRecord, ErrorWord } from '@/types'

const router = useRouter()

const wordbooks = ref<WordbookSummary[]>([])
const recentActivities = ref<ActivityRecord[]>([])
const errorWords = ref<ErrorWord[]>([])
const loading = ref(true)

const accuracy = (correct: number, total: number) => {
  if (total === 0) return 0
  return Math.round((correct / total) * 100)
}

const goStudy = () => router.push('/study')
const goStatistics = () => router.push('/statistics')
const startStudy = (wordbookId: number) => {
  router.push({ path: '/study', query: { wordbookId } })
}

onMounted(async () => {
  try {
    const [wb, acts, errs] = await Promise.all([
      fetchWordbooks(),
      fetchActivities(5),
      fetchErrorWords(5),
    ])
    wordbooks.value = wb
    recentActivities.value = acts
    errorWords.value = errs
  } catch {
    // 后端未启动时静默
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <div class="home">
    <div class="oj-card mb-6">
      <div class="welcome-banner">
        <div class="welcome-banner__text">
          <h1>你好！今天想学点什么？</h1>
          <p class="text-secondary mt-2">
            站点已内置 <strong>{{ wordbooks.length }}</strong> 本词书
          </p>
        </div>
        <div class="welcome-banner__actions">
          <button class="oj-btn oj-btn--primary" @click="goStudy"><span>▶</span> 开始检测</button>
          <button class="oj-btn oj-btn--outline" @click="goStatistics">
            <span>📊</span> 学习统计
          </button>
        </div>
      </div>
    </div>

    <div class="oj-grid oj-grid--2 mb-6">
      <!-- 左侧：近期测试活动 -->
      <div class="oj-card">
        <div class="oj-card__header">
          <h3>📋 近期测试活动</h3>
          <RouterLink to="/statistics" class="oj-btn oj-btn--sm oj-btn--outline"
            >查看全部</RouterLink
          >
        </div>
        <div class="oj-card__body" style="padding: 0">
          <table class="oj-table" v-if="!loading && recentActivities.length > 0">
            <thead>
              <tr>
                <th>词书</th>
                <th>答题</th>
                <th>正确率</th>
                <th>时间</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="act in recentActivities" :key="act.id">
                <td style="color: var(--oj-primary); font-weight: 500">{{ act.wordbook }}</td>
                <td>
                  <span class="oj-status">
                    <span class="oj-status__dot oj-status__dot--green"></span>
                    {{ act.correct }}/{{ act.wordCount }}
                  </span>
                </td>
                <td>
                  <span
                    class="oj-badge"
                    :class="
                      accuracy(act.correct, act.wordCount) >= 80
                        ? 'oj-badge--success'
                        : 'oj-badge--warning'
                    "
                  >
                    {{ accuracy(act.correct, act.wordCount) }}%
                  </span>
                </td>
                <td class="text-muted" style="font-size: 13px">{{ act.date }}</td>
              </tr>
            </tbody>
          </table>
          <div
            v-else-if="!loading"
            style="padding: 40px 20px; text-align: center; color: var(--oj-text-muted)"
          >
            <p style="font-size: 32px; margin-bottom: 12px">📝</p>
            <p>还没有测试记录</p>
            <button class="oj-btn oj-btn--primary mt-4" @click="goStudy">开始第一次检测</button>
          </div>
          <div v-else style="padding: 40px 20px; text-align: center; color: var(--oj-text-muted)">
            <p>加载中...</p>
          </div>
        </div>
      </div>

      <!-- 右侧：高频错词榜 -->
      <div class="oj-card">
        <div class="oj-card__header">
          <h3>❌ 高频错词榜</h3>
          <span class="oj-badge oj-badge--danger" v-if="errorWords.length > 0"
            >{{ errorWords.length }} 词需巩固</span
          >
        </div>
        <div class="oj-card__body" style="padding: 0">
          <table class="oj-table" v-if="!loading && errorWords.length > 0">
            <thead>
              <tr>
                <th style="width: 32px">#</th>
                <th>俄文</th>
                <th>中文</th>
                <th>错误次数</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(word, index) in errorWords" :key="word.id">
                <td class="text-muted" style="font-size: 13px">{{ index + 1 }}</td>
                <td style="font-weight: 600; font-family: 'Segoe UI', 'Times New Roman', serif">
                  {{ word.russian }}
                </td>
                <td class="text-secondary">{{ word.chinese }}</td>
                <td>
                  <span
                    class="oj-badge"
                    :class="word.wrongCount >= 4 ? 'oj-badge--danger' : 'oj-badge--warning'"
                  >
                    ✗ {{ word.wrongCount }}
                  </span>
                </td>
              </tr>
            </tbody>
          </table>
          <div
            v-else-if="!loading"
            style="padding: 40px 20px; text-align: center; color: var(--oj-text-muted)"
          >
            <p style="font-size: 32px; margin-bottom: 12px">🎉</p>
            <p>暂无错词记录</p>
          </div>
          <div v-else style="padding: 40px 20px; text-align: center; color: var(--oj-text-muted)">
            <p>加载中...</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 下方：站点内置词书 -->
    <div class="oj-card">
      <div class="oj-card__header">
        <h3>📚 选择词书</h3>
        <span style="font-size: 13px; color: var(--oj-text-muted)"
          >站点内置共 {{ wordbooks.length }} 本</span
        >
      </div>
      <div class="oj-card__body" style="padding: 0">
        <table class="oj-table" v-if="!loading && wordbooks.length > 0">
          <thead>
            <tr>
              <th>词书名称</th>
              <th>词数</th>
              <th style="text-align: right">操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="wb in wordbooks" :key="wb.id">
              <td style="font-weight: 500">{{ wb.name }}</td>
              <td class="text-secondary">{{ wb.wordCount }} 词</td>
              <td style="text-align: right">
                <button class="oj-btn oj-btn--sm oj-btn--primary" @click="startStudy(wb.id)">
                  开始检测
                </button>
              </td>
            </tr>
          </tbody>
        </table>
        <div
          v-else-if="!loading"
          style="padding: 60px 20px; text-align: center; color: var(--oj-text-muted)"
        >
          <p style="font-size: 40px; margin-bottom: 16px">📖</p>
          <p style="font-size: 16px">暂无词书，请先启动后端服务</p>
        </div>
        <div v-else style="padding: 60px 20px; text-align: center; color: var(--oj-text-muted)">
          <p>加载中...</p>
        </div>
      </div>
    </div>
    <!-- 底部：关于界面 -->
    <div class="text-center text-muted" style="padding: 30px 0">
      <p>Copyright (C) Broadwell 2026. All rights reserved.</p>
      <p>For educational purposes only - Do not distribute!</p>
    </div>
  </div>
</template>

<style scoped>
.home {
  max-width: 1000px;
  margin: 0 auto;
}
.welcome-banner {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 24px 28px;
  flex-wrap: wrap;
  gap: 16px;
}
.welcome-banner__text h1 {
  font-size: 22px;
  font-weight: 700;
  color: var(--oj-text);
}
.welcome-banner__actions {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}
</style>
