package com.community.system.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.community.system.entity.ExchangeOrder;
import com.community.system.entity.User;
import com.community.system.mapper.ExchangeOrderMapper;
import com.community.system.mapper.UserMapper;
import com.community.system.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl extends ServiceImpl<ExchangeOrderMapper, ExchangeOrder> implements OrderService {

    private final UserMapper userMapper;

    @Override
    public ExchangeOrder createOrder(Long userId, Long productId, String productName, Integer pointCost, String receiverName, String receiverPhone, String receiverAddress) {
        // 生成订单号
        String orderNo = "EX" + DateUtil.format(new Date(), "yyyyMMddHHmmss") + RandomUtil.randomNumbers(6);
        
        // 获取用户信息作为默认收货信息
        User user = userMapper.selectById(userId);
        
        ExchangeOrder order = new ExchangeOrder();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setProductId(productId);
        order.setProductName(productName);
        order.setPointCost(pointCost);
        order.setStatus(0); // 待发货
        order.setReceiverName(receiverName != null && !receiverName.isEmpty() ? receiverName : user.getNickname());
        order.setReceiverPhone(receiverPhone != null && !receiverPhone.isEmpty() ? receiverPhone : user.getPhone());
        order.setReceiverAddress(receiverAddress != null && !receiverAddress.isEmpty() ? receiverAddress : ("默认地址：" + (user.getCommunityId() != null ? "社区ID-" + user.getCommunityId() : "未绑定社区")));
        
        this.save(order);
        log.info("创建订单成功: {}", orderNo);
        
        // 模拟自动发货（实际应该由管理员操作）
        simulateShipment(order);
        
        return order;
    }

    @Override
    public List<ExchangeOrder> getUserOrders(Long userId) {
        return this.list(new LambdaQueryWrapper<ExchangeOrder>()
                .eq(ExchangeOrder::getUserId, userId)
                .orderByDesc(ExchangeOrder::getCreateTime));
    }

    @Override
    public ExchangeOrder getOrderDetail(Long orderId, Long userId) {
        ExchangeOrder order = this.getById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new IllegalArgumentException("订单不存在");
        }
        return order;
    }

    @Override
    public Map<String, Object> getLogisticsInfo(String logisticsNo) {
        if (logisticsNo == null) {
            return null;
        }
        
        // 模拟物流轨迹数据
        List<Map<String, Object>> traces = new ArrayList<>();
        
        traces.add(createTrace("2024-03-05 14:30:00", "【上海市】快件已被 菜鸟驿站 签收"));
        traces.add(createTrace("2024-03-05 09:15:00", "【上海市】快件已到达 菜鸟驿站，等待用户取件"));
        traces.add(createTrace("2024-03-05 06:20:00", "【上海市】快件正在派送中，快递员：张师傅，电话：138****1234"));
        traces.add(createTrace("2024-03-04 22:10:00", "【上海市】快件已到达 上海浦东转运中心"));
        traces.add(createTrace("2024-03-04 18:30:00", "【杭州市】快件已发出 杭州转运中心"));
        traces.add(createTrace("2024-03-04 15:00:00", "【杭州市】快件已揽收"));
        
        Map<String, Object> result = new HashMap<>();
        result.put("logisticsNo", logisticsNo);
        result.put("company", "顺丰速运");
        result.put("status", "已签收");
        result.put("traces", traces);
        
        return result;
    }

    /**
     * 模拟自动发货
     */
    private void simulateShipment(ExchangeOrder order) {
        // 生成物流单号
        String logisticsNo = "SF" + RandomUtil.randomNumbers(12);
        
        order.setStatus(1); // 已发货
        order.setLogisticsCompany("顺丰速运");
        order.setLogisticsNo(logisticsNo);
        order.setLogisticsStatus("运输中");
        
        this.updateById(order);
        log.info("订单 {} 已自动发货，物流单号: {}", order.getOrderNo(), logisticsNo);
    }

    private Map<String, Object> createTrace(String time, String desc) {
        Map<String, Object> trace = new HashMap<>();
        trace.put("time", time);
        trace.put("desc", desc);
        return trace;
    }
}
