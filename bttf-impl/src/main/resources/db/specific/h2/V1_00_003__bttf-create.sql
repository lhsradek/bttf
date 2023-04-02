-- BttfApplication 3.14.15 Flyway script 
--
-- This SQL script creates the required tables

----- SEQUENCE revinfo_seq -----

CREATE SEQUENCE revinfo_seq MINVALUE 1;

----- SEQUENCE bttf_counter_seq -----

CREATE SEQUENCE bttf_counter_seq MINVALUE 1;

----- SEQUENCE bttf_message_seq -----

CREATE SEQUENCE bttf_message_seq MINVALUE 1;


----- TABLE revinfo -----

CREATE TABLE revinfo (
    rev INTEGER DEFAULT nextval('revinfo_seq') PRIMARY KEY,
    revtstmp BIGINT
);

----- TABLE bttf_counter -----

CREATE TABLE bttf_counter (
    id BIGINT DEFAULT nextval('bttf_counter_seq') PRIMARY KEY,
    counter_name VARCHAR(255) NOT NULL,
    cnt BIGINT, 
    timestmp BIGINT, 
    status VARCHAR(16) NOT NULL,
    CONSTRAINT counter_name_uk UNIQUE (counter_name)
);

----- TABLE bttf_counter_a -----

CREATE TABLE bttf_counter_a (
    id BIGINT NOT NULL,
    rev INTEGER REFERENCES revinfo (rev),
    revtype TINYINT,
    counter_name VARCHAR(255) NULL,
    counter_name_m BOOLEAN,
    cnt BIGINT NULL,
    cnt_m BOOLEAN,
    timestmp BIGINT NULL,
    timestmp_m BOOLEAN,
    status VARCHAR(16) NULL,
    status_m BOOLEAN,
    CONSTRAINT primary_bttf_counter_a PRIMARY KEY (id, rev)
);
CREATE INDEX key_bttf_counter_a_rev ON bttf_counter_a (rev);


----- TABLE bttf_message -----

CREATE TABLE bttf_message (
    id BIGINT DEFAULT nextval('bttf_message_seq') PRIMARY KEY,
    uuid VARCHAR(255) NOT NULL,
    service_name VARCHAR(255) NOT NULL,
    cnt BIGINT, 
    timestmp BIGINT, 
    message VARCHAR(255) NOT NULL,
    CONSTRAINT uuid_uk UNIQUE (uuid)
);

----- TABLE bttf_message_a -----

CREATE TABLE bttf_message_a (
    id BIGINT NOT NULL,
    rev INTEGER REFERENCES revinfo (rev),
    revtype TINYINT,
    uuid VARCHAR(255) NULL,
    uuid_m BOOLEAN,
    cnt BIGINT NULL,
    cnt_m BOOLEAN,
    timestmp BIGINT NULL,
    timestmp_m BOOLEAN,
    CONSTRAINT primary_bttf_message_a PRIMARY KEY (id, rev)
);
CREATE INDEX key_bttf_message_a_rev ON bttf_message_a (rev);



----- TABLE bttf_role -----

CREATE TABLE bttf_role (
  id BIGINT NOT NULL,
  role_name CHARACTER VARYING(255) NOT NULL,
  enabled BOOLEAN,
  CONSTRAINT primary_key_role PRIMARY KEY (id),
  CONSTRAINT role_name UNIQUE (role_name)
);

----- TABLE bttf_user -----

CREATE TABLE bttf_user (
  id BIGINT NOT NULL,
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
  
----- VIEW bttf_counter_view -----

CREATE VIEW bttf_counter_view AS SELECT
    id, counter_name,
    CASE WHEN cnt > 0 THEN
        TO_CHAR (cnt) ELSE '' END cnt,
    CASE WHEN timestmp > 0 THEN
        TO_CHAR(DATEADD('SECOND', (3600 * RIGHT(CAST(CURRENT_TIMESTAMP AS TIMESTAMP WITH TIME ZONE), 2) + timestmp / 1000),
        DATE '1970-01-01'), 'YYYY-MM-DD HH24:MI:SS') ELSE '' END date,
    status
FROM bttf_counter ORDER BY id;
  

----- VIEW bttf_counter_a_view -----

CREATE VIEW bttf_counter_a_view AS SELECT
    TO_CHAR(DATEADD('SECOND', (3600 * RIGHT(CAST(CURRENT_TIMESTAMP AS TIMESTAMP WITH TIME ZONE), 2) + revtstmp / 1000),
        DATE '1970-01-01'), 'YYYY-MM-DD HH24:MI:SS') revdate,
    a.id, a.rev, a.revtype,
    a.counter_name,
    CASE WHEN a.cnt > 0 THEN
        TO_CHAR (a.cnt) ELSE '' END cnt,
    CASE WHEN a.timestmp > 0 THEN
        TO_CHAR(DATEADD('SECOND', (3600 * RIGHT(CAST(CURRENT_TIMESTAMP AS TIMESTAMP WITH TIME ZONE), 2) + a.timestmp / 1000),
        DATE '1970-01-01'), 'YYYY-MM-DD HH24:MI:SS') ELSE '' END date
FROM bttf_counter_a a, revinfo r WHERE a.rev = r.rev ORDER BY revtstmp, id, rev;

