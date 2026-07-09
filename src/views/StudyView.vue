<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { pickWord, checkAnswer, resetSession, finishSession } from '@/api/study'
import { fetchWordbooks } from '@/api/wordbook'
import { useStudyStore } from '@/stores/study'
import type { PickWordResult } from '@/types'
import RussianKeyboard from '@/components/RussianKeyboard.vue'

const route = useRoute()
const router = useRouter()
const store = useStudyStore()

const currentWord = ref<PickWordResult | null>(null)
const userInput = ref('')
const resultMsg = ref('')
const resultCorrect = ref<boolean | null>(null)
const showNextBtn = ref(false)
const allDone = ref(false)
const wordbooks = ref<{ id: number; name: string }[]>([])
const selectedWordbookId = ref<number | null>(null)
const showKeyboard = ref(false)

// 读取 URL 参数 wordbookId
onMounted(async () => {
  const qParam = route.query.wordbookId
  if (typeof qParam === 'string') {
    selectedWordbookId.value = Number(qParam)
  }
  // 加载词书列表（供选择）
  try {
    const wb = await fetchWordbooks()
    wordbooks.value = wb
    if (!selectedWordbookId.value && wb.length > 0) {
      selectedWordbookId.value = wb[0]!.id
    }
  } catch {
    // 静默
  }
  if (selectedWordbookId.value != null) {
    await startNewSession()
  }
})

async function startNewSession() {
  const wbId = selectedWordbookId.value
  if (wbId == null) return

  const wb = wordbooks.value.find((w) => w.id === wbId)
  store.startSession(wbId, wb?.name || '词书')

  await resetSession()
  allDone.value = false
  resultMsg.value = ''
  resultCorrect.value = null
  showNextBtn.value = false
  userInput.value = ''
  await fetchNext()
}

async function fetchNext() {
  if (!selectedWordbookId.value) return
  const word = await pickWord(selectedWordbookId.value)
  if (!word) {
    allDone.value = true
    currentWord.value = null
    // 自动记录活动
    await finishSession(
      selectedWordbookId.value,
      store.currentWordbookName,
      store.totalQuestions,
      store.correctCount,
    )
    return
  }
  currentWord.value = word
  resultMsg.value = ''
  resultCorrect.value = null
  showNextBtn.value = false
  userInput.value = ''
}

async function submitAnswer() {
  if (!currentWord.value || !userInput.value.trim()) return

  const res = await checkAnswer(currentWord.value.wordId, userInput.value.trim())
  resultCorrect.value = res.correct
  resultMsg.value = res.correct ? '✅ 正确！' : `❌ 错误。正确答案：${res.correctAnswer}`

  if (res.correct) {
    store.recordCorrect()
  } else {
    store.recordWrong(
      currentWord.value.wordId,
      currentWord.value.russian,
      currentWord.value.chinese,
      userInput.value.trim(),
    )
  }
  showNextBtn.value = true
}

async function handleNext() {
  await fetchNext()
}

function selectWordbook(id: number) {
  selectedWordbookId.value = id
}

function goToResults() {
  router.push('/statistics')
}

// 键盘快捷键 Enter 提交
function onKeydown(e: KeyboardEvent) {
  if (e.key === 'Enter') {
    if (!showNextBtn.value && userInput.value.trim()) {
      submitAnswer()
    } else if (showNextBtn.value && !allDone.value) {
      handleNext()
    }
  }
}
</script>

<template>
  <div class="study-page" @keydown="onKeydown">
    <!-- 词书选择（未选择时显示） -->
    <div v-if="!selectedWordbookId" class="oj-card mb-6">
      <div class="oj-card__header">
        <h3>📚 选择词书</h3>
      </div>
      <div class="oj-card__body">
        <div class="wordbook-select">
          <button
            v-for="wb in wordbooks"
            :key="wb.id"
            class="oj-btn"
            :class="selectedWordbookId === wb.id ? 'oj-btn--primary' : 'oj-btn--outline'"
            @click="selectWordbook(wb.id)"
          >
            {{ wb.name }}
          </button>
        </div>
        <div class="mt-4" v-if="selectedWordbookId">
          <button class="oj-btn oj-btn--primary oj-btn--lg" @click="startNewSession">
            ▶ 开始检测
          </button>
        </div>
      </div>
    </div>

    <!-- 检测界面 -->
    <div v-else>
      <!-- 顶部：词书名 + 进度 + 结束检测 -->
      <div class="oj-card mb-4">
        <div class="oj-card__body flex-between">
          <div>
            <strong style="font-size: 16px">{{ store.currentWordbookName }}</strong>
            <span class="text-muted" style="margin-left: 12px; font-size: 13px">
              已答 {{ store.totalQuestions }} 题 · 正确 {{ store.correctCount }} 题
              <template v-if="store.totalQuestions > 0"> · 正确率 {{ store.accuracy }}% </template>
            </span>
          </div>
          <div class="flex-center" style="gap: 8px">
            <button
              class="oj-btn oj-btn--sm"
              :class="showKeyboard ? 'oj-btn--primary' : 'oj-btn--outline'"
              @click="showKeyboard = !showKeyboard"
            >
              ⌨️ {{ showKeyboard ? '关闭键盘' : '键盘' }}
            </button>
            <button class="oj-btn oj-btn--sm oj-btn--outline" @click="goToResults">结束本轮</button>
          </div>
        </div>
      </div>

      <!-- 全部完成 -->
      <div class="oj-card" v-if="allDone">
        <div class="oj-card__body text-center" style="padding: 60px 20px">
          <p style="font-size: 48px; margin-bottom: 16px">🎉</p>
          <h2 style="margin-bottom: 8px">本轮完成！</h2>
          <p class="text-secondary mb-4">
            共 {{ store.totalQuestions }} 题，正确 {{ store.correctCount }} 题， 正确率
            {{ store.accuracy }}%
          </p>
          <div class="flex-center gap-4" style="justify-content: center">
            <button class="oj-btn oj-btn--primary" @click="startNewSession">再来一轮</button>
            <button class="oj-btn oj-btn--outline" @click="goToResults">查看统计</button>
          </div>
        </div>
      </div>

      <!-- 答题卡片 -->
      <div class="oj-card" v-else-if="currentWord">
        <div class="oj-card__body" style="padding: 40px 32px">
          <!-- 提示区 -->
          <div class="text-center mb-6">
            <p class="text-muted" style="font-size: 13px; margin-bottom: 4px">请根据中文输入俄文</p>
            <p style="font-size: 28px; font-weight: 700; color: var(--oj-primary)">
              {{ currentWord.chinese }}
            </p>
            <p v-if="currentWord.english" class="text-muted mt-2" style="font-size: 14px">
              {{ currentWord.english }}
            </p>
          </div>

          <!-- 输入区 -->
          <div class="input-area">
            <input
              v-model="userInput"
              type="text"
              class="oj-input"
              placeholder="输入俄文单词..."
              :disabled="showNextBtn"
              autofocus
            />
            <button
              class="oj-btn oj-btn--primary oj-btn--lg"
              :disabled="!userInput.trim() || showNextBtn"
              @click="submitAnswer"
              style="margin-top: 16px; width: 100%"
            >
              提交答案
            </button>
          </div>

          <!-- 虚拟键盘 -->
          <RussianKeyboard
            v-if="showKeyboard"
            :disabled="showNextBtn"
            @input="(char: string) => (userInput += char)"
            @backspace="userInput = userInput.slice(0, -1)"
            @enter="submitAnswer"
          />

          <!-- 结果反馈 -->
          <div
            v-if="resultMsg"
            class="result-feedback mt-4"
            :class="{ correct: resultCorrect, wrong: resultCorrect === false }"
          >
            <p style="font-size: 18px; font-weight: 600">{{ resultMsg }}</p>
            <p v-if="resultCorrect === false" class="mt-2 text-secondary">
              你的输入：{{ userInput }}
            </p>
            <button v-if="showNextBtn" class="oj-btn oj-btn--primary mt-4" @click="handleNext">
              下一题 →
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.study-page {
  max-width: 640px;
  margin: 0 auto;
}

.wordbook-select {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.input-area {
  max-width: 420px;
  margin: 0 auto;
}

.oj-input {
  width: 100%;
  padding: 14px 16px;
  font-size: 20px;
  border: 2px solid var(--oj-card-border);
  border-radius: var(--oj-radius);
  outline: none;
  transition: border-color 0.2s;
  text-align: center;
  font-family: 'Segoe UI', 'Times New Roman', serif;
}

.oj-input:focus {
  border-color: var(--oj-primary);
}

.oj-input:disabled {
  background: #f5f5f5;
}

.result-feedback {
  text-align: center;
  padding: 20px;
  border-radius: var(--oj-radius);
}

.result-feedback.correct {
  background: #e8f5e9;
}

.result-feedback.wrong {
  background: #fbe9e7;
}
</style>
