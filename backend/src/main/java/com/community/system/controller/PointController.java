package com.community.system.controller;

import com.community.system.entity.ExchangeOrder;
import com.community.system.service.OrderService;
import com.community.system.service.PointService;
import com.community.system.service.UserService;
import com.community.system.vo.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/point")
@RequiredArgsConstructor
public class PointController {

    private final PointService pointService;
    private final UserService userService;
    private final OrderService orderService;

    /**
     * 积分兑换商品
     */
    @PostMapping("/exchange")
    public Result<?> exchangeProduct(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        Long userId = Long.valueOf(request.getAttribute("userId").toString());
        Integer productId = (Integer) params.get("productId");
        Integer pointCost = (Integer) params.get("pointCost");
        String productName = (String) params.get("productName");

        String receiverName = (String) params.get("receiverName");
        String receiverPhone = (String) params.get("receiverPhone");
        String receiverAddress = (String) params.get("receiverAddress");

        if (productId == null || pointCost == null || pointCost <= 0) {
            return Result.error(400, "参数错误");
        }

        try {
            // 创建订单
            ExchangeOrder order = orderService.createOrder(userId, productId.longValue(), productName, pointCost, receiverName, receiverPhone, receiverAddress);
            
            // 扣减积分（负数表示扣减）
            pointService.changePoints(userId, -pointCost, "EXCHANGE", order.getId());
            
            // 返回最新积分余额和订单信息
            Integer newBalance = userService.getUserPointBalance(userId);
            return Result.success(Map.of(
                "message", "兑换成功！工作人员将尽快联系您",
                "newBalance", newBalance,
                "orderId", order.getId(),
                "orderNo", order.getOrderNo()
            ));
        } catch (IllegalArgumentException e) {
            return Result.error(400, e.getMessage());
        } catch (Exception e) {
            return Result.error(500, "兑换失败，请稍后重试");
        }
    }

    /**
     * 获取当前用户积分余额
     */
    @GetMapping("/balance")
    public Result<?> getBalance(HttpServletRequest request) {
        Long userId = Long.valueOf(request.getAttribute("userId").toString());
        Integer balance = userService.getUserPointBalance(userId);
        return Result.success(Map.of("balance", balance));
    }

    /**
     * 获取用户订单列表
     */
    @GetMapping("/orders")
    public Result<?> getOrders(HttpServletRequest request) {
        Long userId = Long.valueOf(request.getAttribute("userId").toString());
        List<ExchangeOrder> orders = orderService.getUserOrders(userId);
        return Result.success(orders);
    }

    /**
     * 获取订单详情
     */
    @GetMapping("/orders/{orderId}")
    public Result<?> getOrderDetail(@PathVariable Long orderId, HttpServletRequest request) {
        Long userId = Long.valueOf(request.getAttribute("userId").toString());
        try {
            ExchangeOrder order = orderService.getOrderDetail(orderId, userId);
            return Result.success(order);
        } catch (IllegalArgumentException e) {
            return Result.error(404, e.getMessage());
        }
    }

    /**
     * 查询物流信息 (通过订单ID)
     */
    @GetMapping("/logistics/{orderId}")
    public Result<?> getLogistics(@PathVariable Long orderId, HttpServletRequest request) {
        Long userId = Long.valueOf(request.getAttribute("userId").toString());
        try {
            ExchangeOrder order = orderService.getOrderDetail(orderId, userId);
            if (order.getLogisticsNo() == null) {
                return Result.error(404, "该订单尚未发货，暂无物流信息");
            }
            Map<String, Object> logistics = orderService.getLogisticsInfo(order.getLogisticsNo());
            return Result.success(logistics);
        } catch (IllegalArgumentException e) {
            return Result.error(404, e.getMessage());
        }
    }
}
