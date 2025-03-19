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

 Date: 25/11/2022 17:36:49
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_signal
-- ----------------------------
DROP TABLE IF EXISTS `t_signal`;
CREATE TABLE `t_signal` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `signal_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `type` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `layout_type` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `points_uid` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '关联的点序列表',
  `stopline_ids` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `xml_uid` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_xmluid` (`xml_uid`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=274 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

SET FOREIGN_KEY_CHECKS = 1;
