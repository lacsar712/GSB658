package com.community.system.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.community.system.entity.AdvisoryOrder;
import com.community.system.entity.Report;
import com.community.system.mapper.AdvisoryOrderMapper;
import com.community.system.mapper.ReportMapper;
import com.community.system.service.PointService;
import com.community.system.service.ReportService;
import com.community.system.service.ThirdPartyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportServiceImpl extends ServiceImpl<ReportMapper, Report> implements ReportService {

    private final ThirdPartyService thirdPartyService;
    private final PointService pointService;
    private final AdvisoryOrderMapper advisoryOrderMapper;

    @Override
    public void submitReport(Report report) {
        if (StrUtil.isBlank(report.getPhotoUrl())) {
            throw new IllegalArgumentException("举报图片不能为空");
        }

        // 每天最多举报 3 次防止恶意刷分机制
        String todayStart = DateUtil.beginOfDay(new Date()).toString();
        String todayEnd = DateUtil.endOfDay(new Date()).toString();
        
        Long count = this.count(new LambdaQueryWrapper<Report>()
                .eq(Report::getUserId, report.getUserId())
                .between(Report::getCreateTime, todayStart, todayEnd));
                
        if (count >= 3) {
            throw new IllegalArgumentException("今天举报次数已达上限，感谢您的热心参与");
        }

        // 如果提供了经纬度，调用高德获取中文地址
        if (report.getLongitude() != null && report.getLatitude() != null) {
            try {
                String address = thirdPartyService.amapRegeo(
                        report.getLongitude().toString(),
                        report.getLatitude().toString()
                );
                if (StrUtil.isNotBlank(address)) {
                    report.setLocationDetail(address);
                }
            } catch (Exception e) {
                log.warn("获取地址信息失败，使用用户提供的位置信息", e);
            }
        }
        
        // 如果没有提供locationDetail，设置默认值
        if (StrUtil.isBlank(report.getLocationDetail())) {
            report.setLocationDetail("位置信息未提供");
        }
        
        report.setStatus(0); // 待审核
        report.setCreateTime(new Date());
        this.save(report);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void processReport(Long reportId, Integer status, String remark) {
        Report report = this.getById(reportId);
        if (report == null || report.getStatus() != 0) {
            throw new IllegalArgumentException("举报不存在或已被处理");
        }

        report.setStatus(status);
        report.setProcessRemark(remark);
        this.updateById(report);

        // 如果是审核通过（例如status=1），则发放10个积分奖励
        if (status == 1) {
            pointService.changePoints(report.getUserId(), 10, "REPORT_REWARD", report.getId());
        }
    }

    @Override
    public Page<Report> listReports(Integer pageNum, Integer pageSize, Integer status) {
        Page<Report> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Report> query = new LambdaQueryWrapper<Report>()
                .eq(status != null, Report::getStatus, status)
                .orderByDesc(Report::getCreateTime);
        return this.page(page, query);
    }

    @Override
    public AdvisoryOrder generateAdvisoryOrder(Long reportId, String violationType, String advisoryContent, Long targetUserId) {
        Report report = this.getById(reportId);
        if (report == null) {
            throw new IllegalArgumentException("关联的举报不存在");
        }

        AdvisoryOrder order = new AdvisoryOrder();
        order.setReportId(reportId);
        order.setTargetUserId(targetUserId);
        order.setViolationType(violationType);
        order.setLocationDetail(report.getLocationDetail());
        order.setPhotoUrl(report.getPhotoUrl());
        order.setAdvisoryContent(advisoryContent);
        order.setStatus(0); // 待处理
        order.setCreateTime(new Date());
        
        advisoryOrderMapper.insert(order);
        return order;
    }

    @Override
    public List<AdvisoryOrder> getAdvisoryOrders() {
        return advisoryOrderMapper.selectList(new LambdaQueryWrapper<AdvisoryOrder>()
                .orderByDesc(AdvisoryOrder::getCreateTime));
    }
}
