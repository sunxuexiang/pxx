
-- ***************************************** account begin *********************************************
-- ***************************************** account end ***********************************************


-- ***************************************** order begin ***********************************************
CREATE TABLE `history_logistics_company` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `customer_id` varchar(32) DEFAULT NULL COMMENT '会员id',
  `order_id` varchar(32) DEFAULT NULL COMMENT '订单id',
  `logistics_name` varchar(100) DEFAULT NULL COMMENT '物流公司名称',
  `logistics_phone` varchar(20) DEFAULT NULL COMMENT '物流公司电话',
  `logistics_address` varchar(200) NOT NULL COMMENT '物流公司地址',
  `receiving_site` varchar(200) DEFAULT NULL COMMENT '收货站点',
  `company_id` int(11) DEFAULT NULL,
  `del_flag` tinyint(2) DEFAULT '0' COMMENT '删除标志',
  `self_flag` tinyint(2) DEFAULT NULL COMMENT '是否是客户自建物流：0：不是1：是',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='物流公司历史记录表';


-- ***************************************** order end ***********************************************



-- ***************************************** marketing begin ***********************************************

-- ***************************************** marketing end ***********************************************




-- ***************************************** goods begin *********************************************

-- 修改步长值类型
alter table goods_info modify column add_step decimal(20,2) DEFAULT NULL COMMENT '增加的步长';

alter table standard_sku modify column add_step decimal(20,2) DEFAULT NULL COMMENT '增加的步长';

 -- 修改单品运费模板快递运送类型
alter table freight_template_goods_express modify column freight_start_num decimal(10, 3) NOT NULL DEFAULT '1.000' COMMENT '首件/重/体积' ;
alter table freight_template_goods_express modify column freight_plus_num decimal(10, 3) NOT NULL DEFAULT '1.000' COMMENT '续件/重/体积' ;


-- ***************************************** goods end ***********************************************



-- ***************************************** setting begin *********************************************
-- 物流公司表
CREATE TABLE `logistics_company` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `company_number` varchar(20) NOT NULL COMMENT '物流公司编号',
  `logistics_name` varchar(100) NOT NULL COMMENT '物流公司名称',
  `logistics_phone` varchar(20) NOT NULL COMMENT '物流公司电话',
  `logistics_address` varchar(200) NOT NULL COMMENT '物流公司地址',
  `del_flag` tinyint(2) DEFAULT '0' COMMENT '删除标志',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `create_person` varchar(32) DEFAULT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `update_person` varchar(32) DEFAULT NULL COMMENT '修改人',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8 COMMENT='物流公司';

-- 更新物流公司为快递公司
UPDATE `sbc-setting`.`menu_info` SET `system_type_cd` = 4, `parent_menu_id` = 'fc8e041c3fe311e9828800163e0fc468', `menu_grade` = 3, `menu_name` = '快递公司设置', `menu_url` = '/logistics-manage', `menu_icon` = '', `sort` = 150, `create_time` = '2018-01-03 10:36:38', `del_flag` = 0 WHERE `menu_id` = 'fc8e04723fe311e9828800163e0fc468';
-- 新增物流公司菜单
INSERT INTO `sbc-setting`.`menu_info`(`menu_id`, `system_type_cd`, `parent_menu_id`, `menu_grade`, `menu_name`, `menu_url`, `menu_icon`, `sort`, `create_time`, `del_flag`) VALUES ('ff8080817599cbb801759c6023630001', 4, 'fc8e041c3fe311e9828800163e0fc468', 3, '物流公司设置', '/logistics-company', NULL, 151, '2020-11-06 15:05:48', 0);


--智齿客服配置sql
INSERT INTO `sbc-setting`.`system_config` (`config_key`, `config_type`, `config_name`, `remark`, `status`, `context`, `create_time`, `update_time`, `del_flag`) VALUES ( 'online_service', 'sobot_online_service', '智齿客服', NULL, '0', '{\"appId\":\"654321\",\"appKey\":\"123456\",\"effectiveApp\":1,\"effectiveMiniProgram\":0,\"effectivePc\":0,\"h5Url\":\"sobot.com/chat/h5/v2/index.html?sysnum=419746b240784f359a62dcd50fee8227&channelid=5\"}',now(), now(), '0');



-- 供应商订单取消
INSERT INTO `sbc-setting`.`function_info`(`function_id`, `system_type_cd`, `menu_id`, `function_title`, `function_name`, `remark`, `sort`, `create_time`, `del_flag`) VALUES ('ff80808176396f8e01763bff700c0000', 3, 'fc8dfdfd3fe311e9828800163e0fc468', '订单取消', 'f_order_cancel', NULL, 27, '2020-12-07 14:59:25', 0);
INSERT INTO `sbc-setting`.`authority`(`authority_id`, `system_type_cd`, `function_id`, `authority_title`, `authority_name`, `authority_url`, `request_type`, `remark`, `sort`, `create_time`, `del_flag`) VALUES ('ff80808176396f8e01763c0144990001', 3, 'ff80808176396f8e01763bff700c0000', '订单取消', NULL, '/trade/cancel/*', 'GET', NULL, 1, '2020-12-07 15:01:25', 0);




CREATE TABLE `banner_admin` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `banner_name` varchar(40) NOT NULL COMMENT '名称',
  `one_cate_id` bigint(20) DEFAULT NULL COMMENT '一级分类ID',
  `one_cate_name` varchar(20) DEFAULT NULL COMMENT '一级分类名称',
  `banner_sort` int(5) DEFAULT NULL COMMENT '排序号',
  `link` varchar(256) DEFAULT NULL COMMENT '添加链接',
  `banner_img` text COMMENT 'banner图片',
  `is_show` tinyint(2) DEFAULT '0' COMMENT '状态(0.显示 1.隐藏)',
  `del_flag` tinyint(2) DEFAULT '0' COMMENT '删除标志',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `create_person` varchar(32) DEFAULT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `update_person` varchar(32) DEFAULT NULL COMMENT '修改人',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8 COMMENT='轮播管理';

-- 轮播管理权限相关
INSERT INTO `sbc-setting`.`menu_info`(`menu_id`, `system_type_cd`, `parent_menu_id`, `menu_grade`, `menu_name`, `menu_url`, `menu_icon`, `sort`, `create_time`, `del_flag`) VALUES ('ff808081763e95e4017640eb44310000', 4, 'fc8e1ac93fe311e9828800163e0fc468', 3, '轮播管理', '/banner-admin', NULL, 21, '2020-12-08 13:55:29', 0);

INSERT INTO `sbc-setting`.`function_info`(`function_id`, `system_type_cd`, `menu_id`, `function_title`, `function_name`, `remark`, `sort`, `create_time`, `del_flag`) VALUES ('ff808081763e95e4017640f32c5a0001', 4, 'ff808081763e95e4017640eb44310000', '轮播管理', 'f_banner_admin', NULL, 1, '2020-12-08 14:04:07', 0);

INSERT INTO `sbc-setting`.`authority`(`authority_id`, `system_type_cd`, `function_id`, `authority_title`, `authority_name`, `authority_url`, `request_type`, `remark`, `sort`, `create_time`, `del_flag`) VALUES ('ff8080817648e2e001764b6be02d0007', 4, 'ff808081763e95e4017640f32c5a0001', '根据id删除轮播管理', NULL, '/banneradmin/*', 'DELETE', NULL, 6, '2020-12-10 14:52:10', 0);
INSERT INTO `sbc-setting`.`authority`(`authority_id`, `system_type_cd`, `function_id`, `authority_title`, `authority_name`, `authority_url`, `request_type`, `remark`, `sort`, `create_time`, `del_flag`) VALUES ('ff8080817648e2e001764b6b5da10006', 4, 'ff808081763e95e4017640f32c5a0001', '修改轮播管理', NULL, '/banneradmin/modify', 'PUT', NULL, 5, '2020-12-10 14:51:36', 0);
INSERT INTO `sbc-setting`.`authority`(`authority_id`, `system_type_cd`, `function_id`, `authority_title`, `authority_name`, `authority_url`, `request_type`, `remark`, `sort`, `create_time`, `del_flag`) VALUES ('ff8080817648e2e001764b6a9eb30005', 4, 'ff808081763e95e4017640f32c5a0001', '新增轮播管理', NULL, '/banneradmin/add', 'POST', NULL, 4, '2020-12-10 14:50:47', 0);
INSERT INTO `sbc-setting`.`authority`(`authority_id`, `system_type_cd`, `function_id`, `authority_title`, `authority_name`, `authority_url`, `request_type`, `remark`, `sort`, `create_time`, `del_flag`) VALUES ('ff8080817648e2e001764b6a34ba0004', 4, 'ff808081763e95e4017640f32c5a0001', '轮播id查询', NULL, '/banneradmin/*', 'GET', NULL, 3, '2020-12-10 14:50:20', 0);
INSERT INTO `sbc-setting`.`authority`(`authority_id`, `system_type_cd`, `function_id`, `authority_title`, `authority_name`, `authority_url`, `request_type`, `remark`, `sort`, `create_time`, `del_flag`) VALUES ('ff8080817648e2e001764b690c0e0003', 4, 'ff808081763e95e4017640f32c5a0001', '列表查询', NULL, '/banneradmin/list', 'POST', NULL, 2, '2020-12-10 14:49:04', 0);
INSERT INTO `sbc-setting`.`authority`(`authority_id`, `system_type_cd`, `function_id`, `authority_title`, `authority_name`, `authority_url`, `request_type`, `remark`, `sort`, `create_time`, `del_flag`) VALUES ('ff8080817648e2e001764b6841b30002', 4, 'ff808081763e95e4017640f32c5a0001', '分页查询', NULL, '/banneradmin/page', 'POST', NULL, 1, '2020-12-10 14:48:12', 0);
INSERT INTO `sbc-setting`.`authority`(`authority_id`, `system_type_cd`, `function_id`, `authority_title`, `authority_name`, `authority_url`, `request_type`, `remark`, `sort`, `create_time`, `del_flag`) VALUES ('ff8080817648e2e001764bb031330008', 4, 'ff808081763e95e4017640f32c5a0001', '隐藏/显示', NULL, '/banneradmin/modifyStatus', 'PUT', NULL, 7, '2020-12-10 16:06:47', 0);



-- ***************************************** setting end *********************************************


-- ***************************************** marketing begin *********************************************
ALTER TABLE `sbc-marketing`.`marketing`
ADD COLUMN `real_end_time`  datetime NULL AFTER `is_pause`,
ADD COLUMN `termination_flag`  tinyint(4) NULL DEFAULT 0 COMMENT '终止标志位：1：终止，0非终止' AFTER `real_end_time`;
-- ***************************************** marketing end *********************************************

-- ***************************************** customer begin *********************************************
-- ***************************************** customer end ***********************************************


-- ***************************************** xxl_job begin ***********************************************
-- ***************************************** xxl_job end ***********************************************
--满折保留sql脚本
ALTER TABLE sbc-marketing. `marketing_full_discount_level`
MODIFY COLUMN `discount`  decimal(10,4) NOT NULL COMMENT '满金额|数量后折扣' AFTER `full_count`;

--把送货到家的目录名修改为配送文案
UPDATE `sbc-setting`.`menu_info` SET `menu_name` = '配送文案' WHERE `menu_name` = '送货到家';

--物流、快递、自提添加文案字段
ALTER TABLE `sbc-setting`.`home_delivery` ADD COLUMN `logistics_content` text DEFAULT NULL COMMENT '物流文案';
ALTER TABLE `sbc-setting`.`home_delivery` ADD COLUMN `express_content` text DEFAULT NULL COMMENT '快递文案';
ALTER TABLE `sbc-setting`.`home_delivery` ADD COLUMN `pick_self_content` text DEFAULT NULL COMMENT '自提文案';

INSERT INTO `sbc-setting`.`authority` (`authority_id`, `system_type_cd`, `function_id`, `authority_title`, `authority_name`, `authority_url`, `request_type`, `remark`, `sort`, `create_time`, `del_flag`) VALUES ('ff8080817662a24f0176647e8ff6000f', '3', 'fc927b993fe311e9828800163e0fc468', '促销活动终止', NULL, '/marketing/termination/*', 'PUT', NULL, '99', '2020-12-15 11:43:05', '0');
INSERT INTO `sbc-setting`.`authority` (`authority_id`, `system_type_cd`, `function_id`, `authority_title`, `authority_name`, `authority_url`, `request_type`, `remark`, `sort`, `create_time`, `del_flag`) VALUES ('ff8080817662a24f017664824d610010', '3', 'ff8080816c6f1aff016c83df2e170007', '获取商品分类一级列表', NULL, '/xsite/goodsRootCatesForXsite', 'GET', NULL, '99', '2020-12-15 11:47:10', '0');

-- ***************************************** 代客退单权限放开 ***********************************************
UPDATE `sbc-setting`.`menu_info` SET `del_flag` =0 WHERE `menu_id`='fc8e105f3fe311e9828800163e0fc468';

--  ************商品推荐权限************
INSERT INTO `sbc-setting`.`function_info` (`function_id`, `system_type_cd`, `menu_id`, `function_title`, `function_name`, `remark`, `sort`, `create_time`, `del_flag`) VALUES ('ff808081769b486c01769cd1918e0000', '4', 'ff80808171b5324a0171b96128b90001', '商品推荐查看', 'f_goods_recommend', '', '1', '2020-12-26 10:12:29', '0');
INSERT INTO `sbc-setting`.`function_info` (`function_id`, `system_type_cd`, `menu_id`, `function_title`, `function_name`, `remark`, `sort`, `create_time`, `del_flag`) VALUES ('ff808081769b486c01769cd843b70002', '4', 'ff80808171b5324a0171b96128b90001', '商品推荐保存', 'f_goods_recommend_modify', NULL, '2', '2020-12-26 10:19:48', '0');


INSERT INTO `sbc-setting`.`authority` (`authority_id`, `system_type_cd`, `function_id`, `authority_title`, `authority_name`, `authority_url`, `request_type`, `remark`, `sort`, `create_time`, `del_flag`) VALUES ('ff808081769b486c01769cd3d8e00001', '4', 'ff808081769b486c01769cd1918e0000', '推荐商品查看', NULL, '/goodsrecommendsetting/get-setting', 'GET', NULL, '1', '2020-12-26 10:14:58', '0');
INSERT INTO `sbc-setting`.`authority` (`authority_id`, `system_type_cd`, `function_id`, `authority_title`, `authority_name`, `authority_url`, `request_type`, `remark`, `sort`, `create_time`, `del_flag`) VALUES ('ff808081769b486c01769cd9ad200003', '4', 'ff808081769b486c01769cd843b70002', '商品推荐保存', NULL, '/goodsrecommendsetting/modify', 'PUT', NULL, '1', '2020-12-26 10:21:20', '0');

-- ********************补等级日志************
INSERT INTO `s2b_statistics`.`replay_customer_level` (`customer_level_id`, `customer_level_name`, `customer_level_discount`, `growth_value`, `rank_badge_img`, `is_defalt`, `del_flag`, `create_time`, `create_person`, `update_time`, `update_person`, `delete_time`, `delete_person`) VALUES ('1', '银杏级', '0.80', '0', 'https://wanmi-b2b.oss-cn-shanghai.aliyuncs.com/201909301105089320.png', '1', '0', '2017-04-18 10:06:46', NULL, '2020-07-31 13:59:07', '2c8080815cd3a74a015cd3ae86850001', NULL, NULL);
-- *******************数谋表结构同步**********
RENAME TABLE `s2b_statistics`.`replay_customer` TO `s2b_statistics`.`replay_customer_bak`;
RENAME TABLE `s2b_statistics`.`replay_customer_detail` TO `s2b_statistics`.`replay_customer_detail_bak`;
RENAME TABLE `s2b_statistics`.`replay_goods_info` TO `s2b_statistics`.`replay_goods_info_bak`;
RENAME TABLE `s2b_statistics`.`replay_store` TO `s2b_statistics`.`replay_store_bak`;

RENAME TABLE `s2b_statistics`.`replay_customer_new` TO `s2b_statistics`.`replay_customer`;
RENAME TABLE `s2b_statistics`.`replay_customer_detail_new` TO `s2b_statistics`.`replay_customer_detail`;
RENAME TABLE `s2b_statistics`.`replay_goods_info_new` TO `s2b_statistics`.`replay_goods_info`;
RENAME TABLE `s2b_statistics`.`replay_store_new` TO `s2b_statistics`.`replay_store`;

ALTER TABLE `sbc-goods`.`goods_info` ADD COLUMN `customer_id`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '客户ID' AFTER `shelflife`;
ALTER TABLE `s2b_statistics`.`replay_goods_info` ADD COLUMN `customer_id`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '客户ID' AFTER `shelflife`;