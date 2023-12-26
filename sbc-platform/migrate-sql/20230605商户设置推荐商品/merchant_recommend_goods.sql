DROP TABLE IF EXISTS `merchant_recommend_goods`;
CREATE TABLE `merchant_recommend_goods` (
  `merchant_recommend_id` varchar(32) CHARACTER SET utf8 NOT NULL COMMENT '商品推荐主键',
  `goods_info_id` varchar(32) CHARACTER SET utf8 NOT NULL COMMENT '推荐的商品编号',
  `store_id` bigint(11) DEFAULT NULL COMMENT '店铺id',
  `sort` bigint(5) DEFAULT '0' COMMENT '排序字段（越小越靠前）',
  `company_info_id` bigint(11) DEFAULT NULL COMMENT '商家ID',
  PRIMARY KEY (`merchant_recommend_id`),
  UNIQUE KEY `store_goods_index` (`goods_info_id`,`store_id`) USING BTREE,
  KEY `company_info_id` (`company_info_id`) USING BTREE COMMENT 'company_info_id 商户id'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商户推荐';