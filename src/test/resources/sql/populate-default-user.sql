-- Tabellen leeren und Auto Increment resetten
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE user_roles;
TRUNCATE TABLE users;
ALTER TABLE users AUTO_INCREMENT = 1;
SET FOREIGN_KEY_CHECKS = 1;

-- Users
INSERT INTO users (user_id, username, password, phone, enabled) VALUES
                                                    (1, 'user', 'password' , null, true),
                                                    (2, 'userwithnumber', 'password','+49000000000000', true),
                                                    (3, 'admin', 'password', null , true);

-- roles
INSERT INTO user_roles (user_id, role) VALUES
                                                  (1, 'STANDARD'),
                                                  (2, 'STANDARD'),
                                                  (2, 'RECOVERER'),
                                                  (3, 'STANDARD'),
                                                  (3, 'RECOVERER'),
                                                  (3, 'ADMIN');