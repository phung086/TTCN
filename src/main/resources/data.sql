INSERT INTO guest (id, first_name, last_name, email, phone, loyalty_points) VALUES
 (1, 'John', 'Doe', 'john@example.com', '111-222', 10),
 (2, 'Jane', 'Smith', 'jane@example.com', '333-444', 20);

INSERT INTO room_type (id, name, capacity, base_price) VALUES
 (1, 'Standard', 2, 80),
 (2, 'Deluxe', 3, 120);

INSERT INTO amenity (id, name) VALUES
 (1, 'Wifi'),
 (2, 'Breakfast'),
 (3, 'Pool Access');

INSERT INTO room_type_amenity (room_type_id, amenity_id) VALUES
 (1,1), (1,2), (2,1), (2,2), (2,3);

INSERT INTO room (id, room_number, floor, status, housekeeping_status, room_type_id) VALUES
 (1, '101', 1, 'AVAILABLE', 'CLEAN', 1),
 (2, '102', 1, 'AVAILABLE', 'CLEAN', 1),
 (3, '201', 2, 'AVAILABLE', 'CLEAN', 2);

INSERT INTO service (id, name, category, unit_price) VALUES
 (1, 'Breakfast', 'Food', 10),
 (2, 'Airport Pickup', 'Transport', 30);
