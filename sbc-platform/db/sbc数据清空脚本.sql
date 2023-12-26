#本文件用于记录各库交付客户的时候,可以清空的数据,尤其是敏感信息必须要清理掉(比如ossId,secret,微信appId,appSecret)

#数据库：sbc-pay
#pay_channel_item;
#pay_gateway;
delete from pay_gateway_config;
INSERT INTO pay_gateway_config(id, gateway_id, create_time) VALUES (1, 1, now());
INSERT INTO pay_gateway_config(id, gateway_id, create_time) VALUES (2, 2, now());
INSERT INTO pay_gateway_config(id, gateway_id, create_time) VALUES (3, 3, now());
INSERT INTO pay_gateway_config(id, gateway_id, create_time) VALUES (4, 4, now());
INSERT INTO pay_gateway_config(id, gateway_id, create_time) VALUES (5, 5, now());
delete from pay_trade_record;


#数据库：sbc-setting
#authority;
#evaluate_ratio;
#function_info;
#menu_info;
delete from role_function_rela where role_info_id != 1;
delete from role_menu_rela where role_info_id != 1;
#sensitive_words;
delete from store_image;
#system_config;
UPDATE system_config SET context = '{\"appId\":\"\",\"appSecret\":\"\"}' WHERE config_type = 'small_program_setting_customer';
UPDATE system_config SET context = '{}' WHERE config_key = 'resource_server';
UPDATE system_config SET context = '{}' WHERE config_type = 'kuaidi100';
delete from system_email_config;
#system_growth_value_config
delete from system_image;
delete from system_ip_info;
delete from system_operation_log;
#system_switch;

delete from base_config;
delete from business_config;
delete from company_info;
delete from express_company;
delete from online_service;
delete from online_service_item;
delete from store_express_company_rela;
delete from store_resource;
delete from store_resource_cate where is_default != 1;
delete from sys_sms;
delete from system_resource;
delete from system_resource_cate where is_default != 1;
delete from wechat_login_set;
delete from wechat_share_set;


#数据库：sbc-customer
delete from company_info where company_info_id != 0;
delete from customer;
delete from customer_account;
delete from customer_delivery_address;
delete from customer_detail;
delete from customer_email;
delete from customer_growth_value;
delete from customer_invoice;
delete from customer_level where is_defalt != 1;
delete from customer_level_rights;
delete from customer_level_rights_rel;
delete from customer_points_detail;
delete from distribution_customer;
delete from distribution_customer_invite_info;
delete from distribution_customer_ranking;
delete from distribution_performance_day;
delete from distribution_performance_month;
delete from live_room;
delete from live_company;
delete from live_room_company;

delete from employee where account_name != 'system';
delete from invite_new_record;
delete from role_info where role_info_id != 1;
delete from store;
delete from store_consumer_statistics;
delete from store_customer_follow;
delete from store_customer_rela;
delete from store_evaluate;
delete from store_evaluate_num;
delete from store_evaluate_sum;
delete from store_level;
delete from third_login_relation;
truncate table distributor_level;

#数据库：sbc-account
delete from company_account;
delete from customer_draw_cash;
delete from customer_funds;
delete from customer_funds_detail;
delete from invoice_project where project_id != '00000000000000000000000000000000'; 
delete from invoice_project_switch;
delete from offline_account;
delete from reconciliation;
delete from settlement;


#数据库：sbc-goods
delete from check_brand;
delete from contract_brand;
delete from contract_cate;
delete from customer_goods_evaluate_praise;
delete from distributor_goods_info;
delete from flash_sale_cate;
delete from flash_sale_goods;
delete from freight_template_goods;
delete from freight_template_goods_express;
delete from freight_template_goods_free;
delete from freight_template_store;
delete from goods;
delete from goods_brand;
delete from goods_cate;
delete from goods_customer_num;
delete from goods_customer_price;
delete from goods_evaluate;
delete from goods_evaluate_image;
delete from goods_image;
delete from goods_info;
delete from goods_info_spec_detail_rel; 
delete from goods_interval_price;
delete from goods_level_price;
delete from goods_prop;
delete from goods_prop_detail;
delete from goods_prop_detail_rel;
delete from goods_spec; 
delete from goods_spec_detail; 
delete from goods_tab_rela;
delete from goods_tobe_evaluate;
delete from points_goods;
delete from points_goods_cate;
delete from standard_goods;
delete from standard_goods_rel;
delete from standard_image;
delete from standard_prop_detail_rel;
delete from standard_sku;
delete from standard_sku_spec_detail_rel;
delete from standard_spec;
delete from standard_spec_detail;
delete from store_cate;
delete from store_cate_goods_rela;
delete from store_goods_tab;
delete from store_tobe_evaluate;
truncate table groupon_goods_info;
delete from distribution_goods_matter;
delete from live_goods;
delete from live_room_live_goods_rel;


#数据库：sbc-marketing
delete from coupon_activity;
delete from coupon_activity_config;
delete from coupon_cate;
delete from coupon_cate_rela;
delete from coupon_code;
delete from coupon_info;
delete from coupon_marketing_scope;
delete from distribution_record;
delete from distribution_recruit_gift;
delete from distribution_reward_coupon;
delete from distribution_setting;
delete from distribution_store_setting;
delete from marketing;
delete from marketing_full_discount_level;
delete from marketing_full_gift_detail;
delete from marketing_full_gift_level;
delete from marketing_full_reduction_level;
delete from marketing_scope;
truncate table groupon_setting;
truncate table groupon_activity;
truncate table groupon_cate;
truncate table groupon_record;
delete from points_coupon;
truncate table coupon_marketing_customer_scope;

-- 批量删除优惠券券码分表数据
drop procedure if exists del_voucher_code_sub_table;

delimiter $
create procedure del_voucher_code_sub_table(tableCount  int)
begin
  declare i int;
  DECLARE table_name VARCHAR(20);
  DECLARE table_pre VARCHAR(20);
  DECLARE sql_text VARCHAR(20000);
  set i = 0;
  SET table_pre = 'coupon_code_';
  while i < tableCount  do
    SET @table_name = CONCAT(table_pre,CONCAT(i, ''));
    SET sql_text = CONCAT('truncate table ', @table_name, '');
    SELECT sql_text;
    SET @sql_text=sql_text;
    PREPARE stmt FROM @sql_text;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;
    set i = i + 1;
  end while;
end $

call del_voucher_code_sub_table(5);



#数据库：sbc-order
delete from distribution_task_temp;
delete from consume_record;
delete from goods_customer_follow;
delete from order_growth_value_temp;
delete from order_invoice;
delete from pay_order;
delete from purchase;
delete from receivable;
delete from refund_bill;
delete from refund_order;



#数据库：sbc-bff
truncate table distribution_task_temp;



