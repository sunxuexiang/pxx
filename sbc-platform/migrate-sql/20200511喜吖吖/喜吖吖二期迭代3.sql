-- ***************************************** marketing begin ***********************************************
alter table `sbc-marketing`.marketing add is_overlap tinyint not null comment '是否叠加（0：否，1：是）';
-- ***************************************** marketing end ***********************************************

---从标品拷过来的页面投放权限
INSERT INTO `menu_info`(`menu_id`, `system_type_cd`, `parent_menu_id`, `menu_grade`, `menu_name`, `menu_url`, `menu_icon`, `sort`, `create_time`, `del_flag`) VALUES ('ff8080816c6f1aff016c83d84cba0003', 3, 'ff8080816c6f1aff016c83d5a5f30001', 3, '页面管理', '/page-manage/weixin', NULL, 10, '2019-08-12 11:21:26', 0);
INSERT INTO `sbc-setting`.`function_info`(`function_id`, `system_type_cd`, `menu_id`, `function_title`, `function_name`, `remark`, `sort`, `create_time`, `del_flag`) VALUES ('ff80808171a672390171aaeb11dc0019', 3, 'ff8080816c6f1aff016c83d84cba0003', '页面投放', 'page-extend', NULL, 1, '2020-04-24 14:41:04', 0);
INSERT INTO `sbc-setting`.`function_info`(`function_id`, `system_type_cd`, `menu_id`, `function_title`, `function_name`, `remark`, `sort`, `create_time`, `del_flag`) VALUES ('ff80808171a672390171aae7e5ff0013', 4, 'fc8e1b223fe311e9828800163e0fc468', '页面投放', 'page-extend', NULL, 1, '2020-04-24 14:37:37', 0);

INSERT INTO `sbc-setting`.`authority`(`authority_id`, `system_type_cd`, `function_id`, `authority_title`, `authority_name`, `authority_url`, `request_type`, `remark`, `sort`, `create_time`, `del_flag`) VALUES ('ff80808171a672390171aaeba78c001c', 3, 'ff80808171a672390171aaeb11dc0019', '页面投放编辑', NULL, '/pageInfoExtend/modify', 'PUT', NULL, 2, '2020-04-24 14:41:43', 0);
INSERT INTO `sbc-setting`.`authority`(`authority_id`, `system_type_cd`, `function_id`, `authority_title`, `authority_name`, `authority_url`, `request_type`, `remark`, `sort`, `create_time`, `del_flag`) VALUES ('ff80808171a672390171aaeb63b1001a', 3, 'ff80808171a672390171aaeb11dc0019', '页面投放查看', NULL, '/pageInfoExtend/query', 'POST', NULL, 1, '2020-04-24 14:41:25', 0);
INSERT INTO `sbc-setting`.`authority`(`authority_id`, `system_type_cd`, `function_id`, `authority_title`, `authority_name`, `authority_url`, `request_type`, `remark`, `sort`, `create_time`, `del_flag`) VALUES ('ff80808171a672390171aae9627d0016', 4, 'ff80808171a672390171aae7e5ff0013', '页面投放编辑', NULL, '/pageInfoExtend/modify', 'PUT', NULL, 2, '2020-04-24 14:39:14', 0);
INSERT INTO `sbc-setting`.`authority`(`authority_id`, `system_type_cd`, `function_id`, `authority_title`, `authority_name`, `authority_url`, `request_type`, `remark`, `sort`, `create_time`, `del_flag`) VALUES ('ff80808171a672390171aae8e8e40014', 4, 'ff80808171a672390171aae7e5ff0013', '页面投放查看', NULL, '/pageInfoExtend/query', 'POST', NULL, 1, '2020-04-24 14:38:43', 0);

---商品表和商品品牌表添加排序序号字段
ALTER TABLE `sbc-goods`.`goods_brand` ADD COLUMN `brand_seq_num` INT DEFAULT NULL COMMENT '品牌排序序号';
ALTER TABLE `sbc-goods`.`goods` ADD COLUMN `goods_seq_num` INT DEFAULT NULL COMMENT '商品排序序号';

---优惠券字段
ALTER TABLE `sbc-marketing`.`coupon_info` ADD COLUMN `prompt`  varchar(20) NULL COMMENT '优惠券文案' AFTER `coupon_type`;
---魔方查询优惠券权限
INSERT INTO `sbc-setting`.`authority` (`authority_id`, `system_type_cd`, `function_id`, `authority_title`, `authority_name`, `authority_url`, `request_type`, `remark`, `sort`, `create_time`, `del_flag`) VALUES ('ff80808176b669f40176b6d97e6b0000', '4', 'fc9288f13fe311e9828800163e0fc468', '领券中心', NULL, '/xsite/front/center', 'POST', NULL, '7', '2020-12-31 11:31:16', '0');

---商品品牌导出权限
INSERT INTO `sbc-setting`.`function_info` VALUES ('ff80808176c9a1940176cb225a940000', 4, 'fc8df3623fe311e9828800163e0fc468', '商品品牌导出', 'f_goods_brand_export', '商品品牌', 4, '2021-1-4 10:03:15', 0);
INSERT INTO `sbc-setting`.`authority` VALUES ('ff80808176c9a1940176cb2397710001', 4, 'ff80808176c9a1940176cb225a940000', '导出品牌', NULL, '/goods/brand/export/params/*', 'GET', '商品品牌', 1, '2021-1-4 10:04:36', 0);

---商品列表及商品排序编辑权限设置
INSERT INTO `sbc-setting`.`function_info` VALUES ('ff80808176cb8def0176cd02d4c00004', 4, 'fc8df25c3fe311e9828800163e0fc468', '批量导出', 'f_goods_export', '商品列表', 7, '2021-1-4 18:48:04', 0);
INSERT INTO `sbc-setting`.`function_info` VALUES ('ff80808176cb8def0176cd03d8f40006', 4, 'fc8df25c3fe311e9828800163e0fc468', '编辑排序', 'f_goods_edit_sort', '商品列表', 8, '2021-1-4 18:49:10', 0);
INSERT INTO `sbc-setting`.`authority` VALUES ('ff80808176cb8def0176cd0377920005', 4, 'ff80808176cb8def0176cd02d4c00004', '商品批量导出', NULL, '/goods/spu/export/params/*', 'GET', '商品列表', 1, '2021-1-4 18:48:45', 0);
INSERT INTO `sbc-setting`.`authority` VALUES ('ff80808176cb8def0176cd04574d0007', 4, 'ff80808176cb8def0176cd03d8f40006', '编辑商品序号', NULL, '/goods/spu/modifySeqNum', 'PUT', '商品列表', 1, '2021-1-4 18:49:42', 0);

--品牌排序导入，商品排序导入
INSERT INTO `sbc-setting`.function_info (function_id, system_type_cd, menu_id, function_title, function_name, remark, sort, create_time, del_flag) VALUES ('ff80808176cb8def0176cc8e7d2f0003', 4, 'fc8df3623fe311e9828800163e0fc468', '商品品牌导入', 'f_brand_sort_import', null, 5, now(), 0);
INSERT INTO `sbc-setting`.function_info (function_id, system_type_cd, menu_id, function_title, function_name, remark, sort, create_time, del_flag) VALUES ('ff80808176cb8def0176cc8c533b0002', 4, 'fc8df25c3fe311e9828800163e0fc468', '商品排序导入', 'f_goods_sort_import', null, 6, now(), 0);
INSERT INTO `sbc-setting`.authority (authority_id, system_type_cd, function_id, authority_title, authority_name, authority_url, request_type, remark, sort, create_time, del_flag) VALUES ('ff808081783745d701783b0e8b7a0007', 4, 'ff80808176cb8def0176cc8e7d2f0003', '下载品牌导入错误模板', null, '/goods/brand/sort/excel/err/*/*', 'GET', null, 4, '2021-03-16 20:41:52', 0);
INSERT INTO `sbc-setting`.authority (authority_id, system_type_cd, function_id, authority_title, authority_name, authority_url, request_type, remark, sort, create_time, del_flag) VALUES ('ff808081783745d701783b0d57ef0006', 4, 'ff80808176cb8def0176cc8c533b0002', '下载排序导入错误模板', null, '/goods/sort/excel/err/*/*', 'GET', null, 4, '2021-03-16 20:40:34', 0);
INSERT INTO `sbc-setting`.authority (authority_id, system_type_cd, function_id, authority_title, authority_name, authority_url, request_type, remark, sort, create_time, del_flag) VALUES ('ff808081783745d701783b08e6ce0005', 4, 'ff80808176cb8def0176cc8e7d2f0003', '品牌导入', null, '/goods/brand/sort/import/*', 'GET', null, 3, '2021-03-16 20:35:42', 0);
INSERT INTO `sbc-setting`.authority (authority_id, system_type_cd, function_id, authority_title, authority_name, authority_url, request_type, remark, sort, create_time, del_flag) VALUES ('ff808081783745d701783b08587a0004', 4, 'ff80808176cb8def0176cc8e7d2f0003', '上传品牌导入模板', null, '/goods/brand/sort/excel/upload', 'POST', null, 2, '2021-03-16 20:35:06', 0);
INSERT INTO `sbc-setting`.authority (authority_id, system_type_cd, function_id, authority_title, authority_name, authority_url, request_type, remark, sort, create_time, del_flag) VALUES ('ff808081783745d701783b07a2440003', 4, 'ff80808176cb8def0176cc8e7d2f0003', '下载品牌导入模板', null, '/goods/brand/sort/excel/template/*', 'GET', null, 1, '2021-03-16 20:34:19', 0);
INSERT INTO `sbc-setting`.authority (authority_id, system_type_cd, function_id, authority_title, authority_name, authority_url, request_type, remark, sort, create_time, del_flag) VALUES ('ff808081783745d701783b0587540002', 4, 'ff80808176cb8def0176cc8c533b0002', '上传商品排序文件', null, '/goods/sort/excel/upload', 'POST', null, 2, '2021-03-16 20:32:01', 0);
INSERT INTO `sbc-setting`.authority (authority_id, system_type_cd, function_id, authority_title, authority_name, authority_url, request_type, remark, sort, create_time, del_flag) VALUES ('ff808081783745d701783b0431de0001', 4, 'ff80808176cb8def0176cc8c533b0002', '商品排序导入', null, '/goods/sort/import/*', 'GET', null, 3, '2021-03-16 20:30:34', 0);
INSERT INTO `sbc-setting`.authority (authority_id, system_type_cd, function_id, authority_title, authority_name, authority_url, request_type, remark, sort, create_time, del_flag) VALUES ('ff808081783745d701783b0375960000', 4, 'ff80808176cb8def0176cc8c533b0002', '下载商品排序模板', null, '/goods/sort/excel/template/*', 'GET', null, 1, '2021-03-16 20:29:46', 0);

---配送文案查询权限
INSERT INTO `sbc-setting`.`function_info` (`function_id`, `system_type_cd`, `menu_id`, `function_title`, `function_name`, `remark`, `sort`, `create_time`, `del_flag`) VALUES ('ff80808176fd215a0176ff80f7d10000', '4', 'ff808081741cb71101741e47d8be0000', '配送文案查询', 'f_homeDeliverySetting_list', '', '1', '2021-01-14 14:06:51', '0');
INSERT INTO `sbc-setting`.`authority` (`authority_id`, `system_type_cd`, `function_id`, `authority_title`, `authority_name`, `authority_url`, `request_type`, `remark`, `sort`, `create_time`, `del_flag`) VALUES ('ff80808176fd215a0176ff8137630001', '4', 'ff80808176fd215a0176ff80f7d10000', '配送文案查询', NULL, '/homedelivery/list', 'GET', NULL, '1', '2021-01-14 14:07:07', '0');

-- 搜索词 begin---
-- 热门搜索词表
CREATE TABLE `sbc-setting`.`popular_search_terms` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `popular_search_keyword` varchar(50) NOT NULL COMMENT '热门搜索词',
  `related_landing_page` varchar(200) DEFAULT NULL COMMENT '关联落地页',
  `sort_number` bigint(20) DEFAULT NULL COMMENT '排序号',
  `del_flag` tinyint(4) NOT NULL COMMENT '删除标记  0：正常，1：删除',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `create_person` varchar(32) DEFAULT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `update_person` varchar(32) DEFAULT NULL COMMENT '修改人',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  `delete_person` varchar(32) DEFAULT NULL COMMENT '删除人',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COMMENT='热门搜索词表';

-- 预置搜索词表
CREATE TABLE `sbc-setting`.`preset_search_terms` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `preset_search_keyword` varchar(50) NOT NULL COMMENT '预置搜索词字',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COMMENT='预置搜索词表';

-- 搜索词
CREATE TABLE `sbc-setting`.`search_associational_word` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `search_terms` varchar(50) NOT NULL COMMENT '搜索词',
  `del_flag` tinyint(4) NOT NULL COMMENT '删除标记  0：正常，1：删除',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `create_person` varchar(32) DEFAULT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `update_person` varchar(32) DEFAULT NULL COMMENT '修改人',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  `delete_person` varchar(32) DEFAULT NULL COMMENT '删除人',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COMMENT='搜索词表';

-- 联想长尾词
CREATE TABLE `sbc-setting`.`association_long_tail_word` (
  `association_long_tail_word_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `associational_word` varchar(50) DEFAULT NULL COMMENT '联想词',
  `long_tail_word` text COMMENT '长尾词',
  `search_associational_word_Id` bigint(20) DEFAULT NULL COMMENT '联想词Id',
  `sort_number` bigint(20) DEFAULT NULL COMMENT '排序号',
  `del_flag` tinyint(4) NOT NULL COMMENT '删除标记  0：正常，1：删除',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `create_person` varchar(32) DEFAULT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `update_person` varchar(32) DEFAULT NULL COMMENT '修改人',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  `delete_person` varchar(32) DEFAULT NULL COMMENT '删除人',
  PRIMARY KEY (`association_long_tail_word_id`),
  KEY `index_search_associational_word_Id` (`search_associational_word_Id`) USING BTREE
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COMMENT='联想长尾词表';

INSERT INTO `sbc-setting`.`menu_info`(`menu_id`, `system_type_cd`, `parent_menu_id`, `menu_grade`, `menu_name`, `menu_url`, `menu_icon`, `sort`, `create_time`, `del_flag`) VALUES ('ff8080817171f3f201717c69cb380002', 4, 'fc8e017c3fe311e9828800163e0fc468', 3, '搜索设置', '/search-manage', NULL, 145, '2020-04-15 13:57:20', 0);

ALTER TABLE `sbc-setting`.popular_search_terms ADD COLUMN `pc_landing_page` varchar(200) DEFAULT NULL COMMENT
'PC落地页' after `related_landing_page`;

INSERT INTO `sbc-setting`.`authority`(`authority_id`, `system_type_cd`, `function_id`, `authority_title`, `authority_name`, `authority_url`, `request_type`, `remark`, `sort`, `create_time`, `del_flag`) VALUES ('ff80808171a672390171aaf48ca70028', 4, 'ff80808171a672390171aaf0480b0025', '查询预置搜索词', '', '/preset_search_terms/list', 'POST', NULL, 2, '2020-04-24 14:51:26', 0);
INSERT INTO `sbc-setting`.`authority`(`authority_id`, `system_type_cd`, `function_id`, `authority_title`, `authority_name`, `authority_url`, `request_type`, `remark`, `sort`, `create_time`, `del_flag`) VALUES ('ff80808171a672390171aaf3441e0027', 4, 'ff80808171a672390171aaf0480b0025', '编辑预置搜索词', NULL, '/preset_search_terms/modify', 'POST', NULL, 1, '2020-04-24 14:50:02', 0);
INSERT INTO `sbc-setting`.`authority`(`authority_id`, `system_type_cd`, `function_id`, `authority_title`, `authority_name`, `authority_url`, `request_type`, `remark`, `sort`, `create_time`, `del_flag`) VALUES ('ff80808171a672390171aaf2469b0026', 4, 'ff80808171a672390171aaf0480b0025', '新增预置搜索词', NULL, '/preset_search_terms/add', 'POST', NULL, 0, '2020-04-24 14:48:57', 0);
INSERT INTO `sbc-setting`.`authority` (`authority_id`, `system_type_cd`, `function_id`, `authority_title`, `authority_name`, `authority_url`, `request_type`, `remark`, `sort`, `create_time`, `del_flag`) VALUES ('ff808081775efa50017761722d040000', '4', 'ff80808171a672390171aae8f1d50015', '搜索联想词列表新增', NULL, '/search_associational_word/add', 'POST', NULL, '3', '2021-02-02 14:33:29', '0');


INSERT INTO `sbc-setting`.`authority`(`authority_id`, `system_type_cd`, `function_id`, `authority_title`, `authority_name`, `authority_url`, `request_type`, `remark`, `sort`, `create_time`, `del_flag`) VALUES ('ff80808171a672390171aaef6d450023', 4, 'ff80808171a672390171aae8f1d50015', '删除联想词', NULL, '/search_associational_word/delete_associational_long_tail_word', 'POST', NULL, 6, '2020-04-24 14:45:50', 0);
INSERT INTO `sbc-setting`.`authority`(`authority_id`, `system_type_cd`, `function_id`, `authority_title`, `authority_name`, `authority_url`, `request_type`, `remark`, `sort`, `create_time`, `del_flag`) VALUES ('ff80808171a672390171aaee889a0022', 4, 'ff80808171a672390171aae8f1d50015', '联想词排序', NULL, '/search_associational_word/sort_associational_word', 'POST', NULL, 5, '2020-04-24 14:44:51', 0);
INSERT INTO `sbc-setting`.`authority`(`authority_id`, `system_type_cd`, `function_id`, `authority_title`, `authority_name`, `authority_url`, `request_type`, `remark`, `sort`, `create_time`, `del_flag`) VALUES ('ff80808171a672390171aaeda7e0001f', 4, 'ff80808171a672390171aae8f1d50015', '修改联想词', NULL, '/search_associational_word/modify_associational_word', 'POST', NULL, 4, '2020-04-24 14:43:54', 0);
INSERT INTO `sbc-setting`.`authority`(`authority_id`, `system_type_cd`, `function_id`, `authority_title`, `authority_name`, `authority_url`, `request_type`, `remark`, `sort`, `create_time`, `del_flag`) VALUES ('ff80808171a672390171aaece6a0001e', 4, 'ff80808171a672390171aae8f1d50015', '新增联想词', NULL, '/search_associational_word/add_associational_word', 'POST', NULL, 3, '2020-04-24 14:43:04', 0);
INSERT INTO `sbc-setting`.`authority`(`authority_id`, `system_type_cd`, `function_id`, `authority_title`, `authority_name`, `authority_url`, `request_type`, `remark`, `sort`, `create_time`, `del_flag`) VALUES ('ff80808171a672390171aaeb885e001b', 4, 'ff80808171a672390171aae8f1d50015', '删除搜索词', NULL, '/search_associational_word/delete_search_associational_word', 'POST', NULL, 2, '2020-04-24 14:41:35', 0);
INSERT INTO `sbc-setting`.`authority`(`authority_id`, `system_type_cd`, `function_id`, `authority_title`, `authority_name`, `authority_url`, `request_type`, `remark`, `sort`, `create_time`, `del_flag`) VALUES ('ff80808171a672390171aaea970f0018', 4, 'ff80808171a672390171aae8f1d50015', '修改搜索词', NULL, '/search_associational_word/modify', 'POST', NULL, 1, '2020-04-24 14:40:33', 0);

INSERT INTO `sbc-setting`.`authority`(`authority_id`, `system_type_cd`, `function_id`, `authority_title`, `authority_name`, `authority_url`, `request_type`, `remark`, `sort`, `create_time`, `del_flag`) VALUES ('ff80808171a672390171aae688550012', 4, 'ff80808171a672390171aae298ce000e', '热门搜索词排序', NULL, '/popular_search_terms/sort_popular_search_terms', 'POST', NULL, 3, '2020-04-24 14:36:07', 0);
INSERT INTO `sbc-setting`.`authority`(`authority_id`, `system_type_cd`, `function_id`, `authority_title`, `authority_name`, `authority_url`, `request_type`, `remark`, `sort`, `create_time`, `del_flag`) VALUES ('ff80808171a672390171aae545190011', 4, 'ff80808171a672390171aae298ce000e', '删除热门搜索词', NULL, '/popular_search_terms/delete', 'POST', NULL, 2, '2020-04-24 14:34:44', 0);
INSERT INTO `sbc-setting`.`authority`(`authority_id`, `system_type_cd`, `function_id`, `authority_title`, `authority_name`, `authority_url`, `request_type`, `remark`, `sort`, `create_time`, `del_flag`) VALUES ('ff80808171a672390171aae428080010', 4, 'ff80808171a672390171aae298ce000e', '编辑热门搜索词', NULL, '/popular_search_terms/modify', 'POST', NULL, 1, '2020-04-24 14:33:31', 0);
INSERT INTO `sbc-setting`.`authority`(`authority_id`, `system_type_cd`, `function_id`, `authority_title`, `authority_name`, `authority_url`, `request_type`, `remark`, `sort`, `create_time`, `del_flag`) VALUES ('ff80808171a672390171aae35a0d000f', 4, 'ff80808171a672390171aae298ce000e', '新增热门搜索词', NULL, '/popular_search_terms/add', 'POST', NULL, 0, '2020-04-24 14:32:39', 0);
INSERT INTO `sbc-setting`.`authority` (`authority_id`, `system_type_cd`, `function_id`, `authority_title`, `authority_name`, `authority_url`, `request_type`, `remark`, `sort`, `create_time`, `del_flag`) VALUES ('ff8080817759d3e701775b96e1090002', '4', 'ff80808171a672390171aae298ce000e', '热门搜索词查询', NULL, '/popular_search_terms/list', 'POST', NULL, '1', '2021-02-01 11:15:51', '0');
INSERT INTO `sbc-setting`.`authority` (`authority_id`, `system_type_cd`, `function_id`, `authority_title`, `authority_name`, `authority_url`, `request_type`, `remark`, `sort`, `create_time`, `del_flag`) VALUES ('ff8080817759d3e701775b961cfa0001', '4', 'ff80808171a672390171aae8f1d50015', '联想搜索词查询', NULL, '/search_associational_word/page', 'POST', NULL, '1', '2021-02-01 11:15:00', '0');


INSERT INTO `sbc-setting`.`function_info`(`function_id`, `system_type_cd`, `menu_id`, `function_title`, `function_name`, `remark`, `sort`, `create_time`, `del_flag`) VALUES ('ff80808171a672390171aae298ce000e', 4, 'ff8080817171f3f201717c69cb380002', '热门搜索词', 'f_popular_search_terms', NULL, 0, '2020-04-24 14:31:49', 0);
INSERT INTO `sbc-setting`.`function_info`(`function_id`, `system_type_cd`, `menu_id`, `function_title`, `function_name`, `remark`, `sort`, `create_time`, `del_flag`) VALUES ('ff80808171a672390171aaf0480b0025', 4, 'ff8080817171f3f201717c69cb380002', '预置搜索词', 'f_preset_search_terms', NULL, 2, '2020-04-24 14:46:46', 0);
INSERT INTO `sbc-setting`.`function_info`(`function_id`, `system_type_cd`, `menu_id`, `function_title`, `function_name`, `remark`, `sort`, `create_time`, `del_flag`) VALUES ('ff80808171a672390171aae8f1d50015', 4, 'ff8080817171f3f201717c69cb380002', '搜索词联想词', 'f_search_associational_word', NULL, 1, '2020-04-24 14:38:45', 0);



-- 搜索词 end---
