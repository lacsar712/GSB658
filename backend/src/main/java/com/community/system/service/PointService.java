package com.community.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.community.system.entity.PointAccountLog;

public interface PointService extends IService<PointAccountLog> {

    /**
     * 为用户增加或扣减积分
     * @param userId 用户ID
     * @param points 变动分数(正负)
     * @param actionType 变动类型(QUIZ_REWARD, EXCHANGE...)
     * @param refId 业务单据号(如答题记录ID或订单ID)
     */
    void changePoints(Long userId, Integer points, String actionType, Long refId);
    
}
