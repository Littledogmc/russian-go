/*
 * 背诵检测状态管理 (Pinia Store)
 * 管理：当前词书、本轮答题统计、错词记录
 */
import { ref, computed } from 'vue'
import { defineStore } from 'pinia'

export const useStudyStore = defineStore('study', () => {
  // ===== 当前检测会话 =====
  const currentWordbookId = ref<number | null>(null)
  const currentWordbookName = ref('')

  // ===== 本轮统计 =====
  const totalQuestions = ref(0)
  const correctCount = ref(0)
  const wrongCount = ref(0)

  // 错词列表 (用于回顾)
  interface WrongItem {
    wordId: number
    russian: string
    chinese: string
    userAnswer: string
  }
  const wrongWords = ref<WrongItem[]>([])

  // 正确率
  const accuracy = computed(() => {
    if (totalQuestions.value === 0) return 0
    return Math.round((correctCount.value / totalQuestions.value) * 100)
  })

  // ===== 动作 =====
  function startSession(wordbookId: number, wordbookName: string) {
    currentWordbookId.value = wordbookId
    currentWordbookName.value = wordbookName
    totalQuestions.value = 0
    correctCount.value = 0
    wrongCount.value = 0
    wrongWords.value = []
  }

  function recordCorrect() {
    totalQuestions.value++
    correctCount.value++
  }

  function recordWrong(wordId: number, russian: string, chinese: string, userAnswer: string) {
    totalQuestions.value++
    wrongCount.value++
    wrongWords.value.push({ wordId, russian, chinese, userAnswer })
  }

  function endSession() {
    // 保持数据供统计页读取，不清空
  }

  return {
    currentWordbookId,
    currentWordbookName,
    totalQuestions,
    correctCount,
    wrongCount,
    wrongWords,
    accuracy,
    startSession,
    recordCorrect,
    recordWrong,
    endSession,
  }
})
