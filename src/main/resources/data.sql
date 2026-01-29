-- Guests (20 records)
INSERT INTO guest (id, first_name, last_name, email, phone, loyalty_points) VALUES
(1, 'John', 'Doe', 'john.doe@email.com', '555-0101', 100),
(2, 'Jane', 'Smith', 'jane.smith@email.com', '555-0102', 250),
(3, 'Michael', 'Johnson', 'michael.j@email.com', '555-0103', 50),
(4, 'Emily', 'Brown', 'emily.b@email.com', '555-0104', 10),
(5, 'David', 'Davis', 'david.d@email.com', '555-0105', 0),
(6, 'Sarah', 'Miller', 'sarah.m@email.com', '555-0106', 500),
(7, 'James', 'Wilson', 'james.w@email.com', '555-0107', 75),
(8, 'Jessica', 'Moore', 'jessica.m@email.com', '555-0108', 120),
(9, 'Robert', 'Taylor', 'robert.t@email.com', '555-0109', 30),
(10, 'Linda', 'Anderson', 'linda.a@email.com', '555-0110', 200),
(11, 'William', 'Thomas', 'william.t@email.com', '555-0111', 0),
(12, 'Elizabeth', 'Jackson', 'elizabeth.j@email.com', '555-0112', 40),
(13, 'Richard', 'White', 'richard.w@email.com', '555-0113', 90),
(14, 'Jennifer', 'Harris', 'jennifer.h@email.com', '555-0114', 150),
(15, 'Charles', 'Martin', 'charles.m@email.com', '555-0115', 300),
(16, 'Patricia', 'Thompson', 'patricia.t@email.com', '555-0116', 20),
(17, 'Thomas', 'Garcia', 'thomas.g@email.com', '555-0117', 60),
(18, 'Susan', 'Martinez', 'susan.m@email.com', '555-0118', 110),
(19, 'Christopher', 'Robinson', 'chris.r@email.com', '555-0119', 80),
(20, 'Margaret', 'Clark', 'margaret.c@email.com', '555-0120', 100);

-- Room Types (5 records)
INSERT INTO room_type (id, name, capacity, base_price) VALUES
(1, 'Single Standard', 1, 50.00),
(2, 'Double Standard', 2, 80.00),
(3, 'Double Deluxe', 2, 120.00),
(4, 'Suite', 4, 200.00),
(5, 'Penthouse', 4, 500.00);

-- Amenities (20 records)
INSERT INTO amenity (id, name) VALUES
(1, 'Wi-Fi'), (2, 'TV'), (3, 'Air Conditioning'), (4, 'Mini Bar'), (5, 'Safe'),
(6, 'Balcony'), (7, 'Sea View'), (8, 'Jacuzzi'), (9, 'Kitchenette'), (10, 'Coffee Maker'),
(11, 'Hair Dryer'), (12, 'Iron'), (13, 'Desk'), (14, 'Sofa'), (15, 'Bathrobe'),
(16, 'Slippers'), (17, 'Toiletries'), (18, 'Room Service'), (19, 'Wake-up Service'), (20, 'Welcome Drink');

-- Room Type Amenities Mapping
INSERT INTO room_type_amenity (room_type_id, amenity_id) VALUES
(1, 1), (1, 2), (1, 3),
(2, 1), (2, 2), (2, 3), (2, 4), (2, 10),
(3, 1), (3, 2), (3, 3), (3, 4), (3, 6), (3, 7), (3, 15),
(4, 1), (4, 2), (4, 3), (4, 4), (4, 5), (4, 6), (4, 8), (4, 9), (4, 13),
(5, 1), (5, 2), (5, 3), (5, 4), (5, 5), (5, 6), (5, 7), (5, 8), (5, 9), (5, 10), (5, 18), (5, 20);

-- Rooms (20 records)
INSERT INTO room (id, room_number, floor, status, housekeeping_status, room_type_id) VALUES
(1, '101', 1, 'AVAILABLE', 'CLEAN', 1),
(2, '102', 1, 'AVAILABLE', 'CLEAN', 1),
(3, '103', 1, 'OCCUPIED', 'NEEDS_CLEANING', 1),
(4, '104', 1, 'OUT_OF_SERVICE', 'NEEDS_CLEANING', 1),
(5, '201', 2, 'AVAILABLE', 'CLEAN', 2),
(6, '202', 2, 'AVAILABLE', 'NEEDS_CLEANING', 2),
(7, '203', 2, 'OCCUPIED', 'CLEAN', 2),
(8, '204', 2, 'OCCUPIED', 'CLEAN', 2),
(9, '301', 3, 'AVAILABLE', 'CLEAN', 3),
(10, '302', 3, 'AVAILABLE', 'CLEAN', 3),
(11, '303', 3, 'RESERVED', 'CLEAN', 3),
(12, '304', 3, 'RESERVED', 'CLEAN', 3),
(13, '401', 4, 'AVAILABLE', 'CLEAN', 4),
(14, '402', 4, 'OCCUPIED', 'CLEAN', 4),
(15, '403', 4, 'AVAILABLE', 'CLEAN', 4),
(16, '501', 5, 'AVAILABLE', 'CLEAN', 5),
(17, '502', 5, 'RESERVED', 'CLEAN', 5),
(18, '105', 1, 'AVAILABLE', 'CLEAN', 1),
(19, '205', 2, 'AVAILABLE', 'CLEAN', 2),
(20, '305', 3, 'AVAILABLE', 'CLEAN', 3);

-- Hotel Services (10 records)
INSERT INTO service (id, name, category, unit_price) VALUES
(1, 'Continental Breakfast', 'Food', 15.00),
(2, 'Full English Breakfast', 'Food', 25.00),
(3, 'Lunch Buffet', 'Food', 35.00),
(4, 'Dinner Set', 'Food', 50.00),
(5, 'Airport Shuttle', 'Transport', 40.00),
(6, 'Car Rental (Day)', 'Transport', 80.00),
(7, 'Spa Massage (1h)', 'Wellness', 90.00),
(8, 'Gym Pass (Day)', 'Wellness', 15.00),
(9, 'Babysitting (Hour)', 'Service', 20.00),
(10, 'Laundry (Bag)', 'Service', 30.00);

-- Reservations
INSERT INTO reservation (id, guest_id, check_in_date, check_out_date, status, total_amount, cancel_fee, loyalty_points_earned) VALUES
(1, 1, '2023-12-01', '2023-12-05', 'CHECKED_OUT', 330.00, 0.00, 30),
(2, 2, '2024-01-10', '2024-01-15', 'CHECKED_OUT', 618.00, 0.00, 60),
(3, 3, '2024-02-14', '2024-02-16', 'CHECKED_OUT', 264.00, 0.00, 20),
(4, 4, CURRENT_DATE - 2, CURRENT_DATE + 3, 'CHECKED_IN', 0.00, 0.00, 0),
(5, 5, CURRENT_DATE, CURRENT_DATE + 5, 'CHECKED_IN', 0.00, 0.00, 0),
(6, 6, CURRENT_DATE + 1, CURRENT_DATE + 4, 'CONFIRMED', 0.00, 0.00, 0),
(7, 7, CURRENT_DATE + 10, CURRENT_DATE + 15, 'CONFIRMED', 0.00, 0.00, 0),
(8, 8, CURRENT_DATE + 20, CURRENT_DATE + 22, 'CONFIRMED', 0.00, 0.00, 0),
(9, 9, '2023-11-01', '2023-11-02', 'CANCELED', 0.00, 0.00, 0),
(10, 10, '2024-03-01', '2024-03-05', 'CHECKED_OUT', 946.00, 0.00, 90),
(11, 11, '2024-03-10', '2024-03-12', 'CHECKED_OUT', 0.00, 0.00, 0),
(12, 12, '2024-04-01', '2024-04-07', 'CHECKED_OUT', 0.00, 0.00, 0),
(13, 13, CURRENT_DATE + 5, CURRENT_DATE + 7, 'CONFIRMED', 0.00, 0.00, 0),
(14, 14, CURRENT_DATE + 30, CURRENT_DATE + 35, 'PENDING', 0.00, 0.00, 0),
(15, 15, '2023-10-01', '2023-10-10', 'CHECKED_OUT', 2018.00, 0.00, 200),
(16, 16, CURRENT_DATE + 2, CURRENT_DATE + 4, 'CONFIRMED', 0.00, 0.00, 0),
(17, 17, CURRENT_DATE + 60, CURRENT_DATE + 67, 'PENDING', 0.00, 0.00, 0),
(18, 18, '2023-12-24', '2023-12-27', 'CHECKED_OUT', 0.00, 0.00, 0),
(19, 19, '2023-12-30', '2024-01-02', 'CHECKED_OUT', 0.00, 0.00, 0),
(20, 20, CURRENT_DATE + 100, CURRENT_DATE + 101, 'PENDING', 0.00, 0.00, 0);

-- Reservation Rooms
INSERT INTO reservation_room (id, reservation_id, room_id, rate_per_night, guests) VALUES
(1, 1, 1, 50.00, 1),
(2, 2, 5, 80.00, 2),
(3, 3, 9, 120.00, 2),
(4, 4, 3, 50.00, 1), 
(5, 5, 7, 80.00, 2), 
(6, 6, 11, 120.00, 2), 
(7, 7, 17, 500.00, 2), 
(8, 8, 2, 50.00, 1),
(9, 9, 6, 80.00, 2),
(10, 10, 13, 200.00, 4),
(11, 11, 10, 120.00, 2),
(12, 12, 16, 500.00, 2),
(13, 13, 4, 50.00, 1),
(14, 14, 8, 80.00, 2),
(15, 15, 14, 200.00, 3),
(16, 16, 12, 120.00, 2),
(17, 17, 19, 80.00, 2),
(18, 18, 15, 200.00, 2),
(19, 19, 5, 80.00, 2), 
(20, 20, 18, 50.00, 1);

-- Service Requests
INSERT INTO service_request (id, reservation_id, service_id, quantity, amount, status) VALUES
(1, 1, 1, 4, 60.00, 'COMPLETED'),
(2, 1, 5, 1, 40.00, 'COMPLETED'),
(3, 2, 7, 2, 180.00, 'COMPLETED'),
(4, 4, 10, 1, 30.00, 'IN_PROGRESS'),
(5, 5, 2, 2, 50.00, 'REQUESTED'),
(6, 10, 8, 4, 60.00, 'COMPLETED'),
(7, 12, 1, 6, 90.00, 'COMPLETED'),
(8, 12, 7, 1, 90.00, 'COMPLETED'),
(9, 15, 5, 2, 80.00, 'COMPLETED'),
(10, 19, 9, 1, 20.00, 'COMPLETED');

-- Invoices
INSERT INTO invoice (id, reservation_id, room_subtotal, service_subtotal, tax, discount, total, status, issued_at) VALUES
(1, 1, 200.00, 100.00, 30.00, 0, 330.00, 'PAID', '2023-12-05 10:00:00'),
(2, 2, 400.00, 180.00, 58.00, 20.00, 618.00, 'PAID', '2024-01-15 11:00:00'),
(3, 3, 240.00, 0.00, 24.00, 0, 264.00, 'PAID', '2024-02-16 09:30:00'),
(4, 9, 0.00, 0.00, 0.00, 0, 0.00, 'DRAFT', '2023-11-01 08:00:00'),
(5, 10, 800.00, 60.00, 86.00, 0, 946.00, 'PAID', '2024-03-05 12:00:00'),
(6, 15, 1800.00, 80.00, 188.00, 50.00, 2018.00, 'PAID', '2023-10-10 10:30:00');

-- Payments
INSERT INTO payment (id, invoice_id, amount, method, transaction_ref, status, paid_at) VALUES
(1, 1, 330.00, 'CARD', 'TXN123456', 'PAID', '2023-12-05 10:05:00'),
(2, 2, 618.00, 'CASH', NULL, 'PAID', '2024-01-15 11:15:00'),
(3, 3, 264.00, 'CARD', 'TXN789012', 'PAID', '2024-02-16 09:35:00'),
(4, 5, 946.00, 'BANK_TRANSFER', 'TXN345678', 'PAID', '2024-03-05 12:10:00'),
(5, 6, 2018.00, 'CARD', 'TXN901234', 'PAID', '2023-10-10 10:45:00');
