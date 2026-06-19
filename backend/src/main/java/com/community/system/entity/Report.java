package com.community.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("report")
public class Report {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String photoUrl;
    private String description;
    private BigDecimal longitude;
    private BigDecimal latitude;
    private String locationDetail;
    private Integer status;
    private String processRemark;
    private Date createTime;
    private Date updateTime;
}
