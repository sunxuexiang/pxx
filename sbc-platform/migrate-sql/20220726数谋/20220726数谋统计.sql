ALTER TABLE `sbc-statistics`.`goods_total`
ADD COLUMN `ware_id`  bigint NULL AFTER `SALE_TOTAL`;
ALTER TABLE `sbc-statistics`.`goods_total_ratio_day`
ADD COLUMN `ware_id`  bigint NULL AFTER `CREATE_TIME`;
ALTER TABLE `sbc-statistics`.`goods_total_ratio_month`
ADD COLUMN `ware_id`  bigint NULL AFTER `CREATE_TIME`;
ALTER TABLE `sbc-statistics`.`goods_total_ratio_recent_seven`
ADD COLUMN `ware_id`  bigint NULL AFTER `CREATE_TIME`;
ALTER TABLE `sbc-statistics`.`goods_total_ratio_recent_thirty`
ADD COLUMN `ware_id`  bigint NULL AFTER `CREATE_TIME`;


ALTER TABLE `sbc-statistics`.`goods_brand_day`
ADD COLUMN `ware_id`  bigint NULL AFTER `CREATE_TM`;

ALTER TABLE `sbc-statistics`.`goods_brand_month`
ADD COLUMN `ware_id`  bigint NULL AFTER `CREATE_TM`;
ALTER TABLE `sbc-statistics`.`goods_brand_recent_seven`
ADD COLUMN `ware_id`  bigint NULL AFTER `CREATE_TM`;
ALTER TABLE `sbc-statistics`.`goods_brand_recent_thirty`
ADD COLUMN `ware_id`  bigint NULL AFTER `CREATE_TM`;


ALTER TABLE `sbc-statistics`.`goods_cate_day`
ADD COLUMN `ware_id`  bigint NULL AFTER `IS_LEAF`;

ALTER TABLE `sbc-statistics`.`goods_cate_month`
ADD COLUMN `ware_id`  bigint NULL AFTER `IS_LEAF`;

ALTER TABLE `sbc-statistics`.`goods_cate_recent_seven`
ADD COLUMN `ware_id`  bigint NULL AFTER `IS_LEAF`;
ALTER TABLE `sbc-statistics`.`goods_cate_recent_thirty`
ADD COLUMN `ware_id`  bigint NULL AFTER `IS_LEAF`;

ALTER TABLE `sbc-statistics`.`goods_store_cate_day`
ADD COLUMN `ware_id`  bigint NULL AFTER `IS_LEAF`;

ALTER TABLE `sbc-statistics`.`goods_store_cate_month`
ADD COLUMN `ware_id`  bigint NULL AFTER `IS_LEAF`;

ALTER TABLE `sbc-statistics`.`goods_store_cate_recent_seven`
ADD COLUMN `ware_id`  bigint NULL AFTER `IS_LEAF`;
ALTER TABLE `sbc-statistics`.`goods_store_cate_recent_thirty`
ADD COLUMN `ware_id`  bigint NULL AFTER `IS_LEAF`;


ALTER TABLE `sbc-goods`.`stockout_manage`
ADD COLUMN `stockout_day`  int NULL AFTER `goods_info_img`;

ALTER TABLE `sbc-goods`.`stockout_manage`
ADD COLUMN `replenishment_time`  datetime AFTER `stockout_day`;




