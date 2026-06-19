package com.community.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.community.system.entity.QuestionBank;
import com.community.system.entity.QuizRecord;

import java.util.List;
import java.util.Map;

public interface QuizService extends IService<QuizRecord> {

    /**
     * 获取今日答题任务 (每日随机推1-3题)
     */
    List<QuestionBank> getTodayQuiz(Long userId);

    /**
     * 提交单题答案并计算积分
     * @return 批注结果及得分
     */
    Map<String, Object> submitAnswer(Long userId, Long questionId, String answer);

    /**
     * 获取打卡人次统计
     * @param days 统计天数
     * @return 打卡人次趋势数据
     */
    Map<String, Object> getCheckinStats(Integer days);

    /**
     * 获取答题正确率统计
     * @return 答题正确率数据
     */
    Map<String, Object> getAccuracyStats();
}
