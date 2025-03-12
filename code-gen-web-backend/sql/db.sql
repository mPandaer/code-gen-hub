
use my_db;

-- 订单表
CREATE TABLE `order` (
                         `order_id` VARCHAR(32) PRIMARY KEY COMMENT '订单号（唯一主键，格式如：ORDER_20240517123456）',
                         `user_id` BIGINT NOT NULL COMMENT '用户ID',
                         `generator_id` BIGINT NOT NULL COMMENT '代码生成器ID',
                         `amount` DECIMAL(10,2) NOT NULL COMMENT '订单金额（单位：元）',
                         `order_status` TINYINT NOT NULL DEFAULT 0 COMMENT '订单状态：0-待支付，1-已支付，2-已取消，3-支付失败',
                         `payment_method` VARCHAR(16) COMMENT '支付方式（如：支付宝/微信）',
                         `payment_no` VARCHAR(64) COMMENT '第三方支付流水号',
                         `out_trade_no` VARCHAR(32) UNIQUE COMMENT '商户订单号（与order_id可合并，但保留方便对账）',
                         `create_time` DATETIME NOT NULL COMMENT '创建时间',
                         `pay_time` DATETIME COMMENT '支付时间',
                         `expire_time` DATETIME COMMENT '订单过期时间（如30分钟未支付自动关闭）',
                         `remark` VARCHAR(255) COMMENT '备注信息'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- 代码生成器付费信息表

CREATE TABLE `generator_fee` (
                                 `generator_id` BIGINT PRIMARY KEY COMMENT '代码生成器ID（外键关联generator表）',
                                 `price` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '价格（单位：元）',
                                 `is_free` TINYINT NOT NULL DEFAULT 0 COMMENT '是否免费：0-否，1-是',
                                 `validity` VARCHAR(32) COMMENT '有效期（如：永久/30天）',
                                 `create_time` DATETIME NOT NULL COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;



 -- 代码生成器与用户的关系表

CREATE TABLE `user_generator` (
                                  `user_id` BIGINT NOT NULL COMMENT '用户ID',
                                  `generator_id` BIGINT NOT NULL COMMENT '代码生成器ID',
                                  `purchase_time` DATETIME NOT NULL COMMENT '购买时间',
                                  `expire_time` DATETIME COMMENT '权限过期时间（如永久则为NULL）',
                                  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1-有效，2-已过期，3-已退款',
                                  PRIMARY KEY (`user_id`, `generator_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
