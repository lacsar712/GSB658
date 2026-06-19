-- 为 user 表添加缺失字段
ALTER TABLE `user` ADD COLUMN IF NOT EXISTS `community_id` BIGINT COMMENT '绑定社区ID';
ALTER TABLE `user` ADD COLUMN IF NOT EXISTS `consecutive_days` INT DEFAULT 0 COMMENT '连续打卡天数';
ALTER TABLE `user` ADD COLUMN IF NOT EXISTS `last_check_in_date` DATETIME COMMENT '最后打卡日期';
