package com.community.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.community.system.entity.AdvisoryOrder;
import com.community.system.entity.Report;

import java.util.List;

public interface ReportService extends IService<Report> {
    
    /**
     * 居民提交违规图文与坐标
     */
    void submitReport(Report report);
    
    /**
     * 管理员处理违规并给居民发放积分奖励
     * @param reportId 举报ID
     * @param status 1通过发分, 2驳回不发分
     * @param remark 处理意见批注
     */
    void processReport(Long reportId, Integer status, String remark);

    /**
     * 获取举报列表（带分页和状态筛选）
     */
    Page<Report> listReports(Integer pageNum, Integer pageSize, Integer status);

    /**
     * 生成劝导工单
     */
    AdvisoryOrder generateAdvisoryOrder(Long reportId, String violationType, String advisoryContent, Long targetUserId);

    /**
     * 获取所有劝导工单
     */
    List<AdvisoryOrder> getAdvisoryOrders();
}
