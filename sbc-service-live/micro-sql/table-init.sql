/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50726
Source Host           : 127.0.0.1:3306
Source Database       : xiyaya

Target Server Type    : MYSQL
Target Server Version : 50726
File Encoding         : 65001

Date: 2022-09-16 19:53:26
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for live_bag
-- ----------------------------
DROP TABLE IF EXISTS `live_bag`;
CREATE TABLE `live_bag` (
  `live_bag_id` int(11) NOT NULL AUTO_INCREMENT,
  `live_room_id` int(11) NOT NULL COMMENT '直播间id',
  `bag_name` varchar(60) NOT NULL COMMENT '福袋名称',
  `provide_status` int(1) NOT NULL DEFAULT 0 COMMENT '发放状态：0：未发放，1：已发放',
  `provide_nums` int(1) NOT NULL DEFAULT 0 COMMENT '发放次数',
  `winning_number` int(11) NOT NULL DEFAULT '0' COMMENT '中奖名额',
  `lottery_time` double NOT NULL DEFAULT '10' COMMENT '开奖时间：分钟',
  `join_type` int(1) NOT NULL DEFAULT '0' COMMENT '用户参与方式：0 指定内容',
  `join_content` varchar(255) DEFAULT NULL COMMENT '用户参与方式的内容',
  `ticket_way` int(1) NOT NULL DEFAULT '0' COMMENT '中奖用户兑换方式：0 自动发放',
  `activity_id` varchar(32) NOT NULL COMMENT '活动id',
  `coupon_id` varchar(32) NOT NULL COMMENT '优惠劵id',
  `create_person` int(11) DEFAULT NULL,
  `create_time` datetime NOT NULL,
  `update_person` int(11) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `del_flag` int(1) NOT NULL DEFAULT '0' COMMENT '删除标识,0:未删除1:已删除',
  PRIMARY KEY (`live_bag_id`),
  KEY `idx_live_bag_live_room_id` (`live_room_id`) USING BTREE,
  KEY `idx_live_bag_create_time` (`create_time`) USING BTREE,
  KEY `idx_live_bag_bag_name` (`bag_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='福袋';

-- ----------------------------
-- Table structure for live_room
-- ----------------------------
DROP TABLE IF EXISTS `live_room`;
CREATE TABLE `live_room` (
  `live_room_id` int(11) NOT NULL AUTO_INCREMENT,
  `live_room_name` varchar(600) NOT NULL COMMENT '直播间名称',
  `img_path`  varchar(600) DEFAULT NULL COMMENT '直播间图片',
  `sys_flag` tinyint(3) unsigned NOT NULL DEFAULT '1' COMMENT '平台标志，0：平台，1：非平台',
  `company_id` int(11) DEFAULT NULL COMMENT '厂商id',
  `create_person` int(11) DEFAULT NULL,
  `create_time` datetime NOT NULL,
  `update_person` int(11) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `del_flag` int(1) DEFAULT '0' COMMENT '删除标识,0:未删除1:已删除 ',
  PRIMARY KEY (`live_room_id`),
  KEY `idx_live_room_name` (`live_room_name`),
  KEY `idx_live_room_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='直播间';

-- ----------------------------
-- Table structure for live_room_rela
-- ----------------------------
DROP TABLE IF EXISTS `live_room_rela`;
CREATE TABLE `live_room_rela` (
  `live_room_rela_id` int(11) NOT NULL AUTO_INCREMENT,
  `live_room_id` int(11) DEFAULT NULL COMMENT '直播间id',
  `rela_type` varchar(2) DEFAULT NULL COMMENT '关联类型,B:品牌,A:直播账号,O:运营账号,G:商品,B:福袋',
  `rela_id` varchar(64) DEFAULT NULL COMMENT '关联id',
  `rela_content` varchar(255) DEFAULT NULL COMMENT '关联内容',
  `status` int(1) DEFAULT '0' COMMENT '状态',
  `create_person` int(11) DEFAULT NULL,
  `create_time` datetime NOT NULL,
  `update_person` int(11) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `del_flag` int(1) DEFAULT '0' COMMENT '删除标识,0:未删除1:已删除',
  PRIMARY KEY (`live_room_rela_id`),
  UNIQUE KEY `idx_live_room_rela` (`live_room_id`,`rela_type`,`rela_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='直播间关联表';
