package com.community.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("point_account_log")
public class PointAccountLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String actionType;
    private Integer points;
    private Integer balanceAfter;
    private Long refId;
    private Date createTime;
}
