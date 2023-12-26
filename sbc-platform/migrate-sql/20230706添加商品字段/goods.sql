
ALTER TABLE `goods`
    ADD COLUMN `good_date`  datetime NULL COMMENT '生产时间' AFTER `ware_id`;

ALTER TABLE `goods_info`
    ADD COLUMN `host_sku`  int(2) NULL DEFAULT 0 COMMENT '主sku （1表示设置主sku）' AFTER `parent_goods_info_id`;

ALTER TABLE `devanning_goods_info`
    ADD COLUMN `host_sku`  int(2) NULL COMMENT '主sku （1表示设置主sku）' AFTER `is_surprise_price`;

ALTER TABLE `goods_info`
DROP INDEX `erp_goods_info_no_UNIQUE` ,
ADD UNIQUE INDEX `erp_goods_info_no_UNIQUE` (`erp_goods_info_no`, `del_flag`, `store_id`) USING BTREE ;






