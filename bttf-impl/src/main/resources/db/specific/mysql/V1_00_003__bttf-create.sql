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
  
