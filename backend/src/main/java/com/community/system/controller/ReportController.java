package com.community.system.controller;

import com.community.system.entity.AdvisoryOrder;
import com.community.system.entity.Report;
import com.community.system.service.ReportService;
import com.community.system.vo.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/api/report")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @PostMapping("/submit")
    public Result<?> submitReport(@RequestBody Report report, HttpServletRequest request) {
        Long userId = Long.valueOf(request.getAttribute("userId").toString());
        report.setUserId(userId);
        
        reportService.submitReport(report);
        return Result.success("举报提交成功，工作人员会尽快核实处理");
    }

    /**
     * 获取举报列表（管理员）
     */
    @GetMapping("/list")
    public Result<?> listReports(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer status,
            HttpServletRequest request) {
        
        String role = request.getAttribute("role").toString();
        if (!"ADMIN".equals(role)) {
            return Result.error(403, "无权访问");
        }
        return Result.success(reportService.listReports(pageNum, pageSize, status));
    }

    // 给管理端的审核接口
    @PostMapping("/process")
    public Result<?> processReport(@RequestBody Report req, HttpServletRequest request) {
        String role = request.getAttribute("role").toString();
        if (!"ADMIN".equals(role)) {
            return Result.error(403, "无权操作");
        }
        reportService.processReport(req.getId(), req.getStatus(), req.getProcessRemark());
        return Result.success("处理成功");
    }

    /**
     * 生成劝导工单
     */
    @PostMapping("/generate-advisory")
    public Result<?> generateAdvisory(@RequestBody Map<String, Object> req, HttpServletRequest request) {
        String role = request.getAttribute("role").toString();
        if (!"ADMIN".equals(role)) {
            return Result.error(403, "无权操作");
        }
        
        Long reportId = Long.valueOf(req.get("reportId").toString());
        String violationType = req.get("violationType").toString();
        String advisoryContent = req.get("advisoryContent").toString();
        Long targetUserId = req.get("targetUserId") != null ? Long.valueOf(req.get("targetUserId").toString()) : null;
        
        AdvisoryOrder order = reportService.generateAdvisoryOrder(reportId, violationType, advisoryContent, targetUserId);
        return Result.success(order);
    }

    /**
     * 获取劝导工单列表
     */
    @GetMapping("/advisory-list")
    public Result<?> getAdvisoryList(HttpServletRequest request) {
        String role = request.getAttribute("role").toString();
        if (!"ADMIN".equals(role)) {
            return Result.error(403, "无权操作");
        }
        return Result.success(reportService.getAdvisoryOrders());
    }
}
