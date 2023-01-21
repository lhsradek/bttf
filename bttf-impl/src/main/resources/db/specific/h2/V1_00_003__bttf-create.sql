--
-- BttfApplication 3.14.15 Flyway script 
--
-- This SQL script creates the required tables

----- SEQUENCE hibernate_sequence -----

CREATE SEQUENCE hibernate_sequence MINVALUE 1 START 1;

----- TABLE revinfo -----

CREATE TABLE revinfo (
    rev BIGINT IDENTITY NOT NULL,
    revtstmp BIGINT,
    CONSTRAINT primary_key_revinfo PRIMARY KEY (rev)
);

----- TABLE bttf_role -----

CREATE TABLE bttf_role (
  id BIGINT IDENTITY NOT NULL,
  role_name CHARACTER VARYING(255) NOT NULL,
  enabled BOOLEAN,
  CONSTRAINT primary_key_role PRIMARY KEY (id),
  CONSTRAINT role_name UNIQUE (role_name)
);

----- TABLE bttf_user -----

CREATE TABLE bttf_user (
  id BIGINT IDENTITY NOT NULL,
  user_name CHARACTER VARYING(255) NOT NULL,
  password CHARACTER VARYING(255) NOT NULL,
  account_non_expired BOOLEAN,
  account_non_locked BOOLEAN,
  credentials_non_expired BOOLEAN,
  enabled BOOLEAN,
  CONSTRAINT primary_key_user PRIMARY KEY (id),
  CONSTRAINT user_name UNIQUE (user_name)
);


----- TABLE bttf_user_role -----

CREATE TABLE bttf_user_role (
  user_id BIGINT REFERENCES bttf_user (id),
  role_id BIGINT REFERENCES bttf_role (id),
  CONSTRAINT primary_key_user_role PRIMARY KEY (user_id, role_id)
);
CREATE INDEX key_user_role_user_id ON bttf_user_role (user_id);
CREATE INDEX key_user_role_role_id ON bttf_user_role (role_id);


----- VIEW bttf_view -----

CREATE VIEW bttf_view AS SELECT ROWNUM() id, a.id user_id, a.user_name, a.enabled user_enabled, c.id role_id,
  c.role_name, c.enabled role_enabled FROM bttf_user a JOIN bttf_user_role b ON a.id = b.user_id
  JOIN bttf_role c ON b.role_id = c.id ORDER BY a.id, c.id;
