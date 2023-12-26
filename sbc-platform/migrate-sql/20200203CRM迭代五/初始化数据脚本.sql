-- sbc_message start----------
-- 先将原先非验证码的模板移至通知类
use `sbc-message`;
ALTER TABLE `sms_template`
MODIFY COLUMN `business_type` varchar(40) NULL DEFAULT NULL COMMENT '业务类型' AFTER `create_time`,
ADD COLUMN `open_flag` tinyint(4) NULL DEFAULT 1 COMMENT '开关标识, 0:关,1:开';

update sms_template set  template_type = 1 where business_type in ('CUSTOMER_PASSWORD','EMPLOYEE_PASSWORD','CUSTOMER_IMPORT_SUCCESS');
-- 通知模板
INSERT INTO `sms_template`(`template_name`, `template_content`, `remark`, `template_type`, `review_status`, `template_code`, `review_reason`, `sms_setting_id`, `del_flag`, `create_time`, `business_type`, `purpose`, `sign_id`) VALUES ('订单提交成功通知模板', '订单${name}已经提交成功，请及时付款哦～', '业务通知需要申请', 1, 3, NULL, '', NULL, 0, '2020-02-03 16:00:00', 'ORDER_COMMIT_SUCCESS', '订单提交成功通知', NULL);
INSERT INTO `sms_template`(`template_name`, `template_content`, `remark`, `template_type`, `review_status`, `template_code`, `review_reason`, `sms_setting_id`, `del_flag`, `create_time`, `business_type`, `purpose`, `sign_id`) VALUES ('订单提交成功审核通知模板', '订单${name}已经提交成功，该订单需要商家审核，请耐心等待哦~', '业务通知需要申请', 1, 3, NULL, '', NULL, 0, '2020-02-03 16:00:00', 'ORDER_COMMIT_SUCCESS_CHECK', '订单提交成功审核通知', NULL);
INSERT INTO `sms_template`(`template_name`, `template_content`, `remark`, `template_type`, `review_status`, `template_code`, `review_reason`, `sms_setting_id`, `del_flag`, `create_time`, `business_type`, `purpose`, `sign_id`) VALUES ('订单审核通过通知模板', '订单${name}已通过商家审核，请及时付款，别错过哦~', '业务通知需要申请', 1, 3, NULL, '', NULL, 0, '2020-02-03 16:00:00', 'ORDER_CHECK_PASS', '订单审核通过通知', NULL);
INSERT INTO `sms_template`(`template_name`, `template_content`, `remark`, `template_type`, `review_status`, `template_code`, `review_reason`, `sms_setting_id`, `del_flag`, `create_time`, `business_type`, `purpose`, `sign_id`) VALUES ('订单审核未通过通知模板', '订单${name}未通过商家审核，原因是：${remark}', '订单审核未通过的通知，remark是审核未通过的原因内容', 1, 3, NULL, '', NULL, 0, '2020-02-03 16:00:00', 'ORDER_CHECK_NOT_PASS', '订单审核未通过通知', NULL);
INSERT INTO `sms_template`(`template_name`, `template_content`, `remark`, `template_type`, `review_status`, `template_code`, `review_reason`, `sms_setting_id`, `del_flag`, `create_time`, `business_type`, `purpose`, `sign_id`) VALUES ('订单支付成功通知模板', '订单${name}支付成功，我们将尽快为您安排发货哦~', '业务通知需要申请', 1, 3, NULL, '', NULL, 0, '2020-02-03 16:00:00', 'ORDER_PAY_SUCCESS', '订单支付成功通知', NULL);
INSERT INTO `sms_template`(`template_name`, `template_content`, `remark`, `template_type`, `review_status`, `template_code`, `review_reason`, `sms_setting_id`, `del_flag`, `create_time`, `business_type`, `purpose`, `sign_id`) VALUES ('订单发货通知模板', '订单${name}已发货，请注意物流进度哦~', '业务通知需要申请', 1, 3, NULL, '', NULL, 0, '2020-02-03 16:00:00', 'ORDER_SEND_GOODS', '订单发货通知', NULL);
INSERT INTO `sms_template`(`template_name`, `template_content`, `remark`, `template_type`, `review_status`, `template_code`, `review_reason`, `sms_setting_id`, `del_flag`, `create_time`, `business_type`, `purpose`, `sign_id`) VALUES ('订单完成通知模板', '订单${name}已完成，期待您分享商品评价与购物心得哦~', '业务通知需要申请', 1, 3, NULL, '', NULL, 0, '2020-02-03 16:00:00', 'ORDER_COMPILE', '订单完成通知', NULL);
INSERT INTO `sms_template`(`template_name`, `template_content`, `remark`, `template_type`, `review_status`, `template_code`, `review_reason`, `sms_setting_id`, `del_flag`, `create_time`, `business_type`, `purpose`, `sign_id`) VALUES ('商品评价提醒模板', '商品${name}还没有收到您的评价呢，期待您与我们分享哦~', '业务通知需要申请', 1, 3, NULL, '', NULL, 0, '2020-02-03 16:00:00', 'GOODS_EVALUATION', '商品评价提醒', NULL);
INSERT INTO `sms_template`(`template_name`, `template_content`, `remark`, `template_type`, `review_status`, `template_code`, `review_reason`, `sms_setting_id`, `del_flag`, `create_time`, `business_type`, `purpose`, `sign_id`) VALUES ('服务评价提醒模板', '订单${name}还没有收到您的服务评价呢，期待您与我们分享哦~', '业务通知需要申请', 1, 3, NULL, '', NULL, 0, '2020-02-03 16:00:00', 'SERVICE_EVALUATION', '服务评价提醒', NULL);
INSERT INTO `sms_template`(`template_name`, `template_content`, `remark`, `template_type`, `review_status`, `template_code`, `review_reason`, `sms_setting_id`, `del_flag`, `create_time`, `business_type`, `purpose`, `sign_id`) VALUES ('开团成功通知模板', '您的拼团${name}已开团成功，快去邀请好友一起拼团吧~', '业务通知需要申请', 1, 3, NULL, '', NULL, 0, '2020-02-03 16:00:00', 'GROUP_OPEN_SUCCESS', '开团成功通知', NULL);
INSERT INTO `sms_template`(`template_name`, `template_content`, `remark`, `template_type`, `review_status`, `template_code`, `review_reason`, `sms_setting_id`, `del_flag`, `create_time`, `business_type`, `purpose`, `sign_id`) VALUES ('参团人数不足提醒模板', '您的拼团${name}即将结束，还差${number}人就可成团了，快去邀请好友一起拼团吧~', '业务通知需要申请', 1, 3, NULL, '', NULL, 0, '2020-02-03 16:00:00', 'GROUP_NUM_LIMIT', '参团人数不足提醒', NULL);
INSERT INTO `sms_template`(`template_name`, `template_content`, `remark`, `template_type`, `review_status`, `template_code`, `review_reason`, `sms_setting_id`, `del_flag`, `create_time`, `business_type`, `purpose`, `sign_id`) VALUES ('拼团成功通知模板', '恭喜，您的拼团${name}已成团，我们将尽快为您安排发货哦~', '业务通知需要申请', 1, 3, NULL, '', NULL, 0, '2020-02-03 16:00:00', 'JOIN_GROUP_SUCCESS', '拼团成功通知', NULL);
INSERT INTO `sms_template`(`template_name`, `template_content`, `remark`, `template_type`, `review_status`, `template_code`, `review_reason`, `sms_setting_id`, `del_flag`, `create_time`, `business_type`, `purpose`, `sign_id`) VALUES ('拼团失败通知模板', '很抱歉，您的拼团${name}组团失败了，将在1-3个工作日内自动退款~', '业务通知需要申请', 1, 3, NULL, '', NULL, 0, '2020-02-03 16:00:00', 'JOIN_GROUP_FAIL', '拼团失败通知', NULL);
INSERT INTO `sms_template`(`template_name`, `template_content`, `remark`, `template_type`, `review_status`, `template_code`, `review_reason`, `sms_setting_id`, `del_flag`, `create_time`, `business_type`, `purpose`, `sign_id`) VALUES ('售后单提交成功通知模板', '售后单${name}已经提交成功，需要商家审核，请耐心等待哦~', '业务通知需要申请', 1, 3, NULL, '', NULL, 0, '2020-02-03 16:00:00', 'AFTER_SALE_ORDER_COMMIT_SUCCESS', '售后单提交成功通知', NULL);
INSERT INTO `sms_template`(`template_name`, `template_content`, `remark`, `template_type`, `review_status`, `template_code`, `review_reason`, `sms_setting_id`, `del_flag`, `create_time`, `business_type`, `purpose`, `sign_id`) VALUES ('售后审核通过通知模板', '售后单${name}已通过商家审核，请及时退回您的货品哦~', '业务通知需要申请', 1, 3, NULL, '', NULL, 0, '2020-02-03 16:00:00', 'AFTER_SALE_ORDER_CHECK_PASS', '售后审核通过通知', NULL);
INSERT INTO `sms_template`(`template_name`, `template_content`, `remark`, `template_type`, `review_status`, `template_code`, `review_reason`, `sms_setting_id`, `del_flag`, `create_time`, `business_type`, `purpose`, `sign_id`) VALUES ('售后审核未通过通知模板', '很抱歉，售后单${name}未通过商家审核，原因是：${remark}', '售后单业务审核未通过的通知，remark是审核未通过的原因内容', 1, 3, NULL, '', NULL, 0, '2020-02-03 16:00:00', 'AFTER_SALE_ORDER_CHECK_NOT_PASS', '售后审核未通过通知', NULL);
INSERT INTO `sms_template`(`template_name`, `template_content`, `remark`, `template_type`, `review_status`, `template_code`, `review_reason`, `sms_setting_id`, `del_flag`, `create_time`, `business_type`, `purpose`, `sign_id`) VALUES ('退货物品拒收通知模板', '很抱歉，您的退货物品${name}被商家拒收，原因是：${remark}', '退货物品业务拒收通知，remark是拒收的原因内容', 1, 3, NULL, '', NULL, 0, '2020-02-03 16:00:00', 'RETURN_ORDER_GOODS_REJECT', '退货物品拒收通知', NULL);
INSERT INTO `sms_template`(`template_name`, `template_content`, `remark`, `template_type`, `review_status`, `template_code`, `review_reason`, `sms_setting_id`, `del_flag`, `create_time`, `business_type`, `purpose`, `sign_id`) VALUES ('退款审核通过通知模板', '售后单${name}已通过退款审核，将在1-3个工作日内自动退款~', '业务通知需要申请', 1, 3, NULL, '', NULL, 0, '2020-02-03 16:00:00', 'REFUND_CHECK_PASS', '退款审核通过通知', NULL);
INSERT INTO `sms_template`(`template_name`, `template_content`, `remark`, `template_type`, `review_status`, `template_code`, `review_reason`, `sms_setting_id`, `del_flag`, `create_time`, `business_type`, `purpose`, `sign_id`) VALUES ('退款审核未通过通知模板', '很抱歉，售后单${name}未通过商家退款审核，原因是：${remark}', '售后单业务审核未通过的通知，remark是审核未通过的原因内容', 1, 3, NULL, '', NULL, 0, '2020-02-03 16:00:00', 'REFUND_CHECK_NOT_PASS', '退款审核未通过通知', NULL);

INSERT INTO `sms_template`(`template_name`, `template_content`, `remark`, `template_type`, `review_status`, `template_code`, `review_reason`, `sms_setting_id`, `del_flag`, `create_time`, `business_type`, `purpose`, `sign_id`) VALUES ('登陆密码试错通知', '您的密码已连续3次错误，请注意账户安全，您可以使用动态密码登录商城或重置密码', '登陆安全通知', 1, 3, NULL, '', NULL, 0, '2020-02-03 16:00:00', 'LOGIN_PASSWORD_SUM_OUT', '登陆密码试错通知', NULL);
INSERT INTO `sms_template`(`template_name`, `template_content`, `remark`, `template_type`, `review_status`, `template_code`, `review_reason`, `sms_setting_id`, `del_flag`, `create_time`, `business_type`, `purpose`, `sign_id`) VALUES ('支付密码试错通知', '您的支付密码已连续3次错误，请注意账户安全，您可以登录商城重置密码', '支付安全通知', 1, 3, NULL, '', NULL, 0, '2020-02-03 16:00:00', 'PAY_PASSWORD_SUM_OUT', '支付密码试错通知', NULL);
INSERT INTO `sms_template`(`template_name`, `template_content`, `remark`, `template_type`, `review_status`, `template_code`, `review_reason`, `sms_setting_id`, `del_flag`, `create_time`, `business_type`, `purpose`, `sign_id`) VALUES ('优惠券到账提醒', '您有${number}张优惠券到账，优惠总额${money}，赶紧花掉他们吧~', '优惠券到账提醒，money是金额', 1, 3, NULL, '', NULL, 0, '2020-02-03 16:00:00', 'COUPON_RECEIPT', '优惠券到账提醒', NULL);
INSERT INTO `sms_template`(`template_name`, `template_content`, `remark`, `template_type`, `review_status`, `template_code`, `review_reason`, `sms_setting_id`, `del_flag`, `create_time`, `business_type`, `purpose`, `sign_id`) VALUES ('优惠券过期提醒', '您有${number}张优惠券今日到期，优惠总额${money}，赶紧花掉他们吧~', '优惠券过期提醒通知，money是金额', 1, 3, NULL, '', NULL, 0, '2020-02-03 16:00:00', 'COUPON_EXPIRED', '优惠券过期提醒', NULL);
INSERT INTO `sms_template`(`template_name`, `template_content`, `remark`, `template_type`, `review_status`, `template_code`, `review_reason`, `sms_setting_id`, `del_flag`, `create_time`, `business_type`, `purpose`, `sign_id`) VALUES ('积分到账提醒', '您今日到账${number}积分，积分可以抵现金哦，真的很棒棒呢~', '积分到账提醒通知，number是积分值', 1, 3, NULL, '', NULL, 0, '2020-02-03 16:00:00', 'INTEGRAL_RECEIPT', '积分到账提醒', NULL);
INSERT INTO `sms_template`(`template_name`, `template_content`, `remark`, `template_type`, `review_status`, `template_code`, `review_reason`, `sms_setting_id`, `del_flag`, `create_time`, `business_type`, `purpose`, `sign_id`) VALUES ('积分过期提醒', '您有${number}积分将于7天后过期，积分来之不易，赶紧花掉他们吧~', '积分过期提醒通知，number是积分值', 1, 3, NULL, '', NULL, 0, '2020-02-03 16:00:00', 'INTEGRAL_EXPIRED', '积分过期提醒', NULL);
INSERT INTO `sms_template`(`template_name`, `template_content`, `remark`, `template_type`, `review_status`, `template_code`, `review_reason`, `sms_setting_id`, `del_flag`, `create_time`, `business_type`, `purpose`, `sign_id`) VALUES ('积分过期再次提醒', '您有${number}积分今天过期，积分来之不易，赶紧花掉他们吧~', '积分过期提醒通知，number是积分值', 1, 3, NULL, '', NULL, 0, '2020-02-03 16:00:00', 'INTEGRAL_EXPIRED_AGAIN', '积分过期再次提醒', NULL);
INSERT INTO `sms_template`(`template_name`, `template_content`, `remark`, `template_type`, `review_status`, `template_code`, `review_reason`, `sms_setting_id`, `del_flag`, `create_time`, `business_type`, `purpose`, `sign_id`) VALUES ('成长值到账提醒', '您今日收获了${number}成长值，真的很棒棒呢~', '成长值到账提醒通知，number是积分值', 1, 3, NULL, '', NULL, 0, '2020-02-03 16:00:00', 'GROWTH_VALUE_RECEIPT', '成长值到账提醒', NULL);
INSERT INTO `sms_template`(`template_name`, `template_content`, `remark`, `template_type`, `review_status`, `template_code`, `review_reason`, `sms_setting_id`, `del_flag`, `create_time`, `business_type`, `purpose`, `sign_id`) VALUES ('余额账户变更提醒', '您的余额账户今日收入${money}，支出${price}，如遇异常变动，请及时联系我们哦~', '余额账户变更通知，money是收入金额值，price也是金额值', 1, 3, NULL, '', NULL, 0, '2020-02-03 16:00:00', 'BALANCE_CHANGE', '余额账户变更提醒', NULL);
INSERT INTO `sms_template`(`template_name`, `template_content`, `remark`, `template_type`, `review_status`, `template_code`, `review_reason`, `sms_setting_id`, `del_flag`, `create_time`, `business_type`, `purpose`, `sign_id`) VALUES ('提现申请提交成功通知', '您的提现申请已经提交成功，需要平台审核，请耐心等待哦~', '提现申请提交成功通知', 1, 3, NULL, '', NULL, 0, '2020-02-03 16:00:00', 'BALANCE_WITHDRAW_APPLY_FOR', '提现申请提交成功通知', NULL);
INSERT INTO `sms_template`(`template_name`, `template_content`, `remark`, `template_type`, `review_status`, `template_code`, `review_reason`, `sms_setting_id`, `del_flag`, `create_time`, `business_type`, `purpose`, `sign_id`) VALUES ('提现成功通知', '恭喜您，您的提现申请已处理成功，将在1-3工作日内到账~', '提现成功通知', 1, 3, NULL, '', NULL, 0, '2020-02-03 16:00:00', 'BALANCE_WITHDRAW_SUCCESS', '提现成功通知', NULL);
INSERT INTO `sms_template`(`template_name`, `template_content`, `remark`, `template_type`, `review_status`, `template_code`, `review_reason`, `sms_setting_id`, `del_flag`, `create_time`, `business_type`, `purpose`, `sign_id`) VALUES ('提现申请驳回通知', '很抱歉，您的提现申请被驳回，原因是：${remark}', '提现审核驳回通知，remark是审核未通过原因的内容，长度不超过20个汉字', 1, 3, NULL, '', NULL, 0, '2020-02-03 16:00:00', 'BALANCE_WITHDRAW_REJECT', '提现申请驳回通知', NULL);
INSERT INTO `sms_template`(`template_name`, `template_content`, `remark`, `template_type`, `review_status`, `template_code`, `review_reason`, `sms_setting_id`, `del_flag`, `create_time`, `business_type`, `purpose`, `sign_id`) VALUES ('推广订单支付成功通知', '亲爱的${name}，您的推广订单${product}已支付成功，预计可赚${money}，继续加油哦~', '用于提醒会员他分享给好友的商品已经交易成功，并告知他预计可得的奖励收益 模板中的product变量取的商品名称，因为商品名称比订单号更容易帮助用户识别订单内容，请准许属性设为名称账号地址', 1, 3, NULL, '', NULL, 0, '2020-02-03 16:00:00', 'PROMOTE_ORDER_PAY_SUCCESS', '推广订单支付成功通知', NULL);
INSERT INTO `sms_template`(`template_name`, `template_content`, `remark`, `template_type`, `review_status`, `template_code`, `review_reason`, `sms_setting_id`, `del_flag`, `create_time`, `business_type`, `purpose`, `sign_id`) VALUES ('好友注册成功通知【无邀新奖励限制时】', '亲爱的${name}，您邀请的好友${number}已成功注册，${money}现金奖励，${price}优惠券奖励即将到账，继续加油哦~', '好友注册成功通知，name是用户名，number是好友手机含四位掩码，money是奖励金额, price是金额', 1, 3, NULL, '', NULL, 0, '2020-02-03 16:00:00', 'FRIEND_REGISTER_SUCCESS_NO_REWARD', '好友注册成功通知【无邀新奖励限制时】', NULL);
INSERT INTO `sms_template`(`template_name`, `template_content`, `remark`, `template_type`, `review_status`, `template_code`, `review_reason`, `sms_setting_id`, `del_flag`, `create_time`, `business_type`, `purpose`, `sign_id`) VALUES ('好友注册成功通知【有邀新奖励限制时】', '亲爱的${name}，您邀请的好友${number}已成功注册，好友成功完成一笔订单就可获得${money}现金奖励，{price}优惠券奖励，继续加油哦~', '好友注册成功通知，name是用户名，number是好友手机含四位掩码，money是奖励金额, price是金额', 1, 3, NULL, '', NULL, 0, '2020-02-03 16:00:00', 'FRIEND_REGISTER_SUCCESS_HAS_REWARD', '好友注册成功通知【有邀新奖励限制时】', NULL);
INSERT INTO `sms_template`(`template_name`, `template_content`, `remark`, `template_type`, `review_status`, `template_code`, `review_reason`, `sms_setting_id`, `del_flag`, `create_time`, `business_type`, `purpose`, `sign_id`) VALUES ('邀新奖励到账通知【有邀新奖励限制时】', '亲爱的${name}，您邀请的好友${number}已成功完成了一笔订单，${money}现金奖励，${price}优惠券奖励即将到账，继续加油哦~', '推广订单支付成功通知通知，name是用户名，number是好友手机含四位掩码，money是奖励金额, price是金额', 1, 3, NULL, '', NULL, 0, '2020-02-03 16:00:00', 'INVITE_CUSTOMER_REWARD_RECEIPT', '邀新奖励到账通知【有邀新奖励限制时】', NULL);
INSERT INTO `sms_template`(`template_name`, `template_content`, `remark`, `template_type`, `review_status`, `template_code`, `review_reason`, `sms_setting_id`, `del_flag`, `create_time`, `business_type`, `purpose`, `sign_id`) VALUES ('退款审核未通过通知模板', '很抱歉，售后单${name}未通过商家退款审核，原因是：${reason}', '业务通知需要申请', 1, 3, NULL, '', NULL, 0, '2020-02-03 16:00:00', 'REFUND_CHECK_NOT_PASS', '退款审核未通过通知', NULL);

-- sbc_message end----------

-- sbc_customer start ---
use `sbc-customer`;
ALTER TABLE `customer_detail`
ADD COLUMN `birth_day`  date NULL DEFAULT NULL COMMENT '生日' AFTER `is_distributor`,
ADD COLUMN `gender`  tinyint(1) NULL DEFAULT NULL COMMENT '0女，1男，2保密' AFTER `birth_day`;
-- sbc_customer end ---

-- -- s2b_statistics start ---
--
-- ALTER TABLE `replay_customer_detail`
-- ADD COLUMN `birth_day`  date NULL DEFAULT NULL COMMENT '生日' AFTER `is_distributor`,
-- ADD COLUMN `gender`  tinyint(1) NULL DEFAULT NULL COMMENT '性别:0女，1男，2保密' AFTER `birth_day`;
--
-- -- s2b_statistics end ---

-- sbc-crm --
use `sbc-crm`;
CREATE TABLE `plan_statistics_message` (
  `plan_id` bigint(11) NOT NULL COMMENT '运营计划id',
  `message_receive_num` int(10) DEFAULT NULL COMMENT '站内信收到人数',
  `message_receive_total` int(10) DEFAULT NULL COMMENT '站内信收到人次',
  `statistics_date` date DEFAULT NULL COMMENT '统计日期',
  `create_person` varchar(50) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_person` varchar(50) DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`plan_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='运营计划效果统计站内信收到人/次统计数据';

ALTER TABLE `customer_base_info`
ADD COLUMN `birth_day`  date NULL DEFAULT NULL COMMENT '生日' AFTER `account_balance`,
ADD COLUMN `gender`  tinyint(1) NULL DEFAULT NULL COMMENT '性别:0女，1男，2保密' AFTER `birth_day`,
ADD COLUMN `register_time` datetime(0) NULL DEFAULT NULL COMMENT '注册时间' AFTER `gender`;

CREATE TABLE `customer_plan_send_count` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT COMMENT '礼包优惠券发放统计id',
  `plan_id` bigint(11) DEFAULT NULL COMMENT '运营计划id',
  `gift_person_count` bigint(11) DEFAULT NULL COMMENT '发放礼包人数',
  `gift_count` bigint(11) DEFAULT NULL COMMENT '发放礼包次数',
  `coupon_person_count` bigint(11) DEFAULT NULL COMMENT '发放优惠券人数',
  `coupon_count` bigint(11) DEFAULT NULL COMMENT '发放优惠券张数',
  `coupon_person_use_count` bigint(11) DEFAULT NULL COMMENT '优惠券使用人数',
  `coupon_use_count` bigint(11) DEFAULT NULL COMMENT '优惠券使用张数',
  `coupon_use_rate` double(11,2) DEFAULT NULL COMMENT '优惠券转化率',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `plan_id_index` (`plan_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=64 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='权益礼包优惠券发放统计表';

-- customer_plan_covers_count运营计划转化效果--
CREATE TABLE `customer_plan_conversion`  (
  `id` bigint(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `plan_id` bigint(11) NULL DEFAULT NULL COMMENT '运营计划id',
  `visitors_uv_count` bigint(11) NULL COMMENT '访客数UV',
  `order_person_count` bigint(11) NULL COMMENT '下单人数',
  `order_count` bigint(11) NULL COMMENT '下单笔数',
  `pay_person_count` bigint(11) NULL COMMENT '付款人数',
  `pay_count` bigint(11) NULL COMMENT '付款笔数',
  `total_price` decimal(10, 2) NULL COMMENT '付款金额',
  `unit_price` decimal(10, 2) NULL COMMENT '客单价',
  `covers_count` bigint(11) NULL COMMENT '覆盖人数',
  `visitors_count` bigint(11) NULL COMMENT '访客人数',
  `covers_visitors_rate` double NULL COMMENT '访客人数/覆盖人数转换率',
  `pay_visitors_rate` double NULL COMMENT '付款人数/访客人数转换率',
  `pay_covers_rate` double NULL COMMENT '付款人数/覆盖人数转换率',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
);

-- sbc-crm  end--

-- sbc-setting --
use `sbc-setting`;
INSERT INTO `function_info`(`function_id`, `system_type_cd`, `menu_id`, `function_title`, `function_name`, `remark`, `sort`, `create_time`, `del_flag`) VALUES ('ff8080817019135101701e1dd1580000', 4, 'ff8080816f660586016f8e00e3c00000', '运营计划数据', 'f-customer-plan-data', NULL, 8, '2020-02-07 13:27:16', 0);

INSERT INTO  `authority`(`authority_id`, `system_type_cd`, `function_id`, `authority_title`, `authority_name`, `authority_url`, `request_type`, `remark`, `sort`, `create_time`, `del_flag`) VALUES ('ff8080817019135101701e212c6d0001', 4, 'ff8080817019135101701e1dd1580000', '根据id查询运营计划', NULL, '/customerplan/*', 'GET', NULL, 1, '2020-02-07 13:30:55', 0);
INSERT INTO  `authority`(`authority_id`, `system_type_cd`, `function_id`, `authority_title`, `authority_name`, `authority_url`, `request_type`, `remark`, `sort`, `create_time`, `del_flag`) VALUES ('ff8080817019135101701e2275b20002', 4, 'ff8080817019135101701e1dd1580000', '根据planId查询覆盖统计', NULL, '/customerplansendcount/*', 'GET', NULL, 2, '2020-02-07 13:32:20', 0);
INSERT INTO  `authority`(`authority_id`, `system_type_cd`, `function_id`, `authority_title`, `authority_name`, `authority_url`, `request_type`, `remark`, `sort`, `create_time`, `del_flag`) VALUES ('ff8080817019135101701e232a750003', 4, 'ff8080817019135101701e1dd1580000', '根据planId查询推送统计', NULL, '/planstatisticsmessagepush/*', 'GET', NULL, 3, '2020-02-07 13:33:06', 0);
INSERT INTO  `authority`(`authority_id`, `system_type_cd`, `function_id`, `authority_title`, `authority_name`, `authority_url`, `request_type`, `remark`, `sort`, `create_time`, `del_flag`) VALUES ('ff80808170526ffa0170579e2eae0000', 4, 'ff8080817019135101701e1dd1580000', '转换效果接口', '', '/customer/plan/conversion/*', 'GET', NULL, 4, '2020-02-18 17:25:49', 0);

INSERT INTO  `authority` (`authority_id`, `system_type_cd`, `function_id`, `authority_title`, `authority_name`, `authority_url`, `request_type`, `remark`, `sort`, `create_time`, `del_flag`) VALUES ('8a9bc76c6f6b7aa2016f6c1936310025', '4', '8a9bc76c6f6b7aa2016f6be12e570005', '通知节点', NULL, '/smstemplate/notices', 'POST', NULL, '9', '2020-02-07 13:33:06', '0');
INSERT INTO  `authority` (`authority_id`, `system_type_cd`, `function_id`, `authority_title`, `authority_name`, `authority_url`, `request_type`, `remark`, `sort`, `create_time`, `del_flag`) VALUES ('8a9bc76c6f6b7aa2016f6bea3d2711cc', '4', '8a9bc76c6f6b7aa2016f6be12e570005', '模板开关', NULL, '/smstemplate/open-flag', 'PUT', NULL, '10', '2020-02-07 13:33:06', '0');

update menu_info set menu_name='应用'  where menu_name = '营销' and system_type_cd = 3;
-- sbc-setting  end--

-- xxl-job --
use `xxl-job`;
INSERT INTO  `xxl_job_qrtz_trigger_info`(`id`, `job_group`, `job_cron`, `job_desc`, `add_time`, `update_time`, `author`, `alarm_email`, `executor_route_strategy`, `executor_handler`, `executor_param`, `executor_block_strategy`, `executor_timeout`, `executor_fail_retry_count`, `glue_type`, `glue_source`, `glue_remark`, `glue_updatetime`, `child_jobid`) VALUES (75, 6, '0 0 1 * * ?', '权益礼包优惠券发放统计', '2020-02-04 14:38:36', '2020-02-04 21:27:04', '张浩', '', 'FIRST', 'customerPlanningCountJobHandler', '', 'SERIAL_EXECUTION', 0, 0, 'BEAN', '', 'GLUE代码初始化', '2020-02-04 14:38:36', '');

INSERT INTO `xxl_job_qrtz_trigger_info`(`id`, `job_group`, `job_cron`, `job_desc`, `add_time`, `update_time`, `author`, `alarm_email`, `executor_route_strategy`, `executor_handler`, `executor_param`, `executor_block_strategy`, `executor_timeout`, `executor_fail_retry_count`, `glue_type`, `glue_source`, `glue_remark`, `glue_updatetime`, `child_jobid`) VALUES (70, 6, '0 0 1 * * ?', '运营计划统计--通知人次统计', '2020-02-10 10:40:24', '2020-02-10 10:40:24', '吕振伟', '', 'FIRST', 'customerPlanStatisticsPushJobHandler', '', 'SERIAL_EXECUTION', 0, 0, 'BEAN', '', 'GLUE代码初始化', '2020-02-10 10:40:24', '');

INSERT INTO  `xxl_job_qrtz_trigger_info`(`id`, `job_group`, `job_cron`, `job_desc`, `add_time`, `update_time`,
`author`, `alarm_email`, `executor_route_strategy`, `executor_handler`, `executor_param`, `executor_block_strategy`,
`executor_timeout`, `executor_fail_retry_count`, `glue_type`, `glue_source`, `glue_remark`, `glue_updatetime`, `child_jobid`) VALUES (73, 6, '0 0 1 * * ?', '运行计划转化效果统计', '2020-02-012 19:38:36', '2020-02-012 19:38:36', '张文昌', '', 'FIRST', 'customerPlanningConversionScheduledGenerate', '', 'SERIAL_EXECUTION', 0, 0, 'BEAN', '', 'GLUE代码初始化', '2020-02-12 19:38:36', '');

-- xxl-job end --
