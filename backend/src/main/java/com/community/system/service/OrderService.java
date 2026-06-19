package com.community.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.community.system.entity.ExchangeOrder;

import java.util.List;
import java.util.Map;

public interface OrderService extends IService<ExchangeOrder> {
    
    /**
     * 创建兑换订单
     */
    ExchangeOrder createOrder(Long userId, Long productId, String productName, Integer pointCost, String receiverName, String receiverPhone, String receiverAddress);
    
    /**
     * 获取用户订单列表
     */
    List<ExchangeOrder> getUserOrders(Long userId);
    
    /**
     * 获取订单详情
     */
    ExchangeOrder getOrderDetail(Long orderId, Long userId);
    
    /**
     * 模拟物流信息查询
     */
    Map<String, Object> getLogisticsInfo(String logisticsNo);
}
