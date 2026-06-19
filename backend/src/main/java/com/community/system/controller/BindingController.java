package com.community.system.controller;

import com.community.system.entity.CommunityBindingRequest;
import com.community.system.service.BindingService;
import com.community.system.vo.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/binding")
@RequiredArgsConstructor
public class BindingController {

    private final BindingService bindingService;

    /**
     * 提交绑定申请
     */
    @PostMapping("/submit")
    public Result<?> submitRequest(@RequestBody CommunityBindingRequest req, HttpServletRequest request) {
        Long userId = Long.valueOf(request.getAttribute("userId").toString());
        req.setUserId(userId);
        bindingService.submitBindingRequest(req);
        return Result.success("申请已提交，请耐心等待社区管理员审核");
    }

    /**
     * 获取我的申请记录
     */
    @GetMapping("/my")
    public Result<List<CommunityBindingRequest>> getMyRequests(HttpServletRequest request) {
        Long userId = Long.valueOf(request.getAttribute("userId").toString());
        return Result.success(bindingService.getUserBindingRequests(userId));
    }

    /**
     * 管理员获取待审核列表
     */
    @GetMapping("/admin/pending")
    public Result<List<CommunityBindingRequest>> getPendingRequests(HttpServletRequest request) {
        String role = request.getAttribute("role").toString();
        if (!"ADMIN".equals(role)) {
            return Result.error(403, "无权访问");
        }
        return Result.success(bindingService.getPendingBindingRequests());
    }

    /**
     * 管理员审核申请
     */
    @PostMapping("/admin/audit")
    public Result<?> auditRequest(@RequestBody CommunityBindingRequest req, HttpServletRequest request) {
        String role = request.getAttribute("role").toString();
        if (!"ADMIN".equals(role)) {
            return Result.error(403, "无权访问");
        }
        bindingService.auditBindingRequest(req.getId(), req.getStatus(), req.getAuditRemark());
        return Result.success("审核成功");
    }
}
