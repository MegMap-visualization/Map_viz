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

 Date: 25/11/2022 17:36:16
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_junction
-- ----------------------------
DROP TABLE IF EXISTS `t_junction`;
CREATE TABLE `t_junction` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `type` varchar(20) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `points_uid` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `connection` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci,
  `xml_uid` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `junction_id` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_xmluid` (`xml_uid`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=111 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

SET FOREIGN_KEY_CHECKS = 1;
