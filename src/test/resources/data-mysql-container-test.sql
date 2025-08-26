-- Users
INSERT INTO users (username, password, enabled) VALUES
                                                    ('user', 'password' , true),
                                                    ('admin', 'password' , true);

-- Authorities
INSERT INTO authorities (username, authority) VALUES
                                                  ('user', 'ROLE_STANDARD'),
                                                  ('admin', 'ROLE_ADMIN'),
                                                  ('admin', 'ROLE_USER');