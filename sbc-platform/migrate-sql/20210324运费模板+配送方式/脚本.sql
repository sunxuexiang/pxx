-- 新增设置配送到家范围表
CREATE TABLE `sbc-goods`.freight_template_delivery_area
(
    id                    bigint(15)    NOT NULL AUTO_INCREMENT COMMENT '主键标识',
    destination_area      varchar(3000) NOT NULL COMMENT '配送地id(逗号分隔)',
    destination_area_name varchar(3000) NOT NULL COMMENT '配送地名称(逗号分隔)',
    store_id              bigint(20)    NOT NULL COMMENT '店铺标识',
    company_info_id       bigint(11)    NOT NULL COMMENT '公司信息ID',
    create_time           datetime               DEFAULT NULL COMMENT '创建时间',
    del_flag              tinyint(1)    NOT NULL DEFAULT '0' COMMENT '是否删除(0:否,1:是)',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 70
  DEFAULT CHARSET = utf8mb4 COMMENT ='设置配送到家范围';

-- 修改计价方式备注
alter table `sbc-goods`.freight_template_goods modify column valuation_type tinyint(1) comment '计价方式(0:按件数,1:按重量,2:按体积,3:按重量/件)';
alter table `sbc-goods`.freight_template_goods_express modify column valuation_type tinyint(1) comment '计价方式(0:按件数,1:按重量,2:按体积,3:按重量/件)';
alter table `sbc-goods`.freight_template_goods_free modify column valuation_type tinyint(1) comment '计价方式(0:按件数,1:按重量,2:按体积,3:按重量/件)';

-- 权限配置
INSERT INTO `sbc-setting`.menu_info (menu_id, system_type_cd, parent_menu_id, menu_grade, menu_name, menu_url, menu_icon, sort, create_time, del_flag) VALUES ('ff808081786ac5cc01786d7c2a580000', 3, 'fc8e51da3fe311e9828800163e0fc468', 3, '免费店配', '/home-delivery', null, 48, '2021-03-26 15:42:37', 0);
INSERT INTO `sbc-setting`.function_info (function_id, system_type_cd, menu_id, function_title, function_name, remark, sort, create_time, del_flag) VALUES ('ff808081786ac5cc01786d808e840001', 3, 'ff808081786ac5cc01786d7c2a580000', '配送范围设置', 'f_homeDelivery_save', null, 1, '2021-03-26 15:47:25', 0);
INSERT INTO `sbc-setting`.authority (authority_id, system_type_cd, function_id, authority_title, authority_name, authority_url, request_type, remark, sort, create_time, del_flag) VALUES ('ff808081786ac5cc01786d899ac20003', 3, 'ff808081786ac5cc01786d808e840001', '配送范围保存', '', '/freighttemplatedeliveryarea/save', 'POST', null, 1, '2021-03-26 15:57:18', 0);