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

 Date: 25/11/2022 17:36:35
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_object
-- ----------------------------
DROP TABLE IF EXISTS `t_object`;
CREATE TABLE `t_object` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `object_id` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `type` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `points_uid` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `xml_uid` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `index_object_id` (`object_id`),
  KEY `idx_xmluid` (`xml_uid`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2480 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

SET FOREIGN_KEY_CHECKS = 1;
