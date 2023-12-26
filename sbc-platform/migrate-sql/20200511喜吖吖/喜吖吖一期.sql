ALTER TABLE `sbc-cutomer`.`customer` ADD COLUMN `enterprise_name` VARCHAR(50) DEFAULT NULL COMMIT '企业名称';
ALTER TABLE `sbc-cutomer`.`customer` ADD COLUMN `social_credit_code` VARCHAR(100) DEFAULT NULL COMMIT '社会信用代码';
ALTER TABLE `sbc-cutomer`.`customer` ADD COLUMN `business_license_url` VARCHAR(1024) DEFAULT NULL COMMIT '营业执照地址';
ALTER TABLE `sbc-cutomer`.`customer` ADD COLUMN `customer_tag` tinyint(4) DEFAULT NULL COMMENT '会员标签 0:零食店，1:便利店，2:商超，3:二批商，4:水果零售店，5:连锁系统，6:炒货店' AFTER `customer_type`;
ALTER TABLE `sbc-cutomer`.`customer` ADD COLUMN `parent_customer_id` VARCHAR(32) DEFAULT NULL COMMIT '父级Id';


ALTER TABLE `sbc-goods`.`goods_info` ADD COLUMN `vip_price` decimal(20,2) DEFAULT NULL COMMENT '大客户价' AFTER `cost_price`;
ALTER TABLE `sbc-goods`.`goods_info` ADD COLUMN `goods_source` tinyint(4) DEFAULT '1' COMMENT '商品来源，0供应商，1商家' AFTER `added_time`;
ALTER TABLE `sbc-goods`.`goods_info` ADD COLUMN `provider_goods_info_id` varchar(32) DEFAULT NULL COMMENT '所属供应商商品skuId' AFTER `enterprise_goods_audit_reason`;
ALTER TABLE `sbc-goods`.`goods_info` ADD COLUMN `provider_id` bigint(20) DEFAULT '0' COMMENT '供应商Id' AFTER `provider_goods_info_id`;
ALTER TABLE `sbc-goods`.`goods_info` ADD COLUMN goods_info_qrcode varchar(255) DEFAULT NULL COMMENT '二维码' AFTER `goods_info_barcode`;
ALTER TABLE `sbc-goods`.`goods` ADD COLUMN `goods_view_num` bigint(20) DEFAULT '0' COMMENT '商品浏览量' AFTER `goods_collect_num`;
ALTER TABLE `sbc-goods`.`goods` ADD COLUMN `provider_name` varchar(32) DEFAULT NULL COMMENT '供应商名称' AFTER `goods_favorable_comment_num`;
ALTER TABLE `sbc-goods`.`goods` ADD COLUMN `provider_id` bigint(20) DEFAULT '0' COMMENT '供应商Id' AFTER `provider_name`;
ALTER TABLE `sbc-goods`.`goods` ADD COLUMN `provider_goods_id` varchar(32) DEFAULT NULL COMMENT '所属供应商商品Id' AFTER `provider_id`;
ALTER TABLE `sbc-goods`.`goods` ADD COLUMN `recommended_retail_price` decimal(20,2) DEFAULT NULL COMMENT '建议零售价' AFTER `supply_price`;
ALTER TABLE `sbc-goods`.`goods_info` ADD COLUMN goods_info_batch_no varchar(50) DEFAULT NULL COMMENT '批次号' AFTER `goods_info_qrcode`;
ALTER TABLE `sbc-goods`.`goods_info` ADD COLUMN goods_info_type tinyint(4) DEFAULT NULL COMMENT '特价商品的标识 0:普通商品  1：特价商品' AFTER `goods_info_batch_no`;
ALTER TABLE `sbc-goods`.`goods_info` ADD COLUMN erp_goods_info_no varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT 'erpSKU编码' AFTER `goods_info_no`;
ALTER TABLE `sbc-goods`.`standard_sku` ADD COLUMN `vip_price` decimal(20,2) DEFAULT NULL COMMENT '大客户价' AFTER `cost_price`;
ALTER TABLE `sbc-goods`.`standard_sku` ADD COLUMN `goods_info_no` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT 'SKU编码' AFTER `goods_id`;
ALTER TABLE `sbc-goods`.`standard_sku` ADD COLUMN `goods_info_barcode` varchar(45) DEFAULT NULL COMMENT '条形码' AFTER `goods_info_img`;
ALTER TABLE `sbc-goods`.`goods_ware_stock` ADD COLUMN `goods_info_ware_id`varchar(100) DEFAULT NULL COMMENT '商品id+库存id' AFTER `del_flag`;
ALTER TABLE `sbc-goods`.`standard_sku` change `goods_info_no` `erp_goods_info_no` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT 'erpSKU编码';
INSERT INTO `sbc-goods`.`goods_cate` VALUES (-102,'默认分类',0,null,'0|',0,0.01,1,0,1,null,null,now(),now(),0,0,1,0,1);
INSERT INTO `sbc-goods`.`goods_cate` VALUES (-101,'默认分类',-102,null,'0|-102',2,0.01,1,0,1,null,null,now(),now(),0,0,1,0,1);
INSERT INTO `sbc-goods`.`goods_cate` VALUES (-100,'默认分类',-101,null,'0|-102|-101',3,0.01,1,0,1,null,null,now(),now(),0,0,1,0,1);


CREATE TABLE `home_delivery` (
  `home_delivery_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `content` text NOT NULL,
  `create_time` datetime DEFAULT NULL COMMENT '生成时间',
  `del_flag` tinyint(4) DEFAULT NULL COMMENT '删除标志位',
  PRIMARY KEY (`home_delivery_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4;







CREATE TABLE `sbc-goods`.`goods_recommend_goods` (
  `recommend_id` varchar(32) NOT NULL COMMENT '商品推荐主键',
  `goods_info_id` varchar(32) NOT NULL COMMENT '推荐的商品编号',
  PRIMARY KEY (`recommend_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='商品推荐表';

CREATE TABLE `sbc-goods`.`goods_recommend_setting` (
  `setting_id` varchar(32) NOT NULL COMMENT '商品推荐设置主键',
  `enabled` tinyint(4) NOT NULL COMMENT '商品推荐开关(0:开启；1:关闭)',
  `entries` varchar(32) NOT NULL COMMENT '推荐入口,多选逗号隔开',
  `priority` tinyint(4) NOT NULL COMMENT '优先级',
  `rule` int(11) DEFAULT '0' COMMENT '推荐规则',
  PRIMARY KEY (`setting_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='商品推荐配置表';


INSERT INTO `sbc-setting`.`store_resource_cate` VALUES (-1000,123456859,3,'二维码',0,NULL,0,1,NULL,NULL,NOW(),NOW(),0,0,0);

-- 增加markeing 的满订单营销类型
ALTER TABLE `sbc-marketing`.`marketing_full_reduction_level` ADD COLUMN full_order bigint(5) DEFAULT NULL COMMENT '满减订单';


INSERT `sbc-pay`.`pay_gateway` (id,NAME,is_open,TYPE,create_time,store_id) VALUE(6,'REPLACEWECHAT',1,0,NOW(),-1),(7,'REPLACEALIPAY',1,0,NOW(),-1);


INSERT `sbc-pay`.`pay_channel_item` (NAME,gateway_name,gateway_id,channel,is_open,terminal,CODE,create_time)
VALUE('微信代付H5支付','REPLACEWECHAT',6,'ReplaceWechat',1,1,'ReplaceWechat_h5',NOW()),
('微信代付app支付','REPLACEWECHAT',6,'ReplaceWechat',1,2,'ReplaceWechat_app',NOW()),
('支付宝代付h5支付','REPLACEALIPAY',7,'ReplaceAlipay',1,1,'ReplaceAlipay_h5',NOW()),
('支付宝代付app支付','REPLACEALIPAY',7,'ReplaceAlipay',1,2,'ReplaceAlipay_app',NOW());


INSERT `sbc-pay`.`pay_gateway_config` (gateway_id,create_time) VALUE(6,NOW()),(7,NOW());



-- 商品库新增工单处理明细表和工单表
CREATE TABLE `sbc-goods`.`work_order_detail` (
  `work_order_del_id` varchar(32) NOT NULL COMMENT '工单处理明细Id',
  `deal_time` datetime DEFAULT NULL COMMENT '处理时间',
  `status` tinyint(4) DEFAULT NULL COMMENT '处理状态',
  `suggestion` varchar(255) DEFAULT NULL COMMENT '处理建议',
  `work_order_id` varchar(32) DEFAULT NULL COMMENT '工单Id',
  PRIMARY KEY (`work_order_del_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工单处理明细';

CREATE TABLE `work_order` (
  `work_order_id` varchar(32) NOT NULL COMMENT '工单Id',
  `work_order_no` varchar(20) DEFAULT NULL COMMENT '工单号',
  `social_credit_code` varchar(30) DEFAULT NULL COMMENT '社会信用代码',
  `approval_customer_id` varchar(32) DEFAULT NULL COMMENT '注册人Id',
  `registed_customer_id` varchar(32) DEFAULT NULL COMMENT '已注册会员的Id',
  `account_merge_status` tinyint(4) DEFAULT NULL COMMENT '账号合并状态',
  `status` tinyint(4) DEFAULT NULL COMMENT '状态 0:待处理，1：已完成',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `modify_time` datetime DEFAULT NULL COMMENT '修改时间',
  `delete_tiime` datetime DEFAULT NULL COMMENT '删除时间',
  `del_flag` tinyint(4) DEFAULT NULL COMMENT '删除标志位',
  PRIMARY KEY (`work_order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工单表';



CREATE TABLE `ware_house` (
  `ware_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `store_id` bigint(20) NOT NULL COMMENT '店铺标识',
  `ware_name` varchar(20) NOT NULL COMMENT '仓库名称',
  `ware_code` varchar(20) NOT NULL COMMENT '仓库编号',
  `province_id` bigint(10) NOT NULL COMMENT '省份',
  `city_id` bigint(10) NOT NULL COMMENT '市',
  `area_id` bigint(10) NOT NULL COMMENT '区',
  `address_detail` varchar(50) CHARACTER SET utf8 NOT NULL COMMENT '详细地址',
  `default_flag` tinyint(4) NOT NULL COMMENT '是否默认仓 0：否，1：是',
  `destination_area` varchar(3000) DEFAULT NULL COMMENT '配送地id(逗号分隔)',
  `destination_area_name` varchar(3000) DEFAULT NULL COMMENT '配送地名称(逗号分隔)',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `create_person` varchar(32) CHARACTER SET utf8 DEFAULT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `update_person` varchar(32) CHARACTER SET utf8 DEFAULT NULL COMMENT '编辑人',
  `del_flag` tinyint(4) NOT NULL COMMENT '是否删除标志 0：否，1：是',
  PRIMARY KEY (`ware_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COMMENT='分仓仓库表';

-- 仓库地区表 --
CREATE TABLE `ware_house_city` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `ware_id` bigint(20) NOT NULL COMMENT '仓库iD',
  `province_id` bigint(10) DEFAULT NULL COMMENT '省份',
  `city_id` bigint(10) DEFAULT NULL COMMENT '市',
  `area_id` bigint(20) DEFAULT NULL COMMENT '区县ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=29277 DEFAULT CHARSET=utf8 COMMENT='仓库关联地区表';

-- sku分仓库存表 --
CREATE TABLE `goods_ware_stock` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `goods_info_id` varchar(32) NOT NULL COMMENT 'sku ID',
  `goods_info_no` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT 'sku编码',
  `goods_id` varchar(32) NOT NULL COMMENT '商品ID',
  `store_id` bigint(20) NOT NULL COMMENT '店铺Id',
  `ware_id` bigint(20) NOT NULL COMMENT '仓库ID ',
  `stock` bigint(20) NOT NULL COMMENT '货品库存',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `create_person` varchar(32) DEFAULT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `update_person` varchar(32) DEFAULT NULL COMMENT '编辑人',
  `del_flag` tinyint(4) NOT NULL COMMENT '是否删除标志 0：否，1：是',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=21041 DEFAULT CHARSET=utf8 COMMENT='sku关联仓库库存';

-- sku关联仓库库存明细数据表 --
CREATE TABLE `goods_ware_stock_detail` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `goods_ware_stock_id` bigint(20) DEFAULT NULL COMMENT '商品库存关联表ID',
  `stock_import_no` varchar(14) NOT NULL COMMENT '导入编码',
  `goods_info_no` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT 'sku编码',
  `import_type` tinyint(4) NOT NULL COMMENT '导入类型 0：导入，1：编辑，2：返还，3：下单扣减',
  `operate_stock` bigint(20) NOT NULL COMMENT '操作库存',
  `stock` bigint(20) NOT NULL COMMENT '库存数量',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `create_person` varchar(32) DEFAULT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `update_person` varchar(32) DEFAULT NULL COMMENT '编辑人',
  `del_flag` tinyint(4) NOT NULL COMMENT '是否删除标志 0：否，1：是',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=21018 DEFAULT CHARSET=utf8 COMMENT='sku关联仓库库存明细数据表';
GINE=InnoDB AUTO_INCREMENT=86 DEFAULT CHARSET=utf8mb4 COMMENT='入库记录明细';

-- 新增大客户价字段
ALTER TABLE `sbc-goods`.`goods` ADD COLUMN `vip_price` DECIMAL(20,2) DEFAULT NULL COMMENT '大客户价' AFTER `market_price`;

ALTER TABLE `sbc-goods`.`ware_house` ADD COLUMN `sp_price_id` varchar(32) DEFAULT NULL COMMENT '特价商品id' AFTER `pick_up_flag`;

ALTER TABLE `sbc-goods`.`ware_house` ADD COLUMN `lat` decimal(10,6) NOT NULL COMMENT '纬度';
ALTER TABLE `sbc-goods`.`ware_house` ADD COLUMN `lng` decimal(10,6) NOT NULL COMMENT '经度';
ALTER TABLE `sbc-goods`.`type` ADD COLUMN `lng` tinyint(4) NOT NULL COMMENT '是否是线上仓，0：不是，1：是';

ALTER TABLE `sbc-customer`.`customer_delivery_address` ADD COLUMN `lat` decimal(10,6) NOT NULL COMMENT '纬度';
ALTER TABLE `sbc-customer`.`customer_delivery_address` ADD COLUMN `lng` decimal(10,6) NOT NULL COMMENT '经度';

ALTER TABLE `sbc-goods`.`goods_ware_stock_detail` ADD COLUMN `goods_info_id` varchar(32) DEFAULT NULL COMMENT '特价商品id' AFTER `del_flag`;
ALTER TABLE `sbc-goods`.`goods_ware_stock_detail` ADD COLUMN `ware_id` bigint(20) DEFAULT NULL COMMENT '特价商品id' AFTER `goods_ware_stock_detail`;

-- 缺货明细表
CREATE TABLE `sbc-goods`.`stockout_detail` (
  `stockout_detail_id` VARCHAR(32) NOT NULL COMMENT '缺货明细',
  `stockout_id` VARCHAR(32) NOT NULL COMMENT '缺货列表id',
  `customer_id` VARCHAR(32) DEFAULT NULL COMMENT '会员id',
  `goods_info_id` VARCHAR(32) DEFAULT NULL COMMENT 'sku id',
  `goods_info_no` VARCHAR(32) DEFAULT NULL COMMENT 'sku编码',
  `stockout_num` BIGINT(20) DEFAULT NULL COMMENT '缺货数量',
  `city_code` VARCHAR(32) DEFAULT NULL COMMENT '缺货市code',
  `address` VARCHAR(128) DEFAULT NULL COMMENT '下单人详细地址',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `del_flag` TINYINT(4) DEFAULT '0' COMMENT '删除标识,0:未删除1:已删除',
  PRIMARY KEY (`stockout_detail_id`)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4;
ALTER TABLE `sbc-goods`.`stockout_manage` ADD COLUMN `ware_id` bigint(20) NOT NULL COMMENT '仓库ID' AFTER `stockout_id`;
--  缺货管理表
CREATE TABLE `sbc-goods`.`stockout_manage` (
  `stockout_id` VARCHAR(32) NOT NULL COMMENT '缺货管理',
  `goods_name` VARCHAR(255) DEFAULT NULL COMMENT '商品名称',
  `goods_info_id` VARCHAR(32) NOT NULL COMMENT 'sku id',
  `goods_info_no` VARCHAR(32) NOT NULL COMMENT 'sku 编码',
  `brand_id` BIGINT(20) DEFAULT NULL COMMENT '品牌id',
  `brand_name` VARCHAR(45) DEFAULT NULL COMMENT '品牌名称',
  `stockout_num` BIGINT(20) DEFAULT NULL COMMENT '缺货数量',
  `stockout_city` VARCHAR(3000) DEFAULT NULL COMMENT '缺货地区',
  `update_time` DATETIME DEFAULT NULL COMMENT '修改时间',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `del_flag` TINYINT(4) DEFAULT '0' COMMENT '删除标识,0:未删除1:已删除',
  `replenishment_flag` TINYINT(4) DEFAULT '0' COMMENT '补货标识,0:未补货1:已补货',
  `store_id` BIGINT(20) NOT NULL COMMENT '店铺id',
  `goods_info_img` VARCHAR(255) DEFAULT NULL COMMENT '商品图片',
  PRIMARY KEY (`stockout_id`)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4;


-- 增加商品的步长
ALTER TABLE `sbc-goods`.`goods_info` ADD COLUMN add_step bigint(5) DEFAULT NULL COMMENT '商品增加的步长值';

-- 增加商品的默认分类
INSERT INTO `sbc-goods`.`goods_cate` VALUES (-102, '默认分类', 0, NULL, '0|', 0, 0.01, 1, 0.00, 1, NULL, NULL, '2020-05-21 19:29:55', '2020-05-21 19:29:55', 0, 0, 1, 0.00, 1);
INSERT INTO `sbc-goods`.`goods_cate` VALUES (-101, '默认分类', -102, NULL, '0|-102', 2, 0.01, 1, 0.00, 1, NULL, NULL, '2020-05-21 19:29:55', '2020-05-21 19:29:55', 0, 0, 1, 0.00, 1);
INSERT INTO `sbc-goods`.`goods_cate` VALUES (-100, '默认分类', -101, NULL, '0|-102|-101', 3, 0.01, 1, 0.00, 1, NULL, NULL, '2020-05-21 19:29:55', '2020-05-21 19:29:55', 0, 0, 1, 0.00, 1);

-- 增加快递公司
INSERT INTO `sbc-setting`.`express_company` VALUES (-100, '喜吖吖快递', 'xyykd', 0, 1, 0);

-- 首页静态化信息
CREATE TABLE `sbc-setting`.`magic_page` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `html_string` TEXT NOT NULL COMMENT '魔方生成的页面静态html内容',
  `del_flag` TINYINT(4) NULL COMMENT '是否删除 0 否  1 是',
  `create_time` DATETIME NULL COMMENT '创建时间',
  `update_time` DATETIME NULL COMMENT '修改时间',
  `operate_person` VARCHAR(45) NULL COMMENT '操作人',
  PRIMARY KEY (`id`))
DEFAULT CHARACTER SET = utf8mb4
COMMENT = '魔方建站生成的静态dom表';


-- 竞价排名配置表
CREATE TABLE `sbc-goods`.`bidding` (
  `bidding_id` varchar(32) NOT NULL COMMENT '竞价配置主键',
  `keywords` varchar(32) DEFAULT NULL COMMENT '关键字,分类',
  `bidding_type` tinyint(4) DEFAULT NULL COMMENT '竞价类型0:关键字，1:分类',
  `bidding_status` tinyint(4) DEFAULT NULL COMMENT '竞价的状态：0:未开始，1:进行中，2:已结束',
  `start_time` datetime DEFAULT NULL COMMENT '开始时间',
  `modify_time` datetime DEFAULT NULL COMMENT '修改时间',
  `end_time` datetime DEFAULT NULL COMMENT '结束时间',
  `del_flag` tinyint(4) DEFAULT NULL COMMENT '删除标志位',
  PRIMARY KEY (`bidding_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `sbc-goods`.`bidding_goods` (
  `bidding_goods_id` varchar(32) NOT NULL COMMENT '竞价商品的Id',
  `bidding_id` varchar(32) DEFAULT NULL COMMENT '竞价的Id',
  `sort` int(2) DEFAULT NULL COMMENT '排名',
  `goods_info_id` varchar(32) DEFAULT NULL COMMENT 'skuId',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `modify_time` datetime DEFAULT NULL COMMENT '修改时间',
  `del_time` datetime DEFAULT NULL COMMENT '删除时间',
  `del_flag` tinyint(4) DEFAULT NULL COMMENT '删除标志',
  PRIMARY KEY (`bidding_goods_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 增加商品的关键字和排名
ALTER TABLE `sbc-goods`.`goods_info` ADD COLUMN key_words varchar (200) DEFAULT NULL COMMENT '商品竞价排名使用的关键词';
ALTER TABLE `sbc-goods`.`goods_info` ADD COLUMN sort_num_key int (2) DEFAULT NULL COMMENT '商品竞价排名使用的关键字排序值';
ALTER TABLE `sbc-goods`.`goods_info` ADD COLUMN sort_num_cate int (2) DEFAULT NULL COMMENT '商品竞价排名使用的分类排序值';
update `sbc-goods`.`goods_info` set sort_num_key = 0 ;
update `sbc-goods`.`goods_info` set sort_num_cate = 0 ;


INSERT INTO `sbc-setting`.`menu_info` VALUES ('ff80808173dc77b90173e1c6b9d90000', 4, 'fc8df1b53fe311e9828800163e0fc468', 3, '竞价排名', '/bidding', NULL, 9, '2020-08-12 16:26:04', 0);

INSERT INTO `sbc-setting`.`function_info` VALUES ('ff80808173dc77b90173e1c738dc0001', 4, 'ff80808173dc77b90173e1c6b9d90000', '新增竞价排名列表', 'f_bidding_add', NULL, 1, '2020-08-12 16:26:37', 0);
INSERT INTO `sbc-setting`.`function_info` VALUES ('ff80808173dc77b90173e1c7d9ea0002', 4, 'ff80808173dc77b90173e1c6b9d90000', '查看竞价排名', 'f_bidding_list', NULL, 2, '2020-08-12 16:27:18', 0);
INSERT INTO `sbc-setting`.`function_info` VALUES ('ff80808173dc77b90173e1c84e320003', 4, 'ff80808173dc77b90173e1c6b9d90000', '编辑竞价排名', 'f_bidding_edit', NULL, 3, '2020-08-12 16:27:48', 0);
INSERT INTO `sbc-setting`.`function_info` VALUES ('ff80808173dc77b90173e1c8afd20004', 4, 'ff80808173dc77b90173e1c6b9d90000', '竞价排名详情', 'f_bidding_find', NULL, 4, '2020-08-12 16:28:13', 0);

INSERT INTO `sbc-setting`.`authority` VALUES ('ff80808173dc77b90173e1cd44350005', 4, 'ff80808173dc77b90173e1c7d9ea0002', '竞价排名列表', NULL, '/bidding/page', 'POST', NULL, 1, '2020-08-12 16:33:13', 0);
INSERT INTO `sbc-setting`.`authority` VALUES ('ff80808173dc77b90173e1ce3dab0006', 4, 'ff80808173dc77b90173e1c8afd20004', '新增竞价排名', NULL, '/bidding/add', 'PUT', NULL, 1, '2020-08-12 16:34:17', 0);
INSERT INTO `sbc-setting`.`authority` VALUES ('ff80808173dc77b90173e1cece130007', 4, 'ff80808173dc77b90173e1c8afd20004', '修改竞价排名', NULL, '/bidding/modify', 'PUT', NULL, 2, '2020-08-12 16:34:54', 0);
INSERT INTO `sbc-setting`.`authority` VALUES ('ff80808173dc77b90173e1d213750008', 4, 'ff80808173dc77b90173e1c84e320003', '编辑竞价', NULL, '/bidding/modify', 'PUT', NULL, 1, '2020-08-12 16:38:28', 0);

CREATE TABLE `sbc-customer`.`live_room` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `room_id` bigint(20) DEFAULT NULL COMMENT '直播房间id',
  `name` varchar(255) DEFAULT NULL COMMENT '直播房间名',
  `recommend` tinyint(4) DEFAULT NULL COMMENT '是否推荐0不推荐 1推荐',
  `start_time` datetime DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '结束时间',
  `anchor_name` varchar(255) DEFAULT NULL COMMENT '主播昵称',
  `anchor_wechat` varchar(255) DEFAULT NULL COMMENT '主播微信',
  `cover_img` varchar(255) DEFAULT NULL COMMENT '直播背景墙',
  `share_img` varchar(255) DEFAULT NULL COMMENT '分享卡片封面',
  `live_status` tinyint(7) DEFAULT NULL COMMENT '直播状态 0: 直播中,1: 暂停中, 2: 异常,3: 未开始, 4: 已结束, 5: 禁播, , 6: 已过期',
  `type` tinyint(4) DEFAULT NULL COMMENT '直播类型，1：推流，0：手机直播',
  `screen_type` tinyint(4) DEFAULT NULL COMMENT '1：横屏，0：竖屏',
  `close_like` tinyint(4) DEFAULT NULL COMMENT '1：关闭点赞 0：开启点赞，关闭后无法开启',
  `close_goods` tinyint(255) DEFAULT NULL COMMENT '1：关闭货架 0：打开货架，关闭后无法开启',
  `close_comment` tinyint(255) DEFAULT NULL COMMENT '1：关闭评论 0：打开评论，关闭后无法开启',
  `store_id` bigint(20) DEFAULT NULL COMMENT '店铺id',
  `live_company_id` varchar(255) DEFAULT NULL COMMENT '直播商户id',
  `create_person` varchar(255) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_person` varchar(255) DEFAULT NULL COMMENT '修改人',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `delete_person` varchar(255) DEFAULT NULL COMMENT '删除人',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  `del_flag` tinyint(4) DEFAULT NULL COMMENT '删除标识,0:未删除1:已删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=224 DEFAULT CHARSET=utf8mb4;


CREATE TABLE `sbc-customer`.`live_company` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `submit_time` datetime DEFAULT NULL COMMENT '提交审核时间',
  `live_broadcast_status` tinyint(5) DEFAULT NULL COMMENT '直播状态 0未开通，1待审核，2已开通，3审核未通过，4禁用中',
  `audit_reason` varchar(255) DEFAULT NULL COMMENT '直播审核原因',
  `create_person` varchar(45) CHARACTER SET utf8 DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_person` varchar(255) DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `delete_person` varchar(255) DEFAULT NULL COMMENT '删除人',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  `del_flag` tinyint(2) NOT NULL COMMENT '删除标识,0:未删除1:已删除',
  `company_info_id` bigint(20) DEFAULT NULL COMMENT '公司信息ID',
  `store_id` bigint(20) DEFAULT NULL COMMENT '店铺id',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `store_id` (`store_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1788 DEFAULT CHARSET=utf8mb4 COMMENT='公司信息';


CREATE TABLE `sbc-customer`.`live_room_replay` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `expire_time` datetime DEFAULT NULL COMMENT '视频过期时间',
  `create_time` datetime DEFAULT NULL COMMENT '视频创建时间',
  `media_url` varchar(255) DEFAULT NULL COMMENT '视频回放路径',
  `room_id` bigint(11) DEFAULT NULL COMMENT '直播房间id',
  `create_person` varchar(255) DEFAULT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `update_person` varchar(255) DEFAULT NULL COMMENT '修改人',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  `delete_person` varchar(255) DEFAULT NULL COMMENT '删除人',
  `del_flag` tinyint(2) DEFAULT NULL COMMENT '删除逻辑 0：未删除 1 已删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=90 DEFAULT CHARSET=utf8mb4;


CREATE TABLE `sbc-goods`.`live_goods` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `goods_id` bigint(32) DEFAULT NULL COMMENT '微信端商品id',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '商品标题',
  `cover_img_url` varchar(255) DEFAULT NULL COMMENT '填入mediaID',
  `price_type` tinyint(3) DEFAULT NULL COMMENT '价格类型，1：一口价，2：价格区间，3：显示折扣价',
  `price` decimal(10,2) DEFAULT NULL COMMENT '直播商品价格左边界',
  `price2` decimal(10,2) DEFAULT NULL COMMENT '直播商品价格右边界',
  `url` varchar(255) DEFAULT NULL COMMENT '商品详情页的小程序路径',
  `stock` bigint(10) DEFAULT NULL COMMENT '库存',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `goods_info_id` varchar(255) DEFAULT NULL COMMENT '商品详情id',
  `store_id` bigint(20) DEFAULT NULL COMMENT '店铺标识',
  `submit_time` datetime DEFAULT NULL COMMENT '提交审核时间',
  `audit_id` bigint(20) DEFAULT NULL COMMENT '审核单ID',
  `audit_status` tinyint(4) NOT NULL COMMENT '审核状态,0：代提审 1:待审核 2：审核通过 3：审核失败',
  `audit_reason` varchar(255) DEFAULT NULL COMMENT '审核原因',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  `delete_person` varchar(255) DEFAULT NULL COMMENT '删除人',
  `del_flag` tinyint(2) NOT NULL COMMENT '删除标记 0:未删除1:已删除',
  `third_party_tag` tinyint(2) DEFAULT NULL COMMENT '1,2：表示是为api添加商品，否则是在MP添加商品',
  `create_person` varchar(255) DEFAULT NULL COMMENT '创建人',
  `update_person` varchar(255) DEFAULT NULL COMMENT '修改人',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `product_id_UNIQUE` (`goods_id`)
) ENGINE=InnoDB AUTO_INCREMENT=904 DEFAULT CHARSET=utf8 COMMENT='SPU表';


CREATE TABLE `sbc-order`.`pick_up_record` (
  `pick_up_id` varchar(32) NOT NULL COMMENT '主键',
  `store_id` bigint(20) NOT NULL COMMENT '店铺id',
  `trade_id` varchar(32) NOT NULL COMMENT '订单id',
  `pick_up_code` varchar(6) NOT NULL COMMENT '自提码',
  `pick_up_flag` tinyint(4) NOT NULL COMMENT '是否已自提:0:未自提 1：已自提',
  `pick_up_time` datetime DEFAULT NULL COMMENT '自提时间',
  `contact_phone` varchar(20) NOT NULL COMMENT '手机号',
  `del_flag` tinyint(4) NOT NULL COMMENT '删除标志位:0:未删除1：已删除',
  `create_time` datetime NOT NULL COMMENT '创建表时间',
  PRIMARY KEY (`pick_up_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


alter table `sbc-goods`.`ware_house` add column self_erp_id varchar (10) default null COMMENT '仓库自身的id';

alter table `sbc-customer`.`company_info` add column erp_id varchar (10) default null comment '商家在erp里的编号';
alter table `sbc-customer`.`store` add column erp_id varchar (10) default null comment '商家在erp里的编号';


alter table `sbc-account`.`settlement` add column store_type tinyint (4) default null comment '商家类型0:供应商 1：商家';
alter table `sbc-account`.`settlement` add column provider_price  decimal(20,2) default null comment '供应商的金额';


INSERT INTO `sbc-message`.`push_send_node` (`node_name`, `node_type`, `node_code`, `node_title`, `node_context`, `expected_send_count`, `actually_send_count`, `open_count`, `status`, `del_flag`, `create_person`, `create_time`, `update_person`, `update_time`) VALUES ('会员自提订单提货短信通知', '2', 'CUSTOMER_PICK_UP_RECEIVE', '会员自提订单提货短信通知', '您的订单号为：{订单号}的订单提货成功，请保管好您的物品', '0', '0', '0', '1', '0', NULL, NULL, NULL, '2020-09-28 19:32:45');
INSERT INTO `sbc-message`.`push_send_node` (`node_name`, `node_type`, `node_code`, `node_title`, `node_context`, `expected_send_count`, `actually_send_count`, `open_count`, `status`, `del_flag`, `create_person`, `create_time`, `update_person`, `update_time`) VALUES ('会员自提订单支付短信通知', '2', 'CUSTOMER_PICK_UP_PAY_SUCCESS', '会员自提订单支付短信通知', '您的订单号为：{订单号}订单正在积极备货中，提供提货码：{自提码}', '0', '0', '0', '1', '0', NULL, NULL, '2c8080815cd3a74a015cd3ae86850001', '2020-09-28 10:21:48');
INSERT INTO `sbc-message`.`push_send_node` (`node_name`, `node_type`, `node_code`, `node_title`, `node_context`, `expected_send_count`, `actually_send_count`, `open_count`, `status`, `del_flag`, `create_person`, `create_time`, `update_person`, `update_time`) VALUES ('会员自提订单发货回传短信通知', '2', 'CUSTOMER_PICK_UP_CODE', '会员自提订单发货回传短信通知', '您的订单号为：{订单号}订单备货完成，请您尽快上门提货！提货时请提供提货码： {自提码}', '0', '0', '0', '1', '0', NULL, NULL, NULL, '2020-09-28 19:49:20');


INSERT INTO `sbc-message`.`message_send_node` (`node_name`, `node_title`, `node_content`, `status`, `create_time`, `create_person`, `update_time`, `update_person`, `del_flag`, `send_sum`, `open_sum`, `route_name`, `node_type`, `node_code`) VALUES ('会员自提订单提货短信通知', '会员自提订单提货短信通知', '您的订单号为：{订单号}的订单提货成功，请保管好您的物品', '1', '2020-09-28 19:25:00', NULL, NULL, NULL, '0', '0', '0', NULL, '2', 'CUSTOMER_PICK_UP_RECEIVE');
INSERT INTO `sbc-message`.`message_send_node` (`node_name`, `node_title`, `node_content`, `status`, `create_time`, `create_person`, `update_time`, `update_person`, `del_flag`, `send_sum`, `open_sum`, `route_name`, `node_type`, `node_code`) VALUES ('会员自提订单支付短信通知', '会员自提订单支付短信通知', '您的订单号为：{订单号}订单正在积极备货中，提供提货码：{自提码}', '1', '2020-09-28 17:27:30', NULL, NULL, NULL, '0', '0', '0', NULL, '2', 'CUSTOMER_PICK_UP_PAY_SUCCESS');
INSERT INTO `sbc-message`.`message_send_node` (`node_name`, `node_title`, `node_content`, `status`, `create_time`, `create_person`, `update_time`, `update_person`, `del_flag`, `send_sum`, `open_sum`, `route_name`, `node_type`, `node_code`) VALUES ('会员自提订单发货回传短信通知', '会员自提订单发货回传短信通知', '您的订单号为：{订单号}订单备货完成，请您尽快上门提货！提货时请提供提货码： {自提码}', '1', '2020-09-28 19:44:43', NULL, NULL, NULL, '0', '0', '0', NULL, '2', 'CUSTOMER_PICK_UP_CODE');


INSERT INTO `sbc-message`.`sms_template` (`template_name`, `template_content`, `remark`, `template_type`, `review_status`, `template_code`, `review_reason`, `sms_setting_id`, `del_flag`, `create_time`, `business_type`, `purpose`, `sign_id`, `open_flag`) VALUES ('会员自提订单提货短信通知', '您的订单号为：${trade}的订单提货成功，请保管好您的物品', '业务通知需要申请', '1', '3', NULL, NULL, NULL, '0', '2020-09-28 19:27:21', 'CUSTOMER_PICK_UP_RECEIVE', '会员自提订单提货短信通知', '48', '1');
INSERT INTO `sbc-message`.`sms_template` (`template_name`, `template_content`, `remark`, `template_type`, `review_status`, `template_code`, `review_reason`, `sms_setting_id`, `del_flag`, `create_time`, `business_type`, `purpose`, `sign_id`, `open_flag`) VALUES ('会员自提订单支付短信通知', '您的订单号为：${trade}订单正在积极备货中，提供提货码：${code}', '业务通知需要申请', '1', '3', NULL, NULL, NULL, '0', '2020-09-28 10:25:40', 'CUSTOMER_PICK_UP_PAY_SUCCESS', '会员自提订单支付短信通知', '48', '1');
INSERT INTO `sbc-message`.`sms_template` (`template_name`, `template_content`, `remark`, `template_type`, `review_status`, `template_code`, `review_reason`, `sms_setting_id`, `del_flag`, `create_time`, `business_type`, `purpose`, `sign_id`, `open_flag`) VALUES ('会员自提订单发货回传短信通知', '您的订单号为：${trade}订单备货完成，请您尽快上门提货！提货时请提供提货码： ${code}', '业务通知需要申请', '1', '3', NULL, NULL, NULL, '0', '2020-09-28 19:46:56', 'CUSTOMER_PICK_UP_CODE', '会员自提订单发货回传短信通知', '48', '1');

---商品详情添加保质期展示
ALTER TABLE `sbc-goods`.`standard_sku` ADD COLUMN `shelflife`  smallint(20) NULL COMMENT '保质期'  ;
ALTER TABLE `sbc-goods`.`goods_info` ADD COLUMN `shelflife`  smallint(20) COMMENT '保质期'  ;