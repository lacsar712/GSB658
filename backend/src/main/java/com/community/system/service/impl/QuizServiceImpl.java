package com.community.system.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.community.system.entity.QuestionBank;
import com.community.system.entity.QuizRecord;
import com.community.system.entity.User;
import com.community.system.mapper.QuestionBankMapper;
import com.community.system.mapper.QuizRecordMapper;
import com.community.system.mapper.UserMapper;
import com.community.system.service.PointService;
import com.community.system.service.QuizService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuizServiceImpl extends ServiceImpl<QuizRecordMapper, QuizRecord> implements QuizService {

    private final QuestionBankMapper questionBankMapper;
    private final PointService pointService;
    private final UserMapper userMapper;

    @Override
    public List<QuestionBank> getTodayQuiz(Long userId) {
        // 取题库全部（实际应做分页/缓存，此处简化）
        List<QuestionBank> allQuestions = questionBankMapper.selectList(null);
        if (allQuestions == null || allQuestions.isEmpty()) {
            return null;
        }
        
        // 随机发3题(如果题库不满3题发全部)
        int size = Math.min(allQuestions.size(), 3);
        List<QuestionBank> selected = RandomUtil.randomEleList(allQuestions, size);

        // 去掉正确答案返回给前端
        return selected.stream().peek(q -> q.setCorrectAnswer(null)).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> submitAnswer(Long userId, Long questionId, String answer) {
        QuestionBank qb = questionBankMapper.selectById(questionId);
        if (qb == null) {
            throw new IllegalArgumentException("题目不存在");
        }

        // 今天该特定题是否答过？防刷处理
        String todayStart = DateUtil.beginOfDay(new Date()).toString();
        String todayEnd = DateUtil.endOfDay(new Date()).toString();
        
        Long count = this.count(new LambdaQueryWrapper<QuizRecord>()
                .eq(QuizRecord::getUserId, userId)
                .eq(QuizRecord::getQuestionId, questionId)
                .between(QuizRecord::getCreateTime, todayStart, todayEnd));
                
        if (count > 0) {
            throw new IllegalArgumentException("今天已经答过这道题咯~");
        }

        boolean isCorrect = qb.getCorrectAnswer().equalsIgnoreCase(answer);
        int baseReward = isCorrect ? qb.getRewardPoints() : 0;

        // 更新连续打卡天数
        User user = userMapper.selectById(userId);
        int consecutiveDays = updateConsecutiveDays(user);
        
        // 计算连续打卡奖励倍数
        double multiplier = calculateStreakMultiplier(consecutiveDays);
        int finalReward = (int) Math.ceil(baseReward * multiplier);
        int bonusPoints = finalReward - baseReward;

        // 记录打卡
        QuizRecord record = new QuizRecord();
        record.setUserId(userId);
        record.setQuestionId(questionId);
        record.setAnswer(answer);
        record.setIsCorrect(isCorrect ? 1 : 0);
        record.setScoreAwarded(finalReward);
        this.save(record);

        // 加分
        if (finalReward > 0) {
            pointService.changePoints(userId, finalReward, "QUIZ_REWARD", record.getId());
        }

        Map<String, Object> res = new HashMap<>();
        res.put("isCorrect", isCorrect);
        res.put("correctAnswer", qb.getCorrectAnswer());
        res.put("earnedPoints", finalReward);
        res.put("basePoints", baseReward);
        res.put("bonusPoints", bonusPoints);
        res.put("consecutiveDays", consecutiveDays);
        res.put("multiplier", multiplier);
        return res;
    }

    /**
     * 更新用户连续打卡天数
     * @param user 用户对象
     * @return 更新后的连续天数
     */
    private int updateConsecutiveDays(User user) {
        LocalDate today = LocalDate.now();
        LocalDate lastCheckIn = user.getLastCheckInDate() != null
            ? user.getLastCheckInDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
            : null;

        int consecutiveDays = user.getConsecutiveDays() != null ? user.getConsecutiveDays() : 0;

        if (lastCheckIn == null) {
            // 首次打卡
            consecutiveDays = 1;
        } else if (lastCheckIn.equals(today)) {
            // 今天已经打过卡，不增加天数
            return consecutiveDays;
        } else if (lastCheckIn.equals(today.minusDays(1))) {
            // 连续打卡
            consecutiveDays++;
        } else {
            // 中断了，重新开始
            consecutiveDays = 1;
        }

        // 更新数据库
        user.setConsecutiveDays(consecutiveDays);
        user.setLastCheckInDate(Date.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        userMapper.updateById(user);

        log.info("用户 {} 连续打卡天数更新为: {}", user.getId(), consecutiveDays);
        return consecutiveDays;
    }

    /**
     * 根据连续打卡天数计算奖励倍数
     * 3天: 1.2倍
     * 7天: 1.5倍
     * 15天: 2.0倍
     * 30天: 3.0倍
     */
    private double calculateStreakMultiplier(int consecutiveDays) {
        if (consecutiveDays >= 30) {
            return 3.0;
        } else if (consecutiveDays >= 15) {
            return 2.0;
        } else if (consecutiveDays >= 7) {
            return 1.5;
        } else if (consecutiveDays >= 3) {
            return 1.2;
        }
        return 1.0;
    }

    @Override
    public Map<String, Object> getCheckinStats(Integer days) {
        Map<String, Object> result = new HashMap<>();
        
        // 计算日期范围
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days - 1);
        
        // 获取日期范围内的所有打卡记录（按日期分组统计）
        List<Map<String, Object>> dailyStats = new java.util.ArrayList<>();
        
        for (int i = 0; i < days; i++) {
            LocalDate date = startDate.plusDays(i);
            Date dayStart = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
            Date dayEnd = Date.from(date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
            
            // 统计当天打卡的不同用户数（去重）
            Long count = this.baseMapper.selectCount(
                new LambdaQueryWrapper<QuizRecord>()
                    .between(QuizRecord::getCreateTime, dayStart, dayEnd)
            );
            // 注意: 实际上是统计打卡人次，如果需要统计人数，MyBatis Plus 不直接支持 count(distinct)
            // 这里我们先统计总人次，或者保持原有的 List 方式如果规模小。
            // 考虑到是演示项目，人次更直观展示活跃度。
            // 如果非要人数，可以用 selectCount + select(distinct userId)
            
            Map<String, Object> dayMap = new HashMap<>();
            dayMap.put("date", date.toString());
            dayMap.put("count", count);
            dailyStats.add(dayMap);
        }
        
        result.put("dailyStats", dailyStats);
        
        // 为兼容旧逻辑保留（可选，但目前 Dashboard.vue 改用 dailyStats 了）
        result.put("dates", dailyStats.stream().map(m -> m.get("date")).collect(Collectors.toList()));
        result.put("counts", dailyStats.stream().map(m -> m.get("count")).collect(Collectors.toList()));
        
        // 计算总打卡人次
        Date rangeStart = Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date rangeEnd = Date.from(endDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        Long totalCheckins = this.count(
            new LambdaQueryWrapper<QuizRecord>()
                .between(QuizRecord::getCreateTime, rangeStart, rangeEnd)
        );
        result.put("totalCheckins", totalCheckins);
        
        // 计算今日打卡人数
        Date todayStart = Date.from(endDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date todayEnd = Date.from(endDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        Long todayCheckins = this.baseMapper.selectList(
            new LambdaQueryWrapper<QuizRecord>()
                .select(QuizRecord::getUserId)
                .between(QuizRecord::getCreateTime, todayStart, todayEnd)
                .groupBy(QuizRecord::getUserId)
        ).stream().map(QuizRecord::getUserId).distinct().count();
        result.put("todayCheckins", todayCheckins);
        
        return result;
    }

    @Override
    public Map<String, Object> getAccuracyStats() {
        Map<String, Object> result = new HashMap<>();
        
        // 统计总答题数
        Long totalQuizzes = this.count();
        
        // 统计正确答题数
        Long correctQuizzes = this.count(
            new LambdaQueryWrapper<QuizRecord>()
                .eq(QuizRecord::getIsCorrect, 1)
        );
        
        // 计算正确率
        double accuracyRate = totalQuizzes > 0 ? (correctQuizzes * 100.0 / totalQuizzes) : 0.0;
        double overallAccuracy = totalQuizzes > 0 ? (correctQuizzes * 1.0 / totalQuizzes) : 0.0;
        
        result.put("totalQuizzes", totalQuizzes);
        result.put("correctQuizzes", correctQuizzes);
        result.put("wrongQuizzes", totalQuizzes - correctQuizzes);
        result.put("accuracyRate", Math.round(accuracyRate * 100.0) / 100.0);
        result.put("overallAccuracy", overallAccuracy); // 增加此字段以匹配前端 Dashboard.vue
        
        // 获取最近7天的正确率趋势
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(6);
        
        List<String> dates = new java.util.ArrayList<>();
        List<Double> rates = new java.util.ArrayList<>();
        
        for (int i = 0; i < 7; i++) {
            LocalDate date = startDate.plusDays(i);
            Date dayStart = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
            Date dayEnd = Date.from(date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
            
            Long dayTotal = this.count(
                new LambdaQueryWrapper<QuizRecord>()
                    .between(QuizRecord::getCreateTime, dayStart, dayEnd)
            );
            
            Long dayCorrect = this.count(
                new LambdaQueryWrapper<QuizRecord>()
                    .eq(QuizRecord::getIsCorrect, 1)
                    .between(QuizRecord::getCreateTime, dayStart, dayEnd)
            );
            
            double dayRate = dayTotal > 0 ? (dayCorrect * 100.0 / dayTotal) : 0.0;
            
            dates.add(date.toString());
            rates.add(Math.round(dayRate * 100.0) / 100.0);
        }
        
        result.put("trendDates", dates);
        result.put("trendRates", rates);
        
        return result;
    }
}
