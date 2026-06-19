package com.community.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.community.system.entity.PointAccountLog;
import com.community.system.entity.User;
import com.community.system.mapper.PointAccountLogMapper;
import com.community.system.service.PointService;
import com.community.system.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PointServiceImpl extends ServiceImpl<PointAccountLogMapper, PointAccountLog> implements PointService {

    private final UserService userService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changePoints(Long userId, Integer points, String actionType, Long refId) {
        if (points == null || points == 0) return;

        User user = userService.getById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }

        int afterBalance = user.getPointBalance() + points;
        if (afterBalance < 0) {
            throw new IllegalArgumentException("积分余额不足");
        }

        // 1. 更新用户表余额 (使用乐观锁/数据库自带安全校验更好，这里简化处理)
        boolean update = userService.update(new LambdaUpdateWrapper<User>()
                .setSql("point_balance = point_balance + " + points)
                .eq(User::getId, userId)
                .ge(points < 0, User::getPointBalance, Math.abs(points)));
                
        if (!update) {
            throw new RuntimeException("积分更新失败，可能余额不足");
        }

        // 2. 插入流水
        PointAccountLog log = new PointAccountLog();
        log.setUserId(userId);
        log.setActionType(actionType);
        log.setPoints(points);
        log.setBalanceAfter(afterBalance);
        log.setRefId(refId);
        this.save(log);
    }
}
