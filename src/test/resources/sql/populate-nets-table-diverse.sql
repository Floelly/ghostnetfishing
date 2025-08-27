TRUNCATE TABLE nets;

INSERT INTO nets (id, location_lat, location_long, net_id, size, state) VALUES
                                                                            (1, 50.1234, 8.1234, 1001, 'L', 'REPORTED'),
                                                                            (2, 50.5678, 8.5678, 1002, 'M', 'LOST'),
                                                                            (3, 50.9876, 8.9876, 1003, 'S', 'RECOVERED'),
                                                                            (4, 50.1111, 8.1111, 1004, 'XL', 'RECOVERY_PENDING');