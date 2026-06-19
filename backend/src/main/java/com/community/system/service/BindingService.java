package com.community.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.community.system.entity.CommunityBindingRequest;
import java.util.List;

public interface BindingService extends IService<CommunityBindingRequest> {
    
    /**
     * 提交绑定申请
     */
    void submitBindingRequest(CommunityBindingRequest request);

    /**
     * 审核绑定申请
     */
    void auditBindingRequest(Long requestId, Integer status, String remark);
    
    /**
     * 获取用户本人的绑定申请（带状态）
     */
    List<CommunityBindingRequest> getUserBindingRequests(Long userId);

    /**
     * 管理端获取待审核列表
     */
    List<CommunityBindingRequest> getPendingBindingRequests();
}
