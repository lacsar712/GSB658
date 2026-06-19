-- 为user表添加连续打卡相关字段
ALTER TABLE `user` 
ADD COLUMN `consecutive_days` INT NOT NULL DEFAULT 0 COMMENT '连续打卡天数' AFTER `point_balance`,
ADD COLUMN `last_check_in_date` DATE DEFAULT NULL COMMENT '最后打卡日期' AFTER `consecutive_days`;
