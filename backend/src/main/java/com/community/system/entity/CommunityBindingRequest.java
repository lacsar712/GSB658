package com.community.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("community_binding_request")
public class CommunityBindingRequest {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long communityId;
    private String communityName;
    private String realName;
    private String addressDetail;
    private String provePhoto;
    private Integer status; // 0待审核 1已通过 2已驳回
    private String auditRemark;
    private Date createTime;
    private Date updateTime;
}
