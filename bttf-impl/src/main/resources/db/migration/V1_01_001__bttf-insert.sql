--
-- BttfApplication 3.14.15 Flyway script 
--
  
----- TABLE bttf_role -----
  
INSERT INTO bttf_role (id, role_name, enabled) VALUES (1, 'adminRole', true);
INSERT INTO bttf_role (id, role_name, enabled) VALUES (2, 'managerRole', true);
INSERT INTO bttf_role (id, role_name, enabled) VALUES (3, 'userRole', true);


----- TABLE bttf_user -----
  
INSERT INTO bttf_user
  (id, user_name, password, account_non_expired, account_non_locked, credentials_non_expired, enabled)
    VALUES (1, 'admin',
    '243261243034246a77614b5352774e75766e4433596734426849714b2e5856724b5a594c37494543316a5765374e74757867454369554a35726c4e65',
    true, true, true, true);
INSERT INTO bttf_user
  (id, user_name, password, account_non_expired, account_non_locked, credentials_non_expired, enabled)
    VALUES (2, 'lhs', 
    '24326124303424655569755352476f2e30737873636f4a3832515878756875656d573634436a7054674f784d5656654b3379586c4f584f6c32684447',
    true, true, true, true);
INSERT INTO bttf_user
  (id, user_name, password, account_non_expired, account_non_locked, credentials_non_expired, enabled)
    VALUES (3, 'user',
    '243261243034244358565237544c4f3853754a6f695552384d386f572e5259693672486c6b6b4b36724163542e624c4b44716878365a735a306a6d75',
    true, true, true, true);

    
----- TABLE bttf_user_role -----
  
    
-- old
-- 
-- Simple INSERT for H2, Postgresql, MariaDB (MySQL) and others databases
-- 
-- INSERT INTO bttf_user_role (user_id, role_id) VALUES (1, 1);
-- INSERT INTO bttf_user_role (user_id, role_id) VALUES (1, 2);
-- INSERT INTO bttf_user_role (user_id, role_id) VALUES (1, 3);
-- INSERT INTO bttf_user_role (user_id, role_id) VALUES (2, 2);
-- INSERT INTO bttf_user_role (user_id, role_id) VALUES (2, 3);
-- INSERT INTO bttf_user_role (user_id, role_id) VALUES (3, 3);

    
-- For H2, Postgresql, MariaDB (MySQL)

-- new, but we are role and user in redis 

INSERT INTO bttf_user_role (user_id, role_id) (
    SELECT id user_id, (SELECT id FROM bttf_role
    WHERE role_name='adminRole') role_id
    FROM bttf_user WHERE user_name='admin');
INSERT INTO bttf_user_role (user_id, role_id) (
    SELECT id user_id, (SELECT id FROM bttf_role
    WHERE role_name='managerRole') role_id
    FROM bttf_user WHERE user_name='admin');
INSERT INTO bttf_user_role (user_id, role_id) (
    SELECT id user_id, (SELECT id FROM bttf_role
    WHERE role_name='userRole') role_id
    FROM bttf_user WHERE user_name='admin');
INSERT INTO bttf_user_role (user_id, role_id) (
    SELECT id user_id, (SELECT id FROM bttf_role
    WHERE role_name='managerRole') role_id
    FROM bttf_user WHERE user_name='lhs');
INSERT INTO bttf_user_role (user_id, role_id) (
    SELECT id user_id, (SELECT id FROM bttf_role
    WHERE role_name='userRole') role_id
    FROM bttf_user WHERE user_name='lhs');
INSERT INTO bttf_user_role (user_id, role_id) (
    SELECT id user_id, (SELECT id FROM bttf_role
    WHERE role_name='userRole') role_id
    FROM bttf_user WHERE user_name='user');
   
