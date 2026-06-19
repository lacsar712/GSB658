package com.community.system.controller;

import com.community.system.entity.QuestionBank;
import com.community.system.service.QuizService;
import com.community.system.vo.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/quiz")
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;

    @GetMapping("/today")
    public Result<List<QuestionBank>> getTodayQuiz(HttpServletRequest request) {
        Long userId = Long.valueOf(request.getAttribute("userId").toString());
        List<QuestionBank> quizzes = quizService.getTodayQuiz(userId);
        return Result.success(quizzes);
    }

    @PostMapping("/submit")
    public Result<Map<String, Object>> submitAnswer(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        Long userId = Long.valueOf(request.getAttribute("userId").toString());
        Long questionId = Long.valueOf(body.get("questionId").toString());
        String answer = body.get("answer").toString();
        
        Map<String, Object> res = quizService.submitAnswer(userId, questionId, answer);
        return Result.success(res);
    }

    /**
     * 获取打卡人次统计（管理员）
     * @param days 统计天数，默认7天
     */
    @GetMapping("/stats/checkin")
    public Result<Map<String, Object>> getCheckinStats(@RequestParam(defaultValue = "7") Integer days, HttpServletRequest request) {
        String role = request.getAttribute("role").toString();
        if (!"ADMIN".equals(role)) {
            return Result.error(403, "无权操作");
        }
        Map<String, Object> stats = quizService.getCheckinStats(days);
        return Result.success(stats);
    }

    /**
     * 获取答题正确率统计（管理员）
     */
    @GetMapping("/stats/accuracy")
    public Result<Map<String, Object>> getAccuracyStats(HttpServletRequest request) {
        String role = request.getAttribute("role").toString();
        if (!"ADMIN".equals(role)) {
            return Result.error(403, "无权操作");
        }
        Map<String, Object> stats = quizService.getAccuracyStats();
        return Result.success(stats);
    }
}
