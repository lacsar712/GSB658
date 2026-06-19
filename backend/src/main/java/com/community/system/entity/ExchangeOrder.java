package com.community.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("exchange_order")
public class ExchangeOrder {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String orderNo;
    private Long userId;
    private Long productId;
    private String productName;
    private Integer pointCost;
    private Integer status; // 0待发货 1已发货 2已完成 3已取消
    private String logisticsCompany;
    private String logisticsNo;
    private String logisticsStatus;
    private String receiverName;
    private String receiverPhone;
    private String receiverAddress;
    private String remark;
    private Date createTime;
    private Date updateTime;
}
