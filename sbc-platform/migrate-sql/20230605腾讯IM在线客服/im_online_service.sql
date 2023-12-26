
-------------------------开放平台-------------------------
-- IM在线客服配置
CREATE TABLE `im_online_service` (
  `im_online_service_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '在线客服主键',
  `company_info_id` bigint(20) DEFAULT NULL COMMENT '店铺ID',
  `server_status` tinyint(1) DEFAULT NULL COMMENT '在线客服是否启用 0 不启用， 1 启用',
  `service_title` varchar(10) DEFAULT NULL COMMENT '客服标题',
  `effective_pc` tinyint(1) DEFAULT NULL COMMENT '生效终端pc 0 不生效 1 生效',
  `effective_app` tinyint(1) DEFAULT NULL COMMENT '生效终端App 0 不生效 1 生效',
  `effective_mobile` tinyint(1) DEFAULT NULL COMMENT '生效终端移动版 0 不生效 1 生效',
  `del_flag` tinyint(1) DEFAULT '0' COMMENT '删除标志 默认0：未删除 1：删除',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `operate_person` varchar(45) DEFAULT NULL COMMENT '操作人'
  PRIMARY KEY (`im_online_service_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8

-- 在线客服明细配置
CREATE TABLE `im_online_service_item` (
  `im_service_item_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '在线客服座席id',
  `company_info_id` bigint(20) DEFAULT NULL COMMENT '店铺ID',
  `im_online_service_id` int(11) NOT NULL COMMENT '在线客服主键',
  `customer_service_name` varchar(10) NOT NULL COMMENT '客服昵称',
  `customer_service_account` varchar(20) NOT NULL COMMENT '客服账号',
  `del_flag` tinyint(1) DEFAULT '0' COMMENT '删除标志 默认0：未删除 1：删除',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `operate_person` varchar(45) DEFAULT NULL COMMENT '操作人'
  PRIMARY KEY (`im_service_item_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8


CREATE TABLE `im_history` (
 `id` int(12) NOT NULL AUTO_INCREMENT,
 `from_account` varchar(255) DEFAULT NULL COMMENT '发送人',
 `to_account` varchar(255) DEFAULT NULL COMMENT '接受人',
 `msg_time` datetime DEFAULT NULL COMMENT '发送时间',
 `msg_seq` varchar(255) DEFAULT NULL COMMENT 'im 自带字段',
 `msg_random` varchar(255) DEFAULT NULL COMMENT 'im 自带字段',
                              PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='im历史信息';
DROP TABLE IF EXISTS `im_history_msg`;
CREATE TABLE `im_history_msg` (
`id` int(12) NOT NULL AUTO_INCREMENT,
`im_history_id` int(12) DEFAULT NULL COMMENT '历史Imd',
`msg_type` varchar(255) DEFAULT NULL COMMENT '信息类型',
`msg_content` text COMMENT 'im内容',
PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4  COMMENT='IM历史信息';
