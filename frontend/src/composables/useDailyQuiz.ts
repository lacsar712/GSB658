import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

export interface QuizItem {
  id: number
  content: string
  optionsJson: string
  [key: string]: any
}

export interface QuizUserState {
  pointBalance: number
  consecutiveDays: number
  [key: string]: any
}

export function useDailyQuiz(userInfo: { value: QuizUserState }) {
  const loadingQuiz = ref(true)
  const quizzes = ref<QuizItem[]>([])

  const loadTodayQuizzes = async () => {
    loadingQuiz.value = true
    try {
      const response = await request.get('/quiz/today')
      if (response.data.code === 200) {
        quizzes.value = response.data.data || []
      }
    } catch (error) {
      console.error('获取今日答题失败:', error)
    } finally {
      loadingQuiz.value = false
    }
  }

  const getStreakBonus = (days: number) => {
    if (days >= 30) return '3.0x'
    if (days >= 15) return '2.0x'
    if (days >= 7) return '1.5x'
    if (days >= 3) return '1.2x'
    return ''
  }

  const parseOptions = (jsonString: string) => {
    try { return JSON.parse(jsonString) } catch { return [] }
  }

  const buildQuizResultMessage = (result: any) => {
    let message = result.isCorrect
      ? `✅ 回答正确！`
      : `❌ 回答错误，正确答案是 ${result.correctAnswer}`

    if (result.earnedPoints > 0) {
      message += ` 获得 ${result.earnedPoints} 积分`
      if (result.bonusPoints > 0) {
        message += ` (基础 ${result.basePoints} + 连续${result.consecutiveDays}天奖励 ${result.bonusPoints})`
      }
    }
    return message
  }

  const applyQuizReward = (result: any) => {
    if (result.earnedPoints > 0) {
      userInfo.value.pointBalance = (userInfo.value.pointBalance as number) + result.earnedPoints
      userInfo.value.consecutiveDays = result.consecutiveDays
    }
  }

  const removeAnsweredQuiz = (qId: number) => {
    quizzes.value = quizzes.value.filter(q => q.id !== qId)
  }

  const submitQuiz = async (qId: number, answerKey: string) => {
    try {
      const response = await request.post('/quiz/submit', {
        questionId: qId,
        answer: answerKey
      })

      if (response.data.code === 200) {
        const result = response.data.data
        const message = buildQuizResultMessage(result)
        applyQuizReward(result)

        ElMessage({
          message,
          type: result.isCorrect ? 'success' : 'warning',
          duration: 4000
        })

        removeAnsweredQuiz(qId)
      } else {
        ElMessage.error(response.data.message || '提交失败')
      }
    } catch (error: any) {
      console.error('提交答案失败:', error)
      ElMessage.error(error.response?.data?.message || '提交失败，请稍后重试')
    }
  }

  return {
    loadingQuiz,
    quizzes,
    loadTodayQuizzes,
    submitQuiz,
    getStreakBonus,
    parseOptions,
    buildQuizResultMessage,
    applyQuizReward,
    removeAnsweredQuiz
  }
}
