

-- ----------------------------
-- Table structure for merchant_recommend_type
-- ----------------------------
DROP TABLE IF EXISTS `merchant_recommend_type`;
CREATE TABLE `merchant_recommend_type` (
 `merchant_type_id` varchar(32) NOT NULL COMMENT '商户类型推荐主键',
 `store_cate_id` bigint(32) NOT NULL COMMENT '推荐的分类编号',
 `store_id` bigint(11) DEFAULT NULL COMMENT '店铺Id',
 `company_info_id` bigint(11) DEFAULT NULL COMMENT '商家Id',
 `sort` int(11) DEFAULT '0' COMMENT '排序',
 PRIMARY KEY (`merchant_type_id`) USING BTREE,
 UNIQUE KEY `store_cate_type_index` (`store_cate_id`,`store_id`) USING BTREE,
 KEY `company_info_id` (`company_info_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='商户类型推荐表';