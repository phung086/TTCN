# Hotel Management System API

Spring Boot 3 RESTful API for hotel management: guests, reservations, services, billing, and invoices.

## Thông tin sinh viên 

- Họ và tên: Phạm Đình Hưng
- Mã sinh viên: 22010145
- Email: phamhung080604@gmail.com

## Stack

- Java 21, Spring Boot 3.2.4
- Spring Web, Data JPA, Validation
- H2 (dev), PostgreSQL (prod placeholder)
- Springdoc OpenAPI (Swagger UI)
- MapStruct, Lombok (optional)

## Thu hoạch / kết quả đạt được

- Ứng dụng chạy ổn định và nạp dữ liệu mẫu thành công.
- Swagger UI hiển thị đầy đủ tham số nhập liệu.
- Có thể gọi API CRUD và các luồng nghiệp vụ chính.

## Run (dev)

1. Cài JDK 21+ và Maven.
2. Chạy: `mvn spring-boot:run`
3. Swagger UI: http://localhost:8080/swagger-ui/index.html

## Build & Test

- `mvn clean test`
- `mvn clean package`

## API Base

- `/api/v1`
- Core endpoints: guests CRUD, room availability, reservations create/get, check-in/out/cancel, invoice by reservation, payments add, services add to reservation.

## Profiles

- `dev` (default): H2 in-memory, ddl-auto=update.
- `prod`: set `SPRING_PROFILES_ACTIVE=prod` and configure PostgreSQL in `application.yml`.

## Data Seed

`src/main/resources/data.sql` loads comprehensive sample data (20+ records per entity).

## Completed Assignment Features

- **Database Design (ORM)**: Full ERD implemented with JPA entities (`docs/diagrams.md`).
- **Data Seeding**: Robust initial dataset in `data.sql`.
- **API Design**: RESTful endpoints with Business Logic (Overlapping dates check, Validation).
- **API Spec**: Swagger/OpenAPI integration.
- **Testing**: Postman Collection provided in `docs/postman-collection.json`.

## Documentation

- Diagrams (ERD, Sequence) available in `docs/diagrams.md`.
- Postman Collection: `docs/postman-collection.json`.

## Chạy trên máy khác

1. Cài JDK 21+ và Maven.
2. Lấy mã nguồn (zip hoặc git clone).
3. Trong thư mục dự án: chạy `mvn clean package` để kiểm tra build.
4. Chạy `mvn spring-boot:run`.
5. Mở Swagger: http://localhost:8080/swagger-ui/index.html.
