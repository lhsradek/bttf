--
-- BttfApplication 3.14.15 Flyway script 
--
-- This SQL script creates the required tables

----- SEQUENCE hibernate_sequence -----

CREATE SEQUENCE hibernate_sequence INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1;

/* -- CREATE SEQUENCE hibernate_sequence MINVALUE 1 START 1; */

----- TABLE revinfo -----

CREATE TABLE revinfo (
    rev BIGINT NOT NULL,
    revtstmp BIGINT NULL,
    CONSTRAINT revinfo_rev_pkey PRIMARY KEY (rev)
);

----- TABLE bttf_role -----

CREATE TABLE bttf_role (
  id BIGINT NOT NULL,
  role_name VARCHAR(255) NOT NULL,
  enabled BOOLEAN,
  CONSTRAINT bttf_role_id_pkey PRIMARY KEY (id),
  CONSTRAINT bttf_role_name_uk UNIQUE (role_name)
);


----- TABLE bttf_user -----

CREATE TABLE bttf_user (
  id BIGINT NOT NULL,
  user_name VARCHAR(255) NOT NULL,
  password VARCHAR(255) NOT NULL,
  account_non_expired BOOLEAN,
  account_non_locked BOOLEAN,
  credentials_non_expired BOOLEAN,
  enabled BOOLEAN,
  CONSTRAINT bttf_user_id_pkey PRIMARY KEY (id),
  CONSTRAINT bttf_user_name_uk UNIQUE (user_name)
);


----- TABLE bttf_user_role -----

CREATE TABLE bttf_user_role (
  user_id BIGINT REFERENCES bttf_user (id) NOT NULL,
  role_id BIGINT REFERENCES bttf_role (id) NOT NULL,
  CONSTRAINT bttf_user_role_pkey PRIMARY KEY (user_id, role_id)
);


----- VIEW bttf_view -----

CREATE VIEW bttf_view AS SELECT ROW_NUMBER() OVER (ORDER BY a.id, c.id) as id, a.id user_id, a.user_name,
  a.enabled user_enabled, c.id role_id, c.role_name, c.enabled role_enabled FROM bttf_user a
  JOIN bttf_user_role b ON a.id = b.user_id JOIN bttf_role c ON b.role_id = c.id ORDER BY a.id, c.id;

