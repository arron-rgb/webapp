-- ----------------------------
-- Table structure for t_comment
-- ----------------------------
DROP TABLE IF EXISTS `t_list`;
CREATE TABLE `t_list`
(
    `id`           varchar(255) COLLATE utf8mb4_bin NOT NULL,
    `name`         varchar(255) COLLATE utf8mb4_bin DEFAULT NULL,
    `user_id`      varchar(255) COLLATE utf8mb4_bin DEFAULT NULL,
    `created_time` datetime                         DEFAULT NULL,
    `updated_time` datetime                         DEFAULT NULL,
    UNIQUE KEY `id` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_bin;
