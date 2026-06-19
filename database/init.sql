SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

CREATE DATABASE IF NOT EXISTS `community_system` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `community_system`;

-- 1. 用户表
CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `nickname` VARCHAR(64) DEFAULT NULL COMMENT '用户昵称',
    `phone` VARCHAR(20) NOT NULL COMMENT '手机号',
    `community_id` BIGINT DEFAULT NULL COMMENT '绑定的社区ID',
    `point_balance` INT NOT NULL DEFAULT 0 COMMENT '当前可用积分余额',
    `consecutive_days` INT NOT NULL DEFAULT 0 COMMENT '连续打卡天数',
    `last_check_in_date` DATE DEFAULT NULL COMMENT '最后打卡日期',
    `role` VARCHAR(20) NOT NULL DEFAULT 'USER' COMMENT '角色: USER居民, ADMIN管理员',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0禁用 1正常',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户基础信息表';

-- 2. 答题记录表
CREATE TABLE IF NOT EXISTS `quiz_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `question_id` BIGINT NOT NULL COMMENT '题目ID',
    `answer` VARCHAR(255) NOT NULL COMMENT '用户提交的答案',
    `is_correct` TINYINT NOT NULL COMMENT '是否正确: 0否 1是',
    `score_awarded` INT NOT NULL DEFAULT 0 COMMENT '此次获得积分',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '答题时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_time` (`user_id`, `create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='答题记录流水表';

-- 3. 积分账户明细流水表
CREATE TABLE IF NOT EXISTS `point_account_log` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `action_type` VARCHAR(32) NOT NULL COMMENT '操作类型: QUIZ_REWARD打卡奖励, REPORT_REWARD举报奖励, EXCHANGE兑换消耗',
    `points` INT NOT NULL COMMENT '变动积分数值(正表示加，负表示减)',
    `balance_after` INT NOT NULL COMMENT '变动后余额',
    `ref_id` BIGINT DEFAULT NULL COMMENT '关联的业务单据ID',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发生时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='积分流动台账表';

-- 4. 虚拟设备管理表
CREATE TABLE IF NOT EXISTS `virtual_device` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `device_no` VARCHAR(64) NOT NULL COMMENT '设备统一编号',
    `device_name` VARCHAR(128) NOT NULL COMMENT '设备名称(如: 北门智能垃圾桶)',
    `longitude` DECIMAL(10, 6) NOT NULL COMMENT '高德经度',
    `latitude` DECIMAL(10, 6) NOT NULL COMMENT '高德纬度',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态: 0正常 1预警 2满溢/故障',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_device_no` (`device_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='社区虚拟/实体设备分布表';

-- 5. 积分商城商品表
CREATE TABLE IF NOT EXISTS `product` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `name` VARCHAR(128) NOT NULL COMMENT '商品名称',
    `category` TINYINT NOT NULL COMMENT '分类: 1生活用品 2食品 3便民服务',
    `point_price` INT NOT NULL COMMENT '所需兑换积分单价',
    `stock` INT NOT NULL DEFAULT 0 COMMENT '剩余库存',
    `image_url` VARCHAR(255) DEFAULT NULL COMMENT '商品封面图片路径',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0下架 1上架',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='积分兑换商品表';

-- 6. 违规举报记录表
CREATE TABLE IF NOT EXISTS `report` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '提交人用户ID',
    `photo_url` LONGTEXT NOT NULL COMMENT '现场照片(Base64),存储路径(逗号分隔存多张)',
    `description` VARCHAR(1024) DEFAULT NULL COMMENT '居民填写的违规描述',
    `longitude` DECIMAL(10, 6) NOT NULL COMMENT '高德经度',
    `latitude` DECIMAL(10, 6) NOT NULL COMMENT '高德纬度',
    `location_detail` VARCHAR(255) DEFAULT NULL COMMENT '高德逆地理编码解析的中文地址',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态: 0待审核 1已处理(发积分) 2已驳回(不发分)',
    `process_remark` VARCHAR(512) DEFAULT NULL COMMENT '管理员审核时的处理批注',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '举证上报时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '状态更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='居民违规举证表';

-- 7. 题库表 (扩展单表设计)
CREATE TABLE IF NOT EXISTS `question_bank` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '题目ID',
    `content` VARCHAR(1024) NOT NULL COMMENT '题干内容',
    `options_json` JSON NOT NULL COMMENT '选项数据(例: [{"A": "是"}, {"B": "否"}])',
    `correct_answer` VARCHAR(10) NOT NULL COMMENT '标准答案标识(如 A、B、C 等)',
    `reward_points` INT NOT NULL DEFAULT 5 COMMENT '本题答对奖励基础积分',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='日常答题打卡关联的系统题库';

-- 8. 积分兑换订单表
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

-- 9. 社区绑定申请审核表
CREATE TABLE IF NOT EXISTS `community_binding_request` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `community_id` BIGINT NOT NULL COMMENT '申请绑定的社区ID',
    `community_name` VARCHAR(128) NOT NULL COMMENT '社区名称',
    `real_name` VARCHAR(64) NOT NULL COMMENT '居民真实姓名',
    `address_detail` VARCHAR(255) NOT NULL COMMENT '详细门牌号/住址',
    `prove_photo` LONGTEXT DEFAULT NULL COMMENT '房产证/租赁合同等证明照片(Base64)',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态: 0待审核 1已通过 2已驳回',
    `audit_remark` VARCHAR(512) DEFAULT NULL COMMENT '审核批注',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='社区绑定申请审核表';

-- 10. 劝导工单表
CREATE TABLE IF NOT EXISTS `advisory_order` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `report_id` BIGINT NOT NULL COMMENT '关联的举报记录ID',
    `target_user_id` BIGINT DEFAULT NULL COMMENT '被劝导的目标用户ID（如果能识别）',
    `violation_type` VARCHAR(64) NOT NULL COMMENT '违规类型',
    `location_detail` VARCHAR(255) NOT NULL COMMENT '违规地点',
    `photo_url` LONGTEXT DEFAULT NULL COMMENT '违规照片(Base64)',
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


-- ==================== 初始化种子数据 Seed ====================

-- 初始化管理员账号
INSERT INTO `user` (`phone`, `nickname`, `role`, `point_balance`) VALUES 
('13800138001', '超级管理员', 'ADMIN', 999),
('13800138000', '张三居民', 'USER', 150);

-- 初始化商品数据
INSERT INTO `product` (`name`, `category`, `point_price`, `stock`, `image_url`) VALUES 
('心相印抽纸一提', 1, 50, 100, '/images/product/tissue.png'),
('金龙鱼葵花籽油5L', 2, 500, 20, '/images/product/oil.png'),
('社区上门维修券(限1小时)', 3, 200, 50, '/images/product/coupon.png');

-- 初始化模拟设备数据 (以上海某个社区假定经纬度为例)
INSERT INTO `virtual_device` (`device_no`, `device_name`, `longitude`, `latitude`, `status`) VALUES 
('BIN-001', '东一门智能感应垃圾桶', 121.473701, 31.230416, 0),
('BIN-002', '西二门生活垃圾集中点', 121.475112, 31.231200, 1),
('BIN-003', '中央广场快递包装回收箱', 121.471239, 31.229155, 2);

-- 初始化题库
INSERT INTO `question_bank` (`content`, `options_json`, `correct_answer`, `reward_points`) VALUES 
('关于生活垃圾分类，废旧电池（含汞等有害物质）属于以下哪一类？', '[{"key": "A", "val": "干垃圾"}, {"key": "B", "val": "有害垃圾"}, {"key": "C", "val": "湿垃圾"}, {"key": "D", "val": "可回收物"}]', 'B', 5),
('在小区内遛狗时，如果不牵狗绳，属于什么行为？', '[{"key": "A", "val": "违规且不道德行为"}, {"key": "B", "val": "合理行为"}, {"key": "C", "val": "物业规定允许范围内"}, {"key": "D", "val": "没人管就行"}]', 'A', 5),
('本社区的物业服务时间通常是？', '[{"key": "A", "val": "朝九晚五"}, {"key": "B", "val": "仅工作日"}, {"key": "C", "val": "7x24小时全天候响应"}, {"key": "D", "val": "看心情"}]', 'C', 5);

-- 初始化模拟答题数据 (用于仪表盘可视化)
INSERT INTO `quiz_record` (`user_id`, `question_id`, `answer`, `is_correct`, `score_awarded`, `create_time`) VALUES 
(2, 1, 'B', 1, 5, DATE_SUB(NOW(), INTERVAL 6 DAY)),
(2, 2, 'A', 1, 5, DATE_SUB(NOW(), INTERVAL 5 DAY)),
(2, 3, 'C', 1, 5, DATE_SUB(NOW(), INTERVAL 4 DAY)),
(2, 1, 'A', 0, 0, DATE_SUB(NOW(), INTERVAL 3 DAY)),
(2, 2, 'A', 1, 5, DATE_SUB(NOW(), INTERVAL 2 DAY)),
(2, 3, 'C', 1, 5, DATE_SUB(NOW(), INTERVAL 1 DAY)),
(2, 1, 'B', 1, 5, NOW());

-- 初始化积分流水 (用于仪表盘可视化)
INSERT INTO `point_account_log` (`user_id`, `action_type`, `points`, `balance_after`, `ref_id`, `create_time`) VALUES 
(2, 'QUIZ_REWARD', 5, 5, 1, DATE_SUB(NOW(), INTERVAL 6 DAY)),
(2, 'QUIZ_REWARD', 5, 10, 2, DATE_SUB(NOW(), INTERVAL 5 DAY)),
(2, 'QUIZ_REWARD', 5, 15, 3, DATE_SUB(NOW(), INTERVAL 4 DAY)),
(2, 'QUIZ_REWARD', 5, 20, 5, DATE_SUB(NOW(), INTERVAL 2 DAY)),
(2, 'QUIZ_REWARD', 5, 25, 6, DATE_SUB(NOW(), INTERVAL 1 DAY)),
(2, 'QUIZ_REWARD', 5, 30, 7, NOW());

-- 初始化社区绑定申请
INSERT INTO `community_binding_request` (`user_id`, `real_name`, `community_id`, `community_name`, `address_detail`, `prove_photo`, `status`, `create_time`) VALUES 
(2, '张三', 1001, '碧桂园凤凰城', '碧桂园凤凰城 5 号楼 2 单元 1203', 'https://images.unsplash.com/photo-1560518883-ce09059eeffa?ixlib=rb-1.2.1&auto=format&fit=crop&w=150&q=80', 0, NOW());

-- 初始化违规举报
INSERT INTO `report` (`user_id`, `photo_url`, `description`, `longitude`, `latitude`, `location_detail`, `status`, `create_time`) VALUES 
(2, 'https://images.unsplash.com/photo-1530587191325-3db32d826c18?ixlib=rb-1.2.1&auto=format&fit=crop&w=150&q=80', '这里有人乱扔装修垃圾，阻碍了道路。', 113.8441, 22.6012, '碧桂园凤凰城南门通道', 0, NOW());
