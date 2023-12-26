DROP TABLE IF EXISTS `store_goods_unit`;
CREATE TABLE `store_goods_unit` (
 `store_goods_unit_id` int(11) NOT NULL,
 `status` bigint(2) DEFAULT NULL COMMENT '状态 0 启用 1 禁用',
 `unit` varchar(50) DEFAULT NULL COMMENT '单位',
 `del_flag` bigint(2) DEFAULT NULL COMMENT '删除标志',
 `company_info_id` int(11) DEFAULT NULL COMMENT '商家Id',
 `create_time` datetime DEFAULT NULL COMMENT '创建时间',
 `update_time` datetime DEFAULT NULL,
 `create_person` varchar(32) DEFAULT NULL COMMENT '创建人',
 `update_person` varchar(32) DEFAULT NULL COMMENT '修改人',
 PRIMARY KEY (`store_goods_unit_id`),
 UNIQUE KEY `company_info_unit` (`unit`,`company_info_id`) USING BTREE COMMENT '公司对应单位不能重复',
 KEY `company_info_id` (`company_info_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商户商品单位';
