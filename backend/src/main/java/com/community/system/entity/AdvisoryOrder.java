package com.community.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("advisory_order")
public class AdvisoryOrder {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long reportId;
    private Long targetUserId;
    private String violationType;
    private String locationDetail;
    private String photoUrl;
    private String advisoryContent;
    private Integer status;
    private Long handlerId;
    private String handleRemark;
    private Date createTime;
    private Date updateTime;
}
