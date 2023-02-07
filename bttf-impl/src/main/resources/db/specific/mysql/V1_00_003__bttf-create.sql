--
-- BttfApplication 3.14.15 Flyway script 
--
-- This SQL script creates the required tables

----- SEQUENCE hibernate_sequence -----

-- CREATE SEQUENCE hibernate_sequence MINVALUE 1 START 1;


----- TABLE revinfo -----

CREATE TABLE `revinfo` (
  `rev` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `revtstmp` BIGINT(20) DEFAULT NULL,
  PRIMARY KEY (`rev`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

----- TABLE bttf_counter -----

CREATE TABLE `bttf_counter` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
    `counter_name` VARCHAR(255) DEFAULT NULL,
    `cnt` BIGINT(20) DEFAULT NULL,
    `timestmp` BIGINT(20) DEFAULT NULL,
    `status` VARCHAR(16) DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `bttf_bttf_name_uk` (`counter_name`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;


----- TABLE bttf_counter_a -----

CREATE TABLE `bttf_counter_a` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
    `rev` BIGINT(20) REFERENCES revinfo (rev),
    `revtype` TINYINT(4) DEFAULT NULL,
    `counter_name` VARCHAR(255) DEFAULT NULL,
    `counter_name_m` BIT(1) DEFAULT NULL,
    `cnt` BIGINT(20) DEFAULT NULL,
    `cnt_m` BIT(1) DEFAULT NULL,
    `timestmp` BIGINT(20) DEFAULT NULL,
    `timestmp_m` BIT(1) DEFAULT NULL,
    `status` VARCHAR(16) DEFAULT NULL,
    `status_m` BIT(1) DEFAULT NULL,
    PRIMARY KEY (`id`,`rev`),
    CONSTRAINT `bttf_counter_a_rev` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;


----- TABLE bttf_message -----

CREATE TABLE `bttf_messgae` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
    `uuid` VARCHAR(255) DEFAULT NULL,
    `sertvice_name` VARCHAR(255) DEFAULT NULL,
    `cnt` BIGINT(20) DEFAULT NULL,
    `timestmp` BIGINT(20) DEFAULT NULL,
    `message` VARCHAR(255) DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `bttf_uuid_uk` (`uuid`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;


----- TABLE bttf_message_a -----

CREATE TABLE `bttf_message_a` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
    `rev` BIGINT(20) REFERENCES revinfo (rev),
    `revtype` TINYINT(4) DEFAULT NULL,
    `uuid` VARCHAR(16) DEFAULT NULL,
    `uuid_m` BIT(1) DEFAULT NULL,
    `cnt` BIGINT(20) DEFAULT NULL,
    `cnt_m` BIT(1) DEFAULT NULL,
    `timestmp` BIGINT(20) DEFAULT NULL,
    `timestmp_m` BIT(1) DEFAULT NULL,
    PRIMARY KEY (`id`,`rev`),
    CONSTRAINT `bttf_message_a_rev` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;


----- TABLE bttf_role -----

CREATE TABLE `bttf_role` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `role_name` VARCHAR(255) DEFAULT NULL,
  `enabled` BIT(1) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `bttf_role_name_uk` (`role_name`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;


----- TABLE bttf_user -----

CREATE TABLE `bttf_user` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `user_name` VARCHAR(255) DEFAULT NULL,
  `password` VARCHAR(255) DEFAULT NULL,
  `account_non_expired` BIT(1) DEFAULT NULL,
  `account_non_locked` BIT(1) DEFAULT NULL,
  `credentials_non_expired` BIT(1) DEFAULT NULL,
  `enabled` BIT(1) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `bttf_user_name_uk` (`user_name`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;


----- TABLE bttf_user_role -----

CREATE TABLE `bttf_user_role` (
  `user_id` BIGINT(20) NOT NULL,
  `role_id` BIGINT(20) NOT NULL,
  PRIMARY KEY (`user_id`,`role_id`),
  KEY `bttf_user_role_user_id` (`user_id`),
  KEY `bttf_user_role_role_id` (`role_id`),
  CONSTRAINT `bttf_user_role_user_id` FOREIGN KEY (`user_id`) REFERENCES `bttf_user` (`id`),
  CONSTRAINT `bttf_user_role_role_id` FOREIGN KEY (`role_id`) REFERENCES `bttf_role` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


----- VIEW bttf_view -----

CREATE VIEW `bttf_view` AS SELECT ROW_NUMBER() OVER (ORDER BY a.id, c.id) as id, a.id user_id, a.user_name,
  a.enabled user_enabled, c.id role_id, c.role_name, c.enabled role_enabled FROM bttf_user a
  JOIN bttf_user_role b ON a.id = b.user_id JOIN bttf_role c ON b.role_id = c.id ORDER BY a.id, c.id;
  
----- VIEW index_counter_view -----

CREATE VIEW `bttf_counter_view` AS SELECT
    `id`,
    `counter_name`,
    IF (`cnt` = 0, "", `cnt`) `cnt`,
    IF (`timestmp` = 0, "",
        FROM_UNIXTIME(`timestmp` / 1000, '%Y-%m-%d %H:%i:%s')) `date`,
    `status`
FROM `bttf_counter` ORDER BY `id`;


----- VIEW index_counter_a_view -----

CREATE VIEW `bttf_counter_a_view` AS SELECT
    FROM_UNIXTIME(`revtstmp` / 1000, '%Y-%m-%d %H:%i:%s') `revdate`,
    a.id, a.rev, `revtype`,
    a.counter_name,
    IF (`cnt` = 0, "", `cnt`) `cnt`,
    IF (`timestmp` = 0, "",
        FROM_UNIXTIME(`timestmp` / 1000, '%Y-%m-%d %H:%i:%s')) `date`
FROM `bttf_counter_a` a, `revinfo` r WHERE a.rev = r.rev ORDER BY revtstmp, id, rev;


