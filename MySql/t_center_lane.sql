/*
 Navicat Premium Data Transfer

 Source Server         : mac_local
 Source Server Type    : MySQL
 Source Server Version : 80030
 Source Host           : localhost:3306
 Source Schema         : megvii

 Target Server Type    : MySQL
 Target Server Version : 80030
 File Encoding         : 65001

 Date: 25/11/2022 17:36:09
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_center_lane
-- ----------------------------
DROP TABLE IF EXISTS `t_center_lane`;
CREATE TABLE `t_center_lane` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `lane_uid` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '所属的车道uid',
  `points_uid` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '点的唯一id',
  `xml_uid` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '所属的xml唯一uid',
  PRIMARY KEY (`id`),
  KEY `laneuid_idx` (`lane_uid`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5068 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

SET FOREIGN_KEY_CHECKS = 1;
