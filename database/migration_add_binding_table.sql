-- 社区绑定申请表
CREATE TABLE IF NOT EXISTS `community_binding_request` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `community_id` BIGINT NOT NULL COMMENT '申请绑定的社区ID',
    `community_name` VARCHAR(128) NOT NULL COMMENT '社区名称',
    `real_name` VARCHAR(64) NOT NULL COMMENT '居民真实姓名',
    `address_detail` VARCHAR(255) NOT NULL COMMENT '详细门牌号/住址',
    `prove_photo` VARCHAR(512) DEFAULT NULL COMMENT '房产证/租赁合同等证明照片',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态: 0待审核 1已通过 2已驳回',
    `audit_remark` VARCHAR(512) DEFAULT NULL COMMENT '审核批注',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='社区绑定申请审核表';
