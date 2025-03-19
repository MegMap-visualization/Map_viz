/*
 Navicat Premium Data Transfer

 Source Server         : loaclhost
 Source Server Type    : MySQL
 Source Server Version : 80032 (8.0.32)
 Source Host           : localhost:3306
 Source Schema         : map_cloud

 Target Server Type    : MySQL
 Target Server Version : 80032 (8.0.32)
 File Encoding         : 65001

 Date: 16/03/2023 22:22:10
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_center_lane
-- ----------------------------
DROP TABLE IF EXISTS `t_center_lane`;
CREATE TABLE `t_center_lane` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `lane_uid` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '所属的车道uid',
  `xml_uid` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '所属的xml唯一uid',
  PRIMARY KEY (`id`),
  KEY `laneuid_idx` (`lane_uid`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=127390 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

SET FOREIGN_KEY_CHECKS = 1;
