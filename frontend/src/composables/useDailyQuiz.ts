import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

export interface QuizOption {
  key: string
  val: string
}

export interface Quiz {
  id: number
  content: string
  optionsJson: string
}

export interface QuizSubmitResult {
  isCorrect: boolean
  correctAnswer: string
  earnedPoints: number
  basePoints: number
  bonusPoints: number
  consecutiveDays: number
}

export interface UserQuizData {
  pointBalance: number
  consecutiveDays: number
}

export function useDailyQuiz() {
  const loadingQuiz = ref(true)
  const quizzes = ref<Quiz[]>([])

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

  const parseOptions = (jsonString: string): QuizOption[] => {
    try {
      return JSON.parse(jsonString)
    } catch {
      return []
    }
  }

  const submitQuiz = async (
    qId: number,
    answerKey: string,
    userData: UserQuizData
  ): Promise<QuizSubmitResult | null> => {
    try {
      const response = await request.post('/quiz/submit', {
        questionId: qId,
        answer: answerKey
      })

      if (response.data.code === 200) {
        const result = response.data.data as QuizSubmitResult

        let message = result.isCorrect
          ? `✅ 回答正确！`
          : `❌ 回答错误，正确答案是 ${result.correctAnswer}`

        if (result.earnedPoints > 0) {
          message += ` 获得 ${result.earnedPoints} 积分`

          if (result.bonusPoints > 0) {
            message += ` (基础 ${result.basePoints} + 连续${result.consecutiveDays}天奖励 ${result.bonusPoints})`
          }

          userData.pointBalance = (userData.pointBalance as number) + result.earnedPoints
          userData.consecutiveDays = result.consecutiveDays
        }

        ElMessage({
          message,
          type: result.isCorrect ? 'success' : 'warning',
          duration: 4000
        })

        quizzes.value = quizzes.value.filter(q => q.id !== qId)

        return result
      } else {
        ElMessage.error(response.data.message || '提交失败')
        return null
      }
    } catch (error: any) {
      console.error('提交答案失败:', error)
      ElMessage.error(error.response?.data?.message || '提交失败，请稍后重试')
      return null
    }
  }

  return {
    loadingQuiz,
    quizzes,
    loadTodayQuizzes,
    getStreakBonus,
    parseOptions,
    submitQuiz
  }
}
