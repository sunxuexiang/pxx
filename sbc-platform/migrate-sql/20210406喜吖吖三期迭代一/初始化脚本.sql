
-- 弹窗表
 CREATE TABLE `sbc-setting`.`popup` (
  `popup_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '弹窗Id',
  `popup_name` varchar(40) NOT NULL COMMENT '弹窗名称',
  `begin_time` datetime DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '结束时间',
  `popup_url` varchar(200) NOT NULL COMMENT '弹窗url',
  `application_page_ame` varchar(250) NOT NULL COMMENT '应用页面:商城首页：shoppingIndex,购物车:shoppingCart,个人中心：personalCenter,个人中心：personalCenter,会员中心：memberCenter,拼团频道：groupChannel,秒杀频道：seckillChannel,领劵中心：securitiesCenter,积分商城: integralMall',
  `jump_page` text NOT NULL COMMENT '跳转页面',
  `launch_frequency` varchar(200) NOT NULL COMMENT '投放频次',
  `size_type` tinyint(4) NOT NULL COMMENT '边框尺寸  0:非全屏，1：全屏',
  `del_flag` tinyint(4) NOT NULL COMMENT '删除标记  0：正常，1：删除',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `create_person` varchar(32) DEFAULT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `update_person` varchar(32) DEFAULT NULL COMMENT '修改人',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  `delete_person` varchar(32) DEFAULT NULL COMMENT '删除人',
  `is_pause` tinyint(4) NOT NULL COMMENT '是否暂停（1：暂停，0：正常）',
  PRIMARY KEY (`popup_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COMMENT='弹窗表';

-- 弹窗关联应用页排序表
CREATE TABLE `sbc-setting`.`application_page` (
  `application_page_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '应用页Id',
  `application_page_name` varchar(40) NOT NULL COMMENT '应用页面:商城首页：shoppingIndex,购物车:shoppingCart,个人中心：personalCenter,个人中心：personalCenter,会员中心：memberCenter,拼团频道：groupChannel,秒杀频道：seckillChannel,领劵中心：securitiesCenter,积分商城: integralMall',
  `popup_id` bigint(20) NOT NULL COMMENT '弹窗id',
  `sort_number` bigint(20) DEFAULT NULL COMMENT '排序号',
  PRIMARY KEY (`application_page_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='弹窗关联应用页排序表';

-- 商品标签表
CREATE TABLE `sbc-goods`.`goods_label` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '标签id',
  `name` varchar(45) NOT NULL COMMENT '标签名称',
  `visible` tinyint(4) DEFAULT '0' COMMENT '商品列表展示开关 0: 关闭 1:开启',
  `sort` int(11) DEFAULT '0' COMMENT '排序',
	`image` varchar(500) NOT NULL COMMENT '标签图片',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `create_person` varchar(32) DEFAULT NULL COMMENT '创建人id',
  `update_person` varchar(32) DEFAULT NULL COMMENT '更新人id',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `del_flag` tinyint(2) DEFAULT NULL COMMENT '删除标志 0未删除 1已删除',
  PRIMARY KEY (`id`),
  KEY `idx_label_name` (`name`),
  KEY `idx_del_flag` (`del_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品标签表';

-- 商品表增加标签关联
ALTER TABLE `sbc-goods`.`goods`
ADD COLUMN `label_id_str` text CHARACTER SET utf8mb4 NULL COMMENT '标签id，以逗号拼凑';


CREATE TABLE `sbc-setting`.`activity_config`(
   `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '活动配置id',
	 `name` varchar(45) DEFAULT NULL COMMENT '配置名称',
	 `alias` varchar(45) DEFAULT NULL COMMENT '配置别名',
	 `value` varchar(500) DEFAULT NULL COMMENT '配置别名',
	 `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `create_person` varchar(32) DEFAULT NULL COMMENT '创建人id',
  `update_person` varchar(32) DEFAULT NULL COMMENT '更新人id',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `del_flag` tinyint(2) DEFAULT NULL COMMENT '删除标志 0未删除 1已删除',
	PRIMARY KEY (`id`),
	UNIQUE KEY `name` (`name`),
	KEY `idx_del_flag` (`del_flag`)
)


INSERT INTO `sbc-setting`.`activity_config`( `name`, `alias`, `value`, `create_time`, `create_person`, `update_person`, `update_time`, `del_flag`) VALUES ( 'isOpen', '活动开关', '0', '2021-04-19 18:45:02', NULL, NULL, NULL, 0);
INSERT INTO `sbc-setting`.`activity_config`( `name`, `alias`, `value`, `create_time`, `create_person`, `update_person`, `update_time`, `del_flag`) VALUES ( 'fullReductionIcon', '满减', NULL, '2021-04-19 18:45:22', NULL, NULL, NULL, 0);
INSERT INTO `sbc-setting`.`activity_config`( `name`, `alias`, `value`, `create_time`, `create_person`, `update_person`, `update_time`, `del_flag`) VALUES ( 'onceReductionIcon', '立减', NULL, '2021-04-19 18:45:40', NULL, NULL, NULL, 0);
INSERT INTO `sbc-setting`.`activity_config`( `name`, `alias`, `value`, `create_time`, `create_person`, `update_person`, `update_time`, `del_flag`) VALUES ( 'discountIcon', '买折', NULL, '2021-04-19 18:46:19', NULL, NULL, NULL, 0);
INSERT INTO `sbc-setting`.`activity_config`( `name`, `alias`, `value`, `create_time`, `create_person`, `update_person`, `update_time`, `del_flag`) VALUES ( 'discountGiftIcon', '买赠', NULL, '2021-04-19 18:46:57', NULL, NULL, NULL, 0);


-- 定时下架任务
INSERT INTO `xxl-job`.`xxl_job_qrtz_trigger_info`(`id`, `job_group`, `job_cron`, `job_desc`, `add_time`, `update_time`, `author`, `alarm_email`, `executor_route_strategy`, `executor_handler`, `executor_param`, `executor_block_strategy`, `executor_timeout`, `executor_fail_retry_count`, `glue_type`, `glue_source`, `glue_remark`, `glue_updatetime`, `child_jobid`) VALUES (87, 1, '0 */2 * * * ?', '定时下架库存不足的SPU', '2021-04-20 11:42:11', '2021-04-20 11:42:11', '吕衡', '', 'FIRST', 'soldOutGoodsHandler', '', 'SERIAL_EXECUTION', 0, 0, 'BEAN', '', 'GLUE代码初始化', '2021-04-20 11:42:11', '');

--分类品牌排序 编辑接口权限
INSERT INTO `sbc-setting`.`authority`(`authority_id`, `system_type_cd`, `function_id`, `authority_title`, `authority_name`, `authority_url`, `request_type`, `remark`, `sort`, `create_time`, `del_flag`) VALUES ('2c939a507905798801790868f3b50009', 4, 'ff80808178f255500178f40b5eb8000b', '编辑排序', NULL, '/brandSort/rel/modify', 'PUT', NULL, 6, '2021-04-25 17:42:46', 0);

-- 导购邀新表
CREATE TABLE `sbc-customer`.`invitation_statistics` (
  `employee_id` varchar(32) NOT NULL DEFAULT '' COMMENT '业务员ID',
  `date` varchar(20) NOT NULL DEFAULT '' COMMENT '日期',
  `results_count` bigint(20) DEFAULT '0' COMMENT '邀新数',
  `trade_price_total` decimal(20,2) DEFAULT '0.00' COMMENT '订单总额',
  `trade_goods_total` bigint(20) DEFAULT '0' COMMENT '总商品数',
  `trade_total` bigint(20) DEFAULT '0' COMMENT '总订单数',
  PRIMARY KEY (`employee_id`,`date`),
  KEY `idx_date` (`date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='邀新统计表';



-- 天气相关设置
CREATE TABLE `sbc-setting`.`weather_switch` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '开关id',
  `top_img_status`  tinyint(4) NOT NULL DEFAULT '0' COMMENT '顶部背景图状态(0.关闭，1开启)',
  `top_img` varchar(255) DEFAULT NULL COMMENT'顶部背景图',
	`slogan_img_status`  tinyint(4) NOT NULL DEFAULT '0' COMMENT 'slogan图图状态(0.关闭，1开启)',
  `slogan_img` varchar(255) DEFAULT NULL COMMENT'slogan图',
  `component_status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '组件开关状态 (0：关闭 1：开启)',
  `search_back_status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '搜索背景状态 (0：关闭 1：开启)',
	`search_back_color` varchar(50) DEFAULT NULL COMMENT'搜索背景颜色值',
	`create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='天气设置';

INSERT INTO `sbc-setting`.`weather_switch`(`id`, `top_img_status`, `top_img`, `slogan_img_status`, `slogan_img`, `component_status`, `search_back_status`, `search_back_color`, `create_time`, `update_time`) VALUES (1, 0, '', 0, '', 0, 0, '', '2021-04-16 17:09:18', '2021-04-16 16:35:42');



 -- 天气设置相关权限
-- INSERT INTO `sbc-setting`.`menu_info`(`menu_id`, `system_type_cd`, `parent_menu_id`, `menu_grade`, `menu_name`, `menu_url`, `menu_icon`, `sort`, `create_time`, `del_flag`) VALUES ('ff80808178d6ea630178d961d6b00001', 4, 'fc8e017c3fe311e9828800163e0fc468', 3, '首页设置', '/home-page-setting', NULL, 151, '2021-04-16 14:32:51', 0);
--
-- INSERT INTO `sbc-setting`.`function_info`(`function_id`, `system_type_cd`, `menu_id`, `function_title`, `function_name`, `remark`, `sort`, `create_time`, `del_flag`) VALUES ('ff80808178d6ea630178d97aef9b0004', 4, 'ff80808178d6ea630178d961d6b00001', '首页天气设置编辑', 'f_homepage_edit', NULL, 2, '2021-04-16 15:00:16', 0);
-- INSERT INTO `sbc-setting`.`function_info`(`function_id`, `system_type_cd`, `menu_id`, `function_title`, `function_name`, `remark`, `sort`, `create_time`, `del_flag`) VALUES ('ff80808178d6ea630178d976ffcb0002', 4, 'ff80808178d6ea630178d961d6b00001', '首页天气设置查看', 'f_homepage_find', NULL, 1, '2021-04-16 14:55:58', 0);
--
-- INSERT INTO `sbc-setting`.`authority`(`authority_id`, `system_type_cd`, `function_id`, `authority_title`, `authority_name`, `authority_url`, `request_type`, `remark`, `sort`, `create_time`, `del_flag`) VALUES ('ff80808178d6ea630178d97d38750005', 4, 'ff80808178d6ea630178d97aef9b0004', '修改天气设置', NULL, '/weatherswitch/modify', 'PUT', NULL, 1, '2021-04-16 15:02:46', 0);
-- INSERT INTO `sbc-setting`.`authority`(`authority_id`, `system_type_cd`, `function_id`, `authority_title`, `authority_name`, `authority_url`, `request_type`, `remark`, `sort`, `create_time`, `del_flag`) VALUES ('ff80808178d6ea630178d97964b00003', 4, 'ff80808178d6ea630178d976ffcb0002', '查询天气设置', NULL, '/weatherswitch/getConfig', 'GET', NULL, 1, '2021-04-16 14:58:35', 0);


-- 营销活动商品终止
INSERT INTO `sbc-setting`.`authority`(`authority_id`, `system_type_cd`, `function_id`, `authority_title`, `authority_name`, `authority_url`, `request_type`, `remark`, `sort`, `create_time`, `del_flag`) VALUES ('ff80808178e65dd00178e9dbecd00002', 3, 'fc927b4b3fe311e9828800163e0fc468', '商品终止', NULL, '/marketing/terminationGoods', 'POST', NULL, 5, '2021-04-19 19:20:08', 0);

-- 商品推荐策略
INSERT INTO `sbc-setting`.`authority`(`authority_id`, `system_type_cd`, `function_id`, `authority_title`, `authority_name`, `authority_url`, `request_type`, `remark`, `sort`, `create_time`, `del_flag`) VALUES ('ff80808178f255500178f3a9841d0008', 4, 'ff808081769b486c01769cd843b70002', '商品推荐策略', NULL, '/goodsrecommendsetting/modifyStrategy', 'PUT', NULL, 2, '2021-04-21 17:01:16', 0);


-- base_config 新增 全部主体色 tag按钮色字段
ALTER TABLE `sbc-setting`.`base_config`
ADD COLUMN `all_subject_color` varchar(50) NULL COMMENT '全部主体色' AFTER `pc_logo`;

ALTER TABLE `sbc-setting`.`base_config`
ADD COLUMN `tag_button_color` varchar(50) NULL COMMENT 'tag按钮色' AFTER `pc_logo`;

-- sbc-marketing 条件标识
ALTER TABLE `sbc-marketing`.`marketing_scope` ADD COLUMN `termination_flag` tinyint(4) DEFAULT '0' COMMENT '终止标志位：1：终止，0非终止' AFTER `scope_id`;

-- 最小单价
ALTER TABLE `sbc-goods`.`goods` ADD COLUMN `minimum_price` decimal(20,2) DEFAULT NULL  COMMENT 'erp同步最小单价' AFTER `goods_subtitle`;

ALTER TABLE `sbc-goods`.`goods_recommend_setting` ADD COLUMN `is_intelligent_recommend` tinyint(4) DEFAULT '0' NOT NULL COMMENT'是否智能推荐(0:手动推荐 1:智能推荐；)'AFTER `rule`;
ALTER TABLE `sbc-goods`.`goods_recommend_setting` ADD COLUMN `intelligent_recommend_amount` int(11) DEFAULT '0' COMMENT'推荐数量（用于智能推荐）'AFTER `rule`;
ALTER TABLE `sbc-goods`.`goods_recommend_setting` ADD COLUMN `intelligent_recommend_dimensionality`  tinyint(4)  DEFAULT 0 COMMENT'推荐维度（用于智能推荐 0.三级类目 1.品牌）'AFTER `rule`;
ALTER TABLE `sbc-goods`.`goods_recommend_setting` ADD COLUMN `intelligent_strategy` tinyint(4) DEFAULT '0'  COMMENT'推荐策略(0:开启 1:关闭；)'AFTER `rule`;
ALTER TABLE `sbc-goods`.`goods_recommend_setting` MODIFY COLUMN `priority` TINYINT (4) NOT NULL COMMENT '优先级(0.最新上架时间 1.关注度 2.浏览量 3.按销量 4.按默认 5.按综合)' ;
INSERT INTO `sbc-goods`.`goods_recommend_setting`(`setting_id`, `enabled`, `entries`, `priority`, `rule`, `intelligent_strategy`, `intelligent_recommend_dimensionality`, `intelligent_recommend_amount`, `is_intelligent_recommend`) VALUES ('8b9bc86c6a684b34017a685fc3a50046', 0, '0|1', 3, 0, 0, 1, 10, 1);
UPDATE `sbc-goods`.`goods_recommend_setting` SET `enabled` = 0, `entries` = '0|1|2|3|4|5', `priority` = 0, `rule` = 0, `intelligent_strategy` = 1, `intelligent_recommend_dimensionality` = NULL, `intelligent_recommend_amount` = NULL, `is_intelligent_recommend` = 0 WHERE `setting_id` = '8a9bc76c6a674a33016a684fc3a40045';

-- 用户最后一次浏览商品记录表
CREATE TABLE `sbc-goods`.`last_goods_write` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `customer_id` varchar(255) DEFAULT NULL COMMENT '用户id',
  `goods_info_id` varchar(255) DEFAULT NULL COMMENT'商品skuId',
  `brand_id` bigint(20) DEFAULT NULL  COMMENT '品牌Id',
  `cate_id`bigint(20) DEFAULT NULL COMMENT'类目id',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8 COMMENT='用户最后一次浏览商品记录表';


-- 邀新汇总
CREATE TABLE `sbc-customer`.`invitation_history_summary` (
  `employee_id` varchar(32) NOT NULL DEFAULT '' COMMENT '业务员ID',
  `total_count` bigint(20) DEFAULT '0' COMMENT '总邀新数',
  `total_trade_price` decimal(20,2) DEFAULT '0.00' COMMENT '总订单金额',
  `total_goods_count` bigint(20) DEFAULT '0' COMMENT '总商品数',
  `trade_total` bigint(20) DEFAULT '0' COMMENT '总订单数',
  PRIMARY KEY (`employee_id`),
  KEY `idx_emp` (`employee_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='邀新历史汇总计表';

-- 定时邀新汇总定时任务
INSERT INTO `xxl-job`.`xxl_job_qrtz_trigger_info`(`id`, `job_group`, `job_cron`, `job_desc`, `add_time`, `update_time`, `author`, `alarm_email`, `executor_route_strategy`, `executor_handler`, `executor_param`, `executor_block_strategy`, `executor_timeout`, `executor_fail_retry_count`, `glue_type`, `glue_source`, `glue_remark`, `glue_updatetime`, `child_jobid`) VALUES (88, 1, '0 0 1 * * ?', '邀新历史汇总统计', '2021-04-27 17:47:52', '2021-04-27 17:47:52', '费传奇', '', 'FIRST', 'invitationHistorySummaryJobHandler', '', 'SERIAL_EXECUTION', 0, 0, 'BEAN', '', 'GLUE代码初始化', '2021-04-27 17:47:52', '');

-- 商品副标题历史数据初始化 （待定）
-- UPDATE `sbc-goods`.`goods` SET minimum_price = market_price
-- UPDATE `sbc-goods`.`goods` SET goods_subtitle = CONCAT(goods_subtitle,'=',minimum_price,'元','/',RIGHT(goods_subtitle,1));

-- 商品品类排序功能 start --
CREATE TABLE `cate_brand_sort_rel` (
  `cate_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '品类ID',
  `brand_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '品牌ID',
  `name` varchar(30) DEFAULT NULL COMMENT '品牌名称',
  `alias` varchar(30) DEFAULT NULL COMMENT '品牌别名',
  `serial_no` int(11) DEFAULT NULL COMMENT '排序序号',
  `del_flag` tinyint(4) NOT NULL COMMENT '删除标识，0：未删除 1：已删除',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `create_person` varchar(32) NOT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `update_person` varchar(32) DEFAULT NULL COMMENT '更新人',
  PRIMARY KEY (`cate_id`,`brand_id`),
  KEY `idx_name` (`name`),
  KEY `idx_serial_no` (`serial_no`),
  KEY `idx_del_flag` (`del_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='品类关联品牌排序表';
-- 商品品类排序功能 end --



-- 自动下架 start--
CREATE TABLE `sold_out_goods` (
  `id` varchar(32) NOT NULL,
  `goods_id` varchar(50) NOT NULL COMMENT '将要被下架的商品',
  `del_flag` tinyint(4) NOT NULL DEFAULT '0' COMMENT '删除标识，0：未删除 1：已删除',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `create_person` varchar(32) DEFAULT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `update_person` varchar(32) DEFAULT NULL COMMENT '更新人',
  PRIMARY KEY (`id`),
  KEY `idx_goods_id` (`goods_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT= '带下架商品表';
-- 自动下架 end--

-- 预置词 start --
ALTER TABLE `sbc-setting`.`preset_search_terms`
ADD COLUMN `sort` int NOT NULL COMMENT '搜索词优先顺序' AFTER `preset_search_keyword`;
-- 预置词 end --


-- 导航栏 start --
CREATE TABLE `sbc-setting`.`navigation_config` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `nav_name` varchar(20) NOT NULL COMMENT '导航名称',
  `icon_show` varchar(500) DEFAULT NULL COMMENT '导航图标-未点击状态',
  `icon_click` varchar(500) DEFAULT NULL COMMENT '导航图标-点击状态',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO `sbc-setting`.`navigation_config`(`id`, `nav_name`, `icon_show`, `icon_click`) VALUES (1, '首页', NULL, NULL);
INSERT INTO `sbc-setting`.`navigation_config`(`id`, `nav_name`, `icon_show`, `icon_click`) VALUES (2, '分类', NULL, NULL);
INSERT INTO `sbc-setting`.`navigation_config`(`id`, `nav_name`, `icon_show`, `icon_click`) VALUES (3, '小视频', NULL, NULL);
INSERT INTO `sbc-setting`.`navigation_config`(`id`, `nav_name`, `icon_show`, `icon_click`) VALUES (4, '购物车', NULL, NULL);
INSERT INTO `sbc-setting`.`navigation_config`(`id`, `nav_name`, `icon_show`, `icon_click`) VALUES (5, '鲸粉', NULL, NULL);
-- 导航栏 end --

ALTER TABLE `sbc-goods`.`goods_image`
ADD COLUMN `sort` int NULL COMMENT '排序序号' AFTER `del_flag`;

ALTER TABLE `sbc-goods`.`standard_image`
ADD COLUMN `sort` int NULL COMMENT '排序序号' AFTER `del_flag`;

create table `sbc-setting`.video_management
(
    video_id     bigint auto_increment comment 'ID'
        primary key,
    video_name   varchar(50) charset utf8  null comment '视频名称',
    state        tinyint    default 0      not null comment '状态0:上架,1:下架',
    play_few     bigint(10) default 0      null comment '播放数',
    resource_key varchar(255) charset utf8 null comment '素材KEY',
    artwork_url  varchar(255) charset utf8 null comment '素材地址',
    create_time  datetime                  null comment '发布时间',
    update_time  datetime                  null comment '更新时间',
    del_flag     tinyint                   null comment '删除标识,0:未删除1:已删除',
    server_type  varchar(255) charset utf8 null comment 'oss服务器类型，对应system_config的config_type'
)
    comment '视频管理';

create table `sbc-setting`.video_like
(
    video_id    bigint      not null comment '视频id',
    customer_id varchar(32) not null comment '用户id',
    primary key (video_id, customer_id)
)
    comment '视频点赞表';


-- 客户新增明细情况2021-05-14 --
CREATE TABLE `replay_customer_temp` (
  `customer_id` varchar(32) NOT NULL COMMENT '会员标识UUID',
  `customer_level_id` bigint(10) DEFAULT NULL COMMENT '客户级别',
  `growth_value` bigint(10) DEFAULT '0' COMMENT '成长值',
  `points_available` bigint(10) DEFAULT '0' COMMENT '可用积分',
  `points_used` bigint(10) DEFAULT '0' COMMENT '已用积分',
  `customer_account` varchar(20) NOT NULL COMMENT '会员登录账号|手机号',
  `customer_password` varchar(200) NOT NULL COMMENT '会员登录密码',
  `safe_level` tinyint(4) DEFAULT NULL COMMENT '密码安全等级：20危险 40低、60中、80高',
  `customer_salt_val` varchar(200) DEFAULT NULL COMMENT '盐值,用于密码加密',
  `customer_type` tinyint(4) DEFAULT NULL COMMENT '客户类型,0:平台客户,1:商家客户',
  `customer_tag` tinyint(4) DEFAULT NULL COMMENT '会员标签 0:零食店，1:便利店，2:商超，3:二批商，4:水果零售店，5:连锁系统，6:炒货店',
  `check_state` tinyint(4) NOT NULL COMMENT '账户的审核状态 0:待审核 1:已审核通过 2:审核未通过',
  `check_time` datetime DEFAULT NULL COMMENT '账户的审核时间',
  `del_flag` tinyint(4) NOT NULL COMMENT '是否删除标志 0：否，1：是',
  `login_ip` varchar(32) DEFAULT '0.0.0.0' COMMENT '登录IP',
  `login_time` datetime DEFAULT NULL COMMENT '登录时间',
  `login_error_time` int(11) DEFAULT NULL COMMENT '登录错误次数',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间|注册时间',
  `create_person` varchar(32) DEFAULT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `update_person` varchar(32) DEFAULT NULL COMMENT '修改人',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  `delete_person` varchar(32) DEFAULT NULL COMMENT '删除人',
  `login_lock_time` datetime DEFAULT NULL COMMENT '登录锁定时间',
  `customer_pay_password` varchar(200) DEFAULT NULL COMMENT '支付密码',
  `pay_error_time` int(11) DEFAULT NULL COMMENT '支付密码错误次数',
  `pay_lock_time` datetime DEFAULT NULL COMMENT '支付锁定时间',
  `sign_continuous_days` int(8) DEFAULT NULL COMMENT '签到连续天数',
  `enterprise_check_state` tinyint(4) NOT NULL DEFAULT '0' COMMENT '企业购会员审核状态  0：无状态 1：待审核 2：已审核 3：审核未通过',
  `enterprise_check_reason` varchar(200) DEFAULT NULL COMMENT '企业购审核原因',
  `customer_register_type` tinyint(4) DEFAULT NULL COMMENT '会员注册的类型',
  `enterprise_name` varchar(100) DEFAULT NULL COMMENT '企业名称',
  `social_credit_code` varchar(50) DEFAULT NULL COMMENT '社会统一信用代码',
  `business_license_url` varchar(1024) DEFAULT NULL COMMENT '营业执照地址',
  `enterprise_status_xyy` tinyint(4) DEFAULT NULL COMMENT '喜吖吖企业购会员审核状态  0：无状态 1：待审核 2：已审核 3：审核未通过',
  `parent_customer_id` varchar(32) DEFAULT NULL COMMENT '父级会员Id',
  `customer_erp_id` varchar(30) DEFAULT NULL COMMENT 'erpId',
  `erp_async_flag` tinyint(4) DEFAULT '0' COMMENT 'erp的同步标志位  0-未同步  1-已同步',
  PRIMARY KEY (`customer_id`),
  UNIQUE KEY `customer_id_UNIQUE` (`customer_id`),
  KEY `INDEX_CUSTOMER_LEVEL` (`customer_level_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='客户信息表';

-- 客户新增明细情况2021-05-18 --
CREATE TABLE `replay_customer_temp_all` (
                                        `customer_id` varchar(32) NOT NULL COMMENT '会员标识UUID',
                                        `customer_level_id` bigint(10) DEFAULT NULL COMMENT '客户级别',
                                        `growth_value` bigint(10) DEFAULT '0' COMMENT '成长值',
                                        `points_available` bigint(10) DEFAULT '0' COMMENT '可用积分',
                                        `points_used` bigint(10) DEFAULT '0' COMMENT '已用积分',
                                        `customer_account` varchar(20) NOT NULL COMMENT '会员登录账号|手机号',
                                        `customer_password` varchar(200) NOT NULL COMMENT '会员登录密码',
                                        `safe_level` tinyint(4) DEFAULT NULL COMMENT '密码安全等级：20危险 40低、60中、80高',
                                        `customer_salt_val` varchar(200) DEFAULT NULL COMMENT '盐值,用于密码加密',
                                        `customer_type` tinyint(4) DEFAULT NULL COMMENT '客户类型,0:平台客户,1:商家客户',
                                        `customer_tag` tinyint(4) DEFAULT NULL COMMENT '会员标签 0:零食店，1:便利店，2:商超，3:二批商，4:水果零售店，5:连锁系统，6:炒货店',
                                        `check_state` tinyint(4) NOT NULL COMMENT '账户的审核状态 0:待审核 1:已审核通过 2:审核未通过',
                                        `check_time` datetime DEFAULT NULL COMMENT '账户的审核时间',
                                        `del_flag` tinyint(4) NOT NULL COMMENT '是否删除标志 0：否，1：是',
                                        `login_ip` varchar(32) DEFAULT '0.0.0.0' COMMENT '登录IP',
                                        `login_time` datetime DEFAULT NULL COMMENT '登录时间',
                                        `login_error_time` int(11) DEFAULT NULL COMMENT '登录错误次数',
                                        `create_time` datetime DEFAULT NULL COMMENT '创建时间|注册时间',
                                        `create_person` varchar(32) DEFAULT NULL COMMENT '创建人',
                                        `update_time` datetime DEFAULT NULL COMMENT '修改时间',
                                        `update_person` varchar(32) DEFAULT NULL COMMENT '修改人',
                                        `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
                                        `delete_person` varchar(32) DEFAULT NULL COMMENT '删除人',
                                        `login_lock_time` datetime DEFAULT NULL COMMENT '登录锁定时间',
                                        `customer_pay_password` varchar(200) DEFAULT NULL COMMENT '支付密码',
                                        `pay_error_time` int(11) DEFAULT NULL COMMENT '支付密码错误次数',
                                        `pay_lock_time` datetime DEFAULT NULL COMMENT '支付锁定时间',
                                        `sign_continuous_days` int(8) DEFAULT NULL COMMENT '签到连续天数',
                                        `enterprise_check_state` tinyint(4) NOT NULL DEFAULT '0' COMMENT '企业购会员审核状态  0：无状态 1：待审核 2：已审核 3：审核未通过',
                                        `enterprise_check_reason` varchar(200) DEFAULT NULL COMMENT '企业购审核原因',
                                        `customer_register_type` tinyint(4) DEFAULT NULL COMMENT '会员注册的类型',
                                        `enterprise_name` varchar(100) DEFAULT NULL COMMENT '企业名称',
                                        `social_credit_code` varchar(50) DEFAULT NULL COMMENT '社会统一信用代码',
                                        `business_license_url` varchar(1024) DEFAULT NULL COMMENT '营业执照地址',
                                        `enterprise_status_xyy` tinyint(4) DEFAULT NULL COMMENT '喜吖吖企业购会员审核状态  0：无状态 1：待审核 2：已审核 3：审核未通过',
                                        `parent_customer_id` varchar(32) DEFAULT NULL COMMENT '父级会员Id',
                                        `customer_erp_id` varchar(30) DEFAULT NULL COMMENT 'erpId',
                                        `erp_async_flag` tinyint(4) DEFAULT '0' COMMENT 'erp的同步标志位  0-未同步  1-已同步',
                                        PRIMARY KEY (`customer_id`),
                                        UNIQUE KEY `customer_id_UNIQUE` (`customer_id`),
                                        KEY `INDEX_CUSTOMER_LEVEL` (`customer_level_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='客户信息表';



--21.6.10 生产bug修改新增表
CREATE TABLE `sbc-customer`.`invitation_statistics_order` (
  `employee_id` varchar(32) NOT NULL DEFAULT '' COMMENT '业务员ID',
  `date` varchar(20) NOT NULL DEFAULT '' COMMENT '日期',
  `order_id` varchar(45) NOT NULL COMMENT '订单id',
  UNIQUE KEY `invitation_order_UNIQUE` (`employee_id`,`date`,`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='当日统计业务员邀新订单明细表';

--21.6.10 生产 导购邀新分享小程序码优化 新增字段
ALTER TABLE `sbc-customer`.`employee` ADD COLUMN `wechat_img_url` varchar(255)  NULL  COMMENT '微信分享码url';