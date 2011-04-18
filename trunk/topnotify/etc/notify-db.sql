-- set encoding for database
SET character_set_client = utf8;
SET character_set_connection = utf8;
SET character_set_database = utf8;
SET character_set_results = utf8;
SET character_set_server = utf8;
SET collation_connection = utf8_bin;
SET collation_database = utf8_bin;
SET collation_server = utf8_bin;

-- create database
CREATE DATABASE IF NOT EXISTS top_notify DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;

-- create tables
CREATE TABLE top_notify (
	`id` BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT,
	`user_id` BIGINT(20) UNSIGNED NOT NULL,
	`category` INTEGER(10) UNSIGNED NOT NULL,
	`biz_type` INTEGER(10) UNSIGNED NOT NULL,
	`status` INTEGER(10) UNSIGNED NOT NULL,
	`app_key` VARCHAR(64) COLLATE utf8_general_ci NOT NULL DEFAULT '',
	`user_name` VARCHAR(32) COLLATE utf8_general_ci DEFAULT NULL,
	`content` VARCHAR(1024) COLLATE utf8_general_ci DEFAULT NULL,
	`gmt_create` DATETIME NOT NULL,
	`gmt_modified` DATETIME NOT NULL,
	PRIMARY KEY (`id`),
	KEY `IDX_TOPNOTIFY_USER` (`app_key`, `gmt_modified`, `user_id`, `category`, `biz_type`, `status`)
)ENGINE=InnoDB
AUTO_INCREMENT=1 CHARACTER SET 'utf8' COLLATE 'utf8_general_ci'
COMMENT='InnoDB free: 5164032 KB';

CREATE TABLE `top_authorize` (
  `id` BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `app_key` VARCHAR(64) COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `user_id` BIGINT(20) UNSIGNED NOT NULL,
  `user_name` VARCHAR(32) COLLATE utf8_general_ci DEFAULT NULL,
  `start_date` DATETIME NOT NULL,
  `end_date` DATETIME NOT NULL,
  `status` INTEGER(10) UNSIGNED NOT NULL,
  `email` VARCHAR(128) COLLATE utf8_general_ci DEFAULT NULL,
  `gmt_create` DATETIME NOT NULL,
  `gmt_modified` DATETIME NOT NULL,
  PRIMARY KEY (`id`),
  KEY `IDX_TOPAUTHORIZE_APPKEY` (`app_key`, `user_id`, `start_date`, `end_date`)
)ENGINE=InnoDB
AUTO_INCREMENT=1000009 CHARACTER SET 'utf8' COLLATE 'utf8_general_ci'
COMMENT='InnoDB free: 5068800 kB';

CREATE TABLE `top_subscribe` (
  `id` BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `app_key` VARCHAR(64) COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `user_id` BIGINT(20) UNSIGNED NOT NULL,
  `user_name` VARCHAR(32) COLLATE utf8_general_ci DEFAULT NULL,
  `type` INTEGER(10) UNSIGNED NOT NULL,
  `start_date` DATETIME NOT NULL,
  `end_date` DATETIME NOT NULL,
  `status` INTEGER(10) UNSIGNED NOT NULL,
  `email` VARCHAR(128) COLLATE utf8_general_ci DEFAULT NULL,
  `subscriptions` VARCHAR(256) COLLATE utf8_general_ci NOT NULL,
  `gmt_create` DATETIME NOT NULL,
  `gmt_modified` DATETIME NOT NULL,
  PRIMARY KEY (`id`),
  KEY `IDX_TOPSUBSCRIBE_APPKEY` (`app_key`, `status`)
)ENGINE=InnoDB
AUTO_INCREMENT=1000009 CHARACTER SET 'utf8' COLLATE 'utf8_general_ci'
COMMENT='InnoDB free: 5068800 kB';