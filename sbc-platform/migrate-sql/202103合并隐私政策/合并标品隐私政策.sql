-- 创建隐私政策表
create table `sbc-setting`.`system_privacy_policy`
(
    privacy_policy_id  varchar(32) not null comment '隐私政策id'
        primary key,
    privacy_policy     mediumtext  null comment '隐私政策',
    privacy_policy_pop mediumtext  null comment '隐私政策弹窗',
    create_person      varchar(32) null comment '创建人',
    create_time        datetime    null comment '创建时间',
    update_person      varchar(32) null comment '修改人',
    update_time        datetime    null comment '修改时间',
    del_flag           tinyint     not null comment '是否删除标志 0：否，1：是'
);

-- 隐私权限
INSERT INTO `sbc-setting`.menu_info (menu_id, system_type_cd, parent_menu_id, menu_grade, menu_name, menu_url, menu_icon, sort, create_time, del_flag) VALUES ('2c93a4e27817095e01781710e8670000', 4, 'fc8e43953fe311e9828800163e0fc468', 3, '隐私政策', '/privacy-policy-setting', null, 3, '2021-03-09 20:58:07', 0);
INSERT INTO `sbc-setting`.function_info (function_id, system_type_cd, menu_id, function_title, function_name, remark, sort, create_time, del_flag) VALUES ('2c93a4e27817095e01781712eb480002', 4, '2c93a4e27817095e01781710e8670000', '隐私政策编辑', 'f_privacy_policy_edit', null, 2, '2021-03-09 21:00:19', 0);
INSERT INTO `sbc-setting`.function_info (function_id, system_type_cd, menu_id, function_title, function_name, remark, sort, create_time, del_flag) VALUES ('2c93a4e27817095e0178171210520001', 4, '2c93a4e27817095e01781710e8670000', '隐私政策查看', 'f_privacy_policy', null, 1, '2021-03-09 20:59:23', 0);
INSERT INTO `sbc-setting`.authority (authority_id, system_type_cd, function_id, authority_title, authority_name, authority_url, request_type, remark, sort, create_time, del_flag) VALUES ('2c93a4e27817095e0178171741390004', 4, '2c93a4e27817095e01781712eb480002', '隐私政策编辑', null, '/boss/systemprivacypolicy', 'POST', null, 2, '2021-03-09 21:05:03', 0);
INSERT INTO `sbc-setting`.authority (authority_id, system_type_cd, function_id, authority_title, authority_name, authority_url, request_type, remark, sort, create_time, del_flag) VALUES ('2c93a4e27817095e0178171680c80003', 4, '2c93a4e27817095e0178171210520001', '隐私政策查看', null, '/boss/systemprivacypolicy', 'GET', null, 1, '2021-03-09 21:04:14', 0);