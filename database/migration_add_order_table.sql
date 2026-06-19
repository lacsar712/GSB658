-- 创建积分兑换订单表
CREATE TABLE IF NOT EXISTS `exchange_order` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '订单ID',
    `order_no` VARCHAR(64) NOT NULL COMMENT '订单编号',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `product_id` BIGINT NOT NULL COMMENT '商品ID',
    `product_name` VARCHAR(128) NOT NULL COMMENT '商品名称',
    `point_cost` INT NOT NULL COMMENT '消耗积分',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '订单状态: 0待发货 1已发货 2已完成 3已取消',
    `logistics_company` VARCHAR(64) DEFAULT NULL COMMENT '物流公司',
    `logistics_no` VARCHAR(128) DEFAULT NULL COMMENT '物流单号',
    `logistics_status` VARCHAR(32) DEFAULT NULL COMMENT '物流状态',
    `receiver_name` VARCHAR(64) DEFAULT NULL COMMENT '收货人姓名',
    `receiver_phone` VARCHAR(20) DEFAULT NULL COMMENT '收货人电话',
    `receiver_address` VARCHAR(512) DEFAULT NULL COMMENT '收货地址',
    `remark` VARCHAR(512) DEFAULT NULL COMMENT '订单备注',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '下单时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='积分兑换订单表';
