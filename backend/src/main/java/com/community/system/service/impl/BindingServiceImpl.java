package com.community.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.community.system.entity.CommunityBindingRequest;
import com.community.system.entity.User;
import com.community.system.mapper.CommunityBindingRequestMapper;
import com.community.system.service.BindingService;
import com.community.system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BindingServiceImpl extends ServiceImpl<CommunityBindingRequestMapper, CommunityBindingRequest> implements BindingService {

    private final UserService userService;

    @Override
    public void submitBindingRequest(CommunityBindingRequest request) {
        if (request.getUserId() == null || request.getCommunityId() == null) {
            throw new IllegalArgumentException("参数不全");
        }
        request.setStatus(0); // 待审核
        request.setCreateTime(new Date());
        this.save(request);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditBindingRequest(Long requestId, Integer status, String remark) {
        CommunityBindingRequest bindingRequest = this.getById(requestId);
        if (bindingRequest == null || bindingRequest.getStatus() != 0) {
            throw new IllegalArgumentException("申请不存在或已被处理");
        }

        bindingRequest.setStatus(status);
        bindingRequest.setAuditRemark(remark);
        bindingRequest.setUpdateTime(new Date());
        this.updateById(bindingRequest);

        // 如果审核通过，同步更新用户表的communityId
        if (status == 1) {
            User user = userService.getById(bindingRequest.getUserId());
            if (user != null) {
                user.setCommunityId(bindingRequest.getCommunityId());
                userService.updateById(user);
            }
        }
    }

    @Override
    public List<CommunityBindingRequest> getUserBindingRequests(Long userId) {
        return this.list(new LambdaQueryWrapper<CommunityBindingRequest>()
                .eq(CommunityBindingRequest::getUserId, userId)
                .orderByDesc(CommunityBindingRequest::getCreateTime));
    }

    @Override
    public List<CommunityBindingRequest> getPendingBindingRequests() {
        return this.list(new LambdaQueryWrapper<CommunityBindingRequest>()
                .eq(CommunityBindingRequest::getStatus, 0)
                .orderByAsc(CommunityBindingRequest::getCreateTime));
    }
}
