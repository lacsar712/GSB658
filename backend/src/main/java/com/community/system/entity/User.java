package com.community.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("user")
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String nickname;
    private String phone;
    private Long communityId;
    private Integer pointBalance;
    private String role;
    private Integer status;
    private Integer consecutiveDays; // 连续打卡天数
    private Date lastCheckInDate; // 最后打卡日期
    private Date createTime;
    private Date updateTime;
}
