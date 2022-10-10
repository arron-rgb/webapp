DROP TABLE IF EXISTS `t_task`;
CREATE TABLE `t_task`
(
    `id`           varchar(255) COLLATE utf8mb4_bin NOT NULL,
    `list_id`      varchar(255) COLLATE utf8mb4_bin DEFAULT NULL,
    `user_id`      varchar(255) COLLATE utf8mb4_bin DEFAULT NULL,
    `task`         text COLLATE utf8mb4_bin         DEFAULT NULL,
    `summary`      varchar(255) COLLATE utf8mb4_bin DEFAULT NULL,
    `priority`     int                              DEFAULT 0,
    `due`          datetime                         DEFAULT NULL,
    `created_time` datetime                         DEFAULT NULL,
    `updated_time` datetime                         DEFAULT NULL,
    UNIQUE KEY `id` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_bin;
