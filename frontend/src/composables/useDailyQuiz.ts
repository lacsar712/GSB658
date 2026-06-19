import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

export interface QuizOption {
  key: string
  val: string
}

export interface QuizSubmitResult {
  isCorrect: boolean
  correctAnswer: string
  earnedPoints: number
  basePoints: number
  bonusPoints: number
  consecutiveDays: number
}

export function useDailyQuiz() {
  const loadingQuiz = ref(true)
  const quizzes = ref<any[]>([])

  const getStreakBonus = (days: number): string => {
    if (days >= 30) return '3.0x'
    if (days >= 15) return '2.0x'
    if (days >= 7) return '1.5x'
    if (days >= 3) return '1.2x'
    return ''
  }

  const parseOptions = (jsonString: string): QuizOption[] => {
    try {
      return JSON.parse(jsonString)
    } catch {
      return []
    }
  }

  const loadTodayQuizzes = async (): Promise<void> => {
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

  const submitQuiz = async (
    qId: number,
    answerKey: string,
    onSuccess?: (result: QuizSubmitResult) => void
  ): Promise<void> => {
    try {
      const response = await request.post('/quiz/submit', {
        questionId: qId,
        answer: answerKey
      })

      if (response.data.code === 200) {
        const result: QuizSubmitResult = response.data.data

        let message = result.isCorrect
          ? `✅ 回答正确！`
          : `❌ 回答错误，正确答案是 ${result.correctAnswer}`

        if (result.earnedPoints > 0) {
          message += ` 获得 ${result.earnedPoints} 积分`

          if (result.bonusPoints > 0) {
            message += ` (基础 ${result.basePoints} + 连续${result.consecutiveDays}天奖励 ${result.bonusPoints})`
          }
        }

        ElMessage({
          message,
          type: result.isCorrect ? 'success' : 'warning',
          duration: 4000
        })

        quizzes.value = quizzes.value.filter(q => q.id !== qId)

        if (onSuccess) {
          onSuccess(result)
        }
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
    getStreakBonus,
    parseOptions,
    loadTodayQuizzes,
    submitQuiz
  }
}
