-- 劝导工单表
CREATE TABLE IF NOT EXISTS `advisory_order` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `report_id` BIGINT NOT NULL COMMENT '关联的举报记录ID',
    `target_user_id` BIGINT DEFAULT NULL COMMENT '被劝导的目标用户ID（如果能识别）',
    `violation_type` VARCHAR(64) NOT NULL COMMENT '违规类型',
    `location_detail` VARCHAR(255) NOT NULL COMMENT '违规地点',
    `photo_url` VARCHAR(512) DEFAULT NULL COMMENT '违规照片',
    `advisory_content` TEXT NOT NULL COMMENT '劝导内容/通知内容',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态: 0待处理 1已发送 2已完成 3已关闭',
    `handler_id` BIGINT DEFAULT NULL COMMENT '处理人ID',
    `handle_remark` VARCHAR(512) DEFAULT NULL COMMENT '处理备注',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_report_id` (`report_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='劝导工单表';
