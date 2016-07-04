/*
Navicat MySQL Data Transfer

Source Server         : Local
Source Server Version : 50703
Source Host           : localhost:3306
Source Database       : test

Target Server Type    : MYSQL
Target Server Version : 50703
File Encoding         : 65001

Date: 2016-07-04 11:18:55
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `ID` mediumint(9) NOT NULL AUTO_INCREMENT,
  `FIRST_NAME` varchar(50) DEFAULT NULL,
  `LAST_NAME` varchar(50) DEFAULT NULL,
  `GENDER` tinyint(2) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=1035 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES ('1', '1w', 'aa', null);
INSERT INTO `user` VALUES ('2', 'same', 'bb', null);
INSERT INTO `user` VALUES ('3', 'same', 'cc', null);
INSERT INTO `user` VALUES ('4', 'same', 'dd', null);
INSERT INTO `user` VALUES ('5', 'same', 'ee', null);
INSERT INTO `user` VALUES ('6', 'same', 'ff', null);
INSERT INTO `user` VALUES ('7', 'same', 'gg', null);
INSERT INTO `user` VALUES ('8', 'same', 'hh', null);
INSERT INTO `user` VALUES ('9', 'same', 'ii', null);
INSERT INTO `user` VALUES ('10', '2wfdkd', 'll', null);
INSERT INTO `user` VALUES ('11', 'same', 'jj', null);
INSERT INTO `user` VALUES ('12', 'same', 'kk', null);
INSERT INTO `user` VALUES ('13', 'same', 'mm', null);
INSERT INTO `user` VALUES ('14', 'same', 'nn', null);
INSERT INTO `user` VALUES ('15', 'dsddsf', 'oo', null);
INSERT INTO `user` VALUES ('16', 'same', 'pp', null);
INSERT INTO `user` VALUES ('17', 'dsldsjfsdf', 'qq', null);
INSERT INTO `user` VALUES ('18', 'dsdfssscdcx', 'rr', null);
INSERT INTO `user` VALUES ('19', 'same', 'sss', null);
INSERT INTO `user` VALUES ('20', 'same', 'tt', null);
INSERT INTO `user` VALUES ('21', 'different', 'uu', null);
INSERT INTO `user` VALUES ('22', 'sdfdsxxc', 'vv', null);
INSERT INTO `user` VALUES ('23', 'xljsdics', 'ww', null);
INSERT INTO `user` VALUES ('24', '的东西', 'xx', null);
INSERT INTO `user` VALUES ('25', '离得近了长', 'yy', null);
INSERT INTO `user` VALUES ('26', '新了空间上大学', 'zz', null);
INSERT INTO `user` VALUES ('1032', null, null, '1');
INSERT INTO `user` VALUES ('1033', null, null, '1');
INSERT INTO `user` VALUES ('1034', null, null, '2');
