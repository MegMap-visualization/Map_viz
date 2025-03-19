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

 Date: 16/03/2023 22:24:07
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_point
-- ----------------------------
DROP TABLE IF EXISTS `t_point`;
CREATE TABLE `t_point` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `lane_uid` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `p_index` bigint DEFAULT NULL,
  `xml_uid` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `geo` geometry DEFAULT NULL,
  `lon` decimal(18,15) DEFAULT NULL,
  `lat` decimal(18,15) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_xml_lane_uid` (`xml_uid`,`lane_uid`) USING BTREE,
  KEY `idx_xml_lon_lat_index` (`xml_uid`,`lon`,`lat`),
  KEY `idx_xml` (`xml_uid`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1626600892814205439 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

SET FOREIGN_KEY_CHECKS = 1;
