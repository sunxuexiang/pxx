
DROP TABLE IF EXISTS `goods_attribute`;
CREATE TABLE `goods_attribute` (
`attribute_id` varchar(32) NOT NULL,
`status` bigint(2) DEFAULT NULL COMMENT '状态 0 启用 1 禁用',
`attribute` varchar(50) DEFAULT NULL COMMENT '属性',
`del_flag` bigint(2) DEFAULT NULL COMMENT '删除标志',
`create_time` datetime DEFAULT NULL COMMENT '创建时间',
`update_time` datetime DEFAULT NULL,
PRIMARY KEY (`attribute_id`),
UNIQUE KEY `good_attribute_key` (`attribute`) USING BTREE COMMENT '名字唯一'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品属性表';

DROP TABLE IF EXISTS `goods_attribute_key`;
CREATE TABLE `goods_attribute_key` (
 `goods_attribute_key_id` int(12) NOT NULL AUTO_INCREMENT,
 `goods_attribute_id` varchar(32) DEFAULT NULL COMMENT '属性id',
 `goods_attribute_value` varchar(200) DEFAULT NULL COMMENT '属性名称',
 `goods_id` varchar(32) DEFAULT NULL COMMENT 'spuId',
 `goods_info_id` varchar(32) DEFAULT NULL COMMENT '商品ID',
 PRIMARY KEY (`goods_attribute_key_id`),
 KEY `goods_info_id_index` (`goods_info_id`) USING BTREE COMMENT 'sku 查询索引',
 KEY `goods_id_index` (`goods_id`) USING BTREE COMMENT 'spu 查询索引',
 KEY `goods_attribute_id_indx` (`goods_attribute_id`) USING BTREE COMMENT '主表信息',
 KEY `goods_attribute_index` (`goods_attribute_id`,`goods_info_id`) USING BTREE COMMENT '查询名称'
) ENGINE=InnoDB AUTO_INCREMENT=86 DEFAULT CHARSET=utf8mb4 COMMENT='商品属性关联关系表';





ALTER TABLE `goods`
    ADD COLUMN `good_date`  datetime NULL COMMENT '生产时间' AFTER `ware_id`;

ALTER TABLE `goods_info`
    ADD COLUMN `host_sku`  int(2) NULL DEFAULT 0 COMMENT '主sku （1表示设置主sku）' AFTER `parent_goods_info_id`;



