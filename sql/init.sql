SET NAMES utf8mb4;

CREATE DATABASE IF NOT EXISTS cold_chain DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE cold_chain;

DROP TABLE IF EXISTS `responsibility_attribution`;
DROP TABLE IF EXISTS `loss_record`;
DROP TABLE IF EXISTS `batch_flow`;
DROP TABLE IF EXISTS `batch`;
DROP TABLE IF EXISTS `cold_chain_node`;
DROP TABLE IF EXISTS `product`;
DROP TABLE IF EXISTS `sys_user`;

CREATE TABLE `sys_user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(100) NOT NULL COMMENT '密码',
    `real_name` VARCHAR(50) COMMENT '真实姓名',
    `phone` VARCHAR(20) COMMENT '手机号',
    `role` VARCHAR(20) NOT NULL DEFAULT 'operator' COMMENT '角色：admin-管理员，operator-操作员',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

CREATE TABLE `product` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `product_code` VARCHAR(50) NOT NULL COMMENT '产品编码',
    `product_name` VARCHAR(100) NOT NULL COMMENT '产品名称',
    `category` VARCHAR(50) COMMENT '产品分类',
    `unit` VARCHAR(20) NOT NULL DEFAULT 'kg' COMMENT '计量单位',
    `storage_temp` DECIMAL(5,2) COMMENT '最佳储存温度',
    `shelf_life` INT COMMENT '保质期（天）',
    `description` TEXT COMMENT '产品描述',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_product_code` (`product_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='产品表';

CREATE TABLE `cold_chain_node` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `node_code` VARCHAR(50) NOT NULL COMMENT '节点编码',
    `node_name` VARCHAR(100) NOT NULL COMMENT '节点名称',
    `node_type` VARCHAR(20) NOT NULL COMMENT '节点类型：supplier、warehouse、transit、store',
    `address` VARCHAR(255) COMMENT '节点地址',
    `manager` VARCHAR(50) COMMENT '负责人',
    `phone` VARCHAR(20) COMMENT '联系电话',
    `temperature` DECIMAL(5,2) COMMENT '当前温度',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-停用，1-正常',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_node_code` (`node_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='冷链节点表';

CREATE TABLE `batch` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `batch_no` VARCHAR(50) NOT NULL COMMENT '批次号',
    `product_id` BIGINT NOT NULL COMMENT '产品ID',
    `quantity` DECIMAL(10,2) NOT NULL COMMENT '初始数量',
    `remaining_quantity` DECIMAL(10,2) NOT NULL COMMENT '剩余数量',
    `total_loss` DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT '总损耗量',
    `loss_rate` DECIMAL(5,2) NOT NULL DEFAULT 0 COMMENT '损耗率(%)',
    `origin` VARCHAR(100) COMMENT '产地',
    `harvest_date` DATE COMMENT '采收日期',
    `expire_date` DATE COMMENT '到期日期',
    `status` VARCHAR(20) NOT NULL DEFAULT 'in_storage' COMMENT '状态：in_transit、in_storage、completed、closed',
    `remark` TEXT COMMENT '备注',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_batch_no` (`batch_no`),
    KEY `idx_product_id` (`product_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='批次表';

CREATE TABLE `batch_flow` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `batch_id` BIGINT NOT NULL COMMENT '批次ID',
    `from_node_id` BIGINT COMMENT '来源节点ID',
    `to_node_id` BIGINT NOT NULL COMMENT '目标节点ID',
    `flow_quantity` DECIMAL(10,2) NOT NULL COMMENT '流转数量',
    `operator_id` BIGINT NOT NULL COMMENT '操作人ID',
    `operate_time` DATETIME NOT NULL COMMENT '操作时间',
    `temperature` DECIMAL(5,2) COMMENT '运输温度',
    `transport_duration` INT COMMENT '运输时长（分钟）',
    `remark` TEXT COMMENT '备注',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_batch_id` (`batch_id`),
    KEY `idx_from_node_id` (`from_node_id`),
    KEY `idx_to_node_id` (`to_node_id`),
    KEY `idx_operate_time` (`operate_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='批次流转记录表';

CREATE TABLE `loss_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `batch_id` BIGINT NOT NULL COMMENT '批次ID',
    `flow_id` BIGINT COMMENT '流转记录ID',
    `node_id` BIGINT NOT NULL COMMENT '发生损耗的节点ID',
    `loss_quantity` DECIMAL(10,2) NOT NULL COMMENT '损耗数量',
    `loss_rate` DECIMAL(5,2) NOT NULL COMMENT '批次损耗率(%)',
    `loss_type` VARCHAR(50) NOT NULL COMMENT '损耗类型',
    `loss_reason` TEXT COMMENT '损耗原因描述',
    `discover_time` DATETIME NOT NULL COMMENT '发现时间',
    `operator_id` BIGINT NOT NULL COMMENT '记录人ID',
    `is_attributed` TINYINT NOT NULL DEFAULT 0 COMMENT '是否已归因：0-未归因，1-已归因',
    `status` VARCHAR(20) NOT NULL DEFAULT 'pending' COMMENT '状态：pending、processed、attributed',
    `remark` TEXT COMMENT '备注',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_batch_id` (`batch_id`),
    KEY `idx_node_id` (`node_id`),
    KEY `idx_discover_time` (`discover_time`),
    KEY `idx_is_attributed` (`is_attributed`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='损耗记录表';

CREATE TABLE `responsibility_attribution` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `loss_id` BIGINT NOT NULL COMMENT '损耗记录ID',
    `batch_id` BIGINT NOT NULL COMMENT '批次ID',
    `node_id` BIGINT NOT NULL COMMENT '责任节点ID',
    `responsible_party` VARCHAR(100) NOT NULL COMMENT '责任方',
    `responsibility_type` VARCHAR(50) NOT NULL COMMENT '责任类型',
    `responsibility_level` VARCHAR(20) NOT NULL COMMENT '责任等级：primary、secondary、none',
    `confidence` DECIMAL(5,2) NOT NULL COMMENT '归因置信度(%)',
    `analysis_basis` TEXT NOT NULL COMMENT '分析依据',
    `suggestion` TEXT COMMENT '整改建议',
    `analyst_id` BIGINT NOT NULL COMMENT '分析人ID',
    `analysis_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '分析时间',
    `status` VARCHAR(20) NOT NULL DEFAULT 'confirmed' COMMENT '状态：confirmed、appealed、revised',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_loss_id` (`loss_id`),
    KEY `idx_batch_id` (`batch_id`),
    KEY `idx_node_id` (`node_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='责任归因分析表';

INSERT INTO `sys_user` (`username`, `password`, `real_name`, `phone`, `role`, `status`) VALUES
('admin', '123456', '系统管理员', '13800138000', 'admin', 1),
('operator1', '123456', '张明', '13800138001', 'operator', 1),
('operator2', '123456', '李华', '13800138002', 'operator', 1);

INSERT INTO `product` (`product_code`, `product_name`, `category`, `unit`, `storage_temp`, `shelf_life`, `description`) VALUES
('PRD001', '有机西红柿', '蔬菜', 'kg', 4.00, 15, '山东基地直供有机西红柿'),
('PRD002', '红富士苹果', '水果', 'kg', 0.00, 60, '优质红富士苹果'),
('PRD003', '冷鲜猪肉', '肉类', 'kg', -2.00, 7, '合格冷鲜猪肉'),
('PRD004', '三文鱼', '水产', 'kg', -18.00, 180, '进口冷冻三文鱼'),
('PRD005', '生菜', '蔬菜', 'kg', 2.00, 7, '新鲜生菜');

INSERT INTO `cold_chain_node` (`node_code`, `node_name`, `node_type`, `address`, `manager`, `phone`, `temperature`, `status`) VALUES
('NOD001', '山东蔬菜基地', 'supplier', '山东省寿光市蔬菜产业园', '王强', '13900139001', 15.00, 1),
('NOD002', '烟台苹果产区', 'supplier', '山东省烟台市苹果种植基地', '李军', '13900139002', 12.00, 1),
('NOD003', '北京通州中心仓', 'warehouse', '北京市通州区冷链物流园', '张伟', '13900139003', 2.00, 1),
('NOD004', '上海浦东分仓', 'warehouse', '上海市浦东新区冷链仓储中心', '刘洋', '13900139004', 3.00, 1),
('NOD005', '北京运输中转站', 'transit', '北京市大兴区物流中转站', '赵磊', '13900139005', 5.00, 1),
('NOD006', '北京朝阳门店', 'store', '北京市朝阳区建国路88号', '陈晨', '13900139006', 4.00, 1),
('NOD007', '北京海淀门店', 'store', '北京市海淀区中关村大街1号', '孙超', '13900139007', 4.00, 1);

INSERT INTO `batch` (`batch_no`, `product_id`, `quantity`, `remaining_quantity`, `total_loss`, `loss_rate`, `origin`, `harvest_date`, `expire_date`, `status`, `remark`) VALUES
('B20240101001', 1, 1000.00, 920.00, 80.00, 8.00, '山东寿光', '2024-01-01', '2024-01-16', 'in_storage', '第一批有机西红柿'),
('B20240102001', 2, 2000.00, 1950.00, 50.00, 2.50, '山东烟台', '2024-01-02', '2024-03-02', 'in_transit', '苹果春节备货批次'),
('B20240103001', 3, 500.00, 480.00, 20.00, 4.00, '北京大兴', '2024-01-03', '2024-01-10', 'completed', '冷鲜猪肉批次'),
('B20240105001', 5, 300.00, 285.00, 15.00, 5.00, '河北蔬菜基地', '2024-01-05', '2024-01-12', 'in_storage', '新鲜生菜批次');

INSERT INTO `batch_flow` (`batch_id`, `from_node_id`, `to_node_id`, `flow_quantity`, `operator_id`, `operate_time`, `temperature`, `transport_duration`, `remark`) VALUES
(1, 1, 5, 1000.00, 2, '2024-01-01 08:00:00', 6.00, 360, '从山东基地运输至北京中转站'),
(1, 5, 3, 950.00, 2, '2024-01-02 10:00:00', 5.00, 120, '从中转站运输至中心仓'),
(1, 3, 6, 300.00, 3, '2024-01-05 09:00:00', 4.00, 60, '配送至朝阳门店'),
(1, 3, 7, 200.00, 3, '2024-01-06 14:00:00', 4.00, 90, '配送至海淀门店'),
(2, 2, 5, 2000.00, 2, '2024-01-02 07:00:00', 2.00, 420, '苹果从烟台运输至北京'),
(3, NULL, 3, 500.00, 2, '2024-01-03 06:00:00', -1.00, 30, '猪肉直接入库'),
(3, 3, 6, 250.00, 3, '2024-01-04 08:00:00', -2.00, 45, '猪肉配送到朝阳门店'),
(4, 1, 3, 300.00, 2, '2024-01-05 07:30:00', 3.00, 300, '生菜从基地运输至中心仓');

INSERT INTO `loss_record` (`batch_id`, `flow_id`, `node_id`, `loss_quantity`, `loss_rate`, `loss_type`, `loss_reason`, `discover_time`, `operator_id`, `is_attributed`, `status`) VALUES
(1, 2, 5, 50.00, 5.00, '变质', '运输途中温度偏高，导致部分西红柿变质', '2024-01-02 10:30:00', 2, 1, 'attributed'),
(1, NULL, 3, 30.00, 3.00, '损坏', '仓储期间堆压不当，导致部分西红柿损坏', '2024-01-04 15:00:00', 3, 1, 'attributed'),
(2, 5, 5, 50.00, 2.50, '损坏', '装卸过程中操作不当，导致苹果碰伤', '2024-01-02 18:00:00', 2, 0, 'pending'),
(3, NULL, 3, 20.00, 4.00, '变质', '冷库温度波动导致部分猪肉变质', '2024-01-05 08:00:00', 2, 1, 'attributed'),
(4, 8, 3, 15.00, 5.00, '变质', '运输时间过长，生菜叶片发黄', '2024-01-05 14:00:00', 2, 0, 'processed');

INSERT INTO `responsibility_attribution` (`loss_id`, `batch_id`, `node_id`, `responsible_party`, `responsibility_type`, `responsibility_level`, `confidence`, `analysis_basis`, `suggestion`, `analyst_id`, `status`) VALUES
(1, 1, 5, '运输部门', '温度控制不当', 'primary', 85.00, '运输温度记录显示途中温度达到6℃，超过西红柿最佳储存温度，持续时间较长', '加强运输途中温度监控，安装温度报警装置', 1, 'confirmed'),
(2, 1, 3, '仓储部门', '操作失误', 'primary', 90.00, '仓储堆码过高，超出规范层数，底层西红柿受压损坏', '加强员工培训，规范堆码操作，设置高度标识', 1, 'confirmed'),
(4, 3, 3, '设备运维部门', '温度控制不当', 'primary', 80.00, '冷库温度日志显示夜间温度升高至5℃，持续2小时', '定期维护制冷设备，安装备用机组并增加报警机制', 1, 'confirmed');
