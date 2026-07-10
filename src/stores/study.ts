/*
 * Study session state (Pinia store).
 * Manages: current wordbook, per-round stats, wrong word list.
 */
import { ref, computed } from 'vue'
import { defineStore } from 'pinia'

interface WrongItem {
  wordId: number
  russian: string
  chinese: string
  userAnswer: string
}

export const useStudyStore = defineStore('study', () => {
  const currentWordbookId = ref<number | null>(null)
  const currentWordbookName = ref('')
  const totalQuestions = ref(0)
  const correctCount = ref(0)
  const wrongCount = ref(0)
  const lsWrongWords = ref<WrongItem[]>([])

  const accuracy = computed(() => {
    if (totalQuestions.value === 0) return 0
    return Math.round((correctCount.value / totalQuestions.value) * 100)
  })

  function startSession(wordbookId: number, wordbookName: string) {
    currentWordbookId.value = wordbookId
    currentWordbookName.value = wordbookName
    totalQuestions.value = 0
    correctCount.value = 0
    wrongCount.value = 0
    lsWrongWords.value = []
  }

  function recordCorrect() {
    totalQuestions.value++
    correctCount.value++
  }

  function recordWrong(wordId: number, russian: string, chinese: string, userAnswer: string) {
    totalQuestions.value++
    wrongCount.value++
    lsWrongWords.value.push({ wordId, russian, chinese, userAnswer })
  }

  return {
    currentWordbookId,
    currentWordbookName,
    totalQuestions,
    correctCount,
    wrongCount,
    lsWrongWords,
    accuracy,
    startSession,
    recordCorrect,
    recordWrong,
  }
})
