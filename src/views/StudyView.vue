<script setup lang="ts">
/*
 * Study page.
 * Wordbook selection -> random word dictation -> answer check -> next word.
 * Supports virtual Russian keyboard and keyboard shortcut (Enter).
 */
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { pickWord, checkAnswer, resetSession, finishSession } from '@/api/study'
import { getWordbooks } from '@/api/wordbook'
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
const isShowNext = ref(false)
const isAllDone = ref(false)
const lsWordbooks = ref<{ id: number; name: string }[]>([])
const selectedWordbookId = ref<number | null>(null)
const isShowKeyboard = ref(false)

onMounted(async () => {
  const qParam = route.query.wordbookId
  if (typeof qParam === 'string') {
    selectedWordbookId.value = Number(qParam)
  }
  try {
    const wb = await getWordbooks()
    lsWordbooks.value = wb
    if (!selectedWordbookId.value && wb.length > 0) {
      selectedWordbookId.value = wb[0]!.id
    }
  } catch {
    // Backend not available
  }
  if (selectedWordbookId.value != null) {
    await startNewSession()
  }
})

async function startNewSession() {
  const wbId = selectedWordbookId.value
  if (wbId == null) return

  const wb = lsWordbooks.value.find((w) => w.id === wbId)
  store.startSession(wbId, wb?.name || '词书')

  await resetSession(wbId)
  isAllDone.value = false
  resultMsg.value = ''
  resultCorrect.value = null
  isShowNext.value = false
  userInput.value = ''
  await fetchNext()
}

async function fetchNext() {
  const wbId = selectedWordbookId.value
  if (wbId == null) return

  const word = await pickWord(wbId)
  if (!word) {
    isAllDone.value = true
    currentWord.value = null
    await finishSession(wbId, store.currentWordbookName, store.totalQuestions, store.correctCount)
    return
  }
  currentWord.value = word
  resultMsg.value = ''
  resultCorrect.value = null
  isShowNext.value = false
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
  isShowNext.value = true
}

async function goNext() {
  await fetchNext()
}

function selectWordbook(id: number) {
  selectedWordbookId.value = id
}

function goToResults() {
  router.push('/statistics')
}

function onKeydown(e: KeyboardEvent) {
  if (e.key === 'Enter') {
    if (!isShowNext.value && userInput.value.trim()) {
      submitAnswer()
    } else if (isShowNext.value && !isAllDone.value) {
      goNext()
    }
  }
}
</script>

<template>
  <div class="study-page" @keydown="onKeydown">
    <!-- Wordbook selection -->
    <div v-if="!selectedWordbookId" class="oj-card mb-6">
      <div class="oj-card__header">
        <h3>📚 选择词书</h3>
      </div>
      <div class="oj-card__body">
        <div class="wordbook-select">
          <button
            v-for="wb in lsWordbooks"
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

    <!-- Dictation interface -->
    <div v-else>
      <!-- Top bar: wordbook name + progress + controls -->
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
              :class="isShowKeyboard ? 'oj-btn--primary' : 'oj-btn--outline'"
              @click="isShowKeyboard = !isShowKeyboard"
            >
              ⌨️ {{ isShowKeyboard ? '关闭键盘' : '键盘' }}
            </button>
            <button class="oj-btn oj-btn--sm oj-btn--outline" @click="goToResults">结束本轮</button>
          </div>
        </div>
      </div>

      <!-- All done -->
      <div class="oj-card" v-if="isAllDone">
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

      <!-- Answer card -->
      <div class="oj-card" v-else-if="currentWord">
        <div class="oj-card__body" style="padding: 40px 32px">
          <!-- Prompt -->
          <div class="text-center mb-6">
            <p class="text-muted" style="font-size: 13px; margin-bottom: 4px">请根据中文输入俄文</p>
            <p style="font-size: 28px; font-weight: 700; color: var(--oj-primary)">
              {{ currentWord.chinese }}
            </p>
            <p v-if="currentWord.english" class="text-muted mt-2" style="font-size: 14px">
              {{ currentWord.english }}
            </p>
          </div>

          <!-- Input -->
          <div class="input-area">
            <input
              v-model="userInput"
              type="text"
              class="oj-input"
              placeholder="输入俄文单词..."
              :disabled="isShowNext"
              autofocus
            />
            <button
              class="oj-btn oj-btn--primary oj-btn--lg"
              :disabled="!userInput.trim() || isShowNext"
              @click="submitAnswer"
              style="margin-top: 16px; width: 100%"
            >
              提交答案
            </button>
          </div>

          <!-- Virtual keyboard -->
          <RussianKeyboard
            v-if="isShowKeyboard"
            :disabled="isShowNext"
            @input="(char: string) => (userInput += char)"
            @backspace="userInput = userInput.slice(0, -1)"
            @enter="submitAnswer"
          />

          <!-- Result feedback -->
          <div
            v-if="resultMsg"
            class="result-feedback mt-4"
            :class="{ correct: resultCorrect, wrong: resultCorrect === false }"
          >
            <p style="font-size: 18px; font-weight: 600">{{ resultMsg }}</p>
            <p v-if="resultCorrect === false" class="mt-2 text-secondary">
              你的输入：{{ userInput }}
            </p>
            <button v-if="isShowNext" class="oj-btn oj-btn--primary mt-4" @click="goNext">
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
