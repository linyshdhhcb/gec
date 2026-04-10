/*
 Navicat Premium Data Transfer

 Source Server         : nisangzi
 Source Server Type    : MySQL
 Source Server Version : 80040
 Source Host           : localhost:3306
 Source Schema         : gec_auth

 Target Server Type    : MySQL
 Target Server Version : 80040
 File Encoding         : 65001

 Date: 10/04/2026 09:17:08
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '角色id',
  `role_name` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '角色名称',
  `role_code` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '角色编码',
  `description` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '描述',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '删除标记（0:可用 1:已删除）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '角色' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES (1, 'system管理员', 'SYSTEM', 'admin', '2021-05-31 18:09:18', '2022-12-27 10:18:47', 1);
INSERT INTO `sys_role` VALUES (2, '普通管理员', 'COMMON', '普通管理员', '2021-06-01 08:38:40', '2022-02-24 10:42:46', 1);
INSERT INTO `sys_role` VALUES (8, '用户管理员', 'yhgly', NULL, '2022-06-08 17:39:04', '2022-06-08 17:39:04', 1);
INSERT INTO `sys_role` VALUES (9, '大飞管理员', 'dafei', 'dafei', NULL, NULL, 1);
INSERT INTO `sys_role` VALUES (10, '大飞管理员2', 'dafei2', 'dafei2', '2022-12-26 15:53:38', NULL, 0);
INSERT INTO `sys_role` VALUES (11, '测试账号', 'testadmin', '测试账号', '2022-12-27 10:15:04', NULL, 0);
INSERT INTO `sys_role` VALUES (12, 'aa', 'aa', NULL, '2022-12-30 16:38:51', NULL, 0);
INSERT INTO `sys_role` VALUES (13, 'b', 'bb', NULL, '2022-12-30 16:39:06', '2026-04-10 08:08:29', 0);
INSERT INTO `sys_role` VALUES (14, '1', '1', NULL, '2026-04-10 08:08:51', NULL, 0);

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户id',
  `username` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '用户名',
  `password` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '' COMMENT '密码',
  `name` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '姓名',
  `phone` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '手机号',
  `email` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '邮箱',
  `avatar` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '头像地址',
  `gender` tinyint NULL DEFAULT 0 COMMENT '性别（0:未知 1:男 2:女）',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态（0:正常 1:禁用）',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '删除标记（0:可用 1:已删除）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_username`(`username` ASC) USING BTREE COMMENT '用户名唯一索引'
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user
-- ----------------------------

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `user_id` bigint NOT NULL COMMENT '用户id',
  `role_id` bigint NOT NULL COMMENT '角色id',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '删除标记（0:可用 1:已删除）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_role`(`user_id` ASC, `role_id` ASC) USING BTREE COMMENT '用户角色唯一约束',
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_role_id`(`role_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '用户角色关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;
