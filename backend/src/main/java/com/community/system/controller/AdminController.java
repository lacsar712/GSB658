package com.community.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.community.system.entity.PointAccountLog;
import com.community.system.entity.User;
import com.community.system.service.PointService;
import com.community.system.service.UserService;
import com.community.system.vo.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final PointService pointService;

    /**
     * 获取居民列表（管理员）
     */
    @GetMapping("/users")
    public Result<?> getUsers(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            HttpServletRequest request) {
        
        String role = request.getAttribute("role").toString();
        if (!"ADMIN".equals(role)) {
            return Result.error(403, "无权访问");
        }

        Page<User> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<User> query = new LambdaQueryWrapper<User>()
                .like(keyword != null, User::getNickname, keyword)
                .or()
                .like(keyword != null, User::getPhone, keyword)
                .orderByDesc(User::getCreateTime);

        Page<User> result = userService.page(page, query);
        return Result.success(result);
    }

    /**
     * 获取居民积分分布统计 (用于仪表盘小卡片)
     */
    @GetMapping("/stats/points")
    public Result<?> getPointStats(HttpServletRequest request) {
        String role = request.getAttribute("role").toString();
        if (!"ADMIN".equals(role)) {
            return Result.error(403, "无权访问");
        }

        long totalUsers = userService.count();
        int totalPoints = userService.list().stream().mapToInt(u -> u.getPointBalance() != null ? u.getPointBalance() : 0).sum();
        
        // 模拟一些趋势数据，实际应从历史表计算
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", totalUsers);
        stats.put("totalPoints", totalPoints);
        stats.put("userTrend", 12.5);
        stats.put("pointTrend", 8.2);
        
        return Result.success(stats);
    }
}
