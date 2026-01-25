# Hotel Management System API

Spring Boot 3 RESTful API for hotel management (guests, reservations, services, billing).

## Stack

- Java 17, Spring Boot 3.2
- Spring Web, Data JPA, Validation
- H2 (dev), PostgreSQL (prod placeholder)
- Springdoc OpenAPI
- MapStruct, Lombok (optional)

## Run (dev)

1. Ensure JDK 17+ and Maven.
2. `mvn spring-boot:run`
3. Swagger UI: http://localhost:8080/swagger-ui.html

## Build & Test

- `mvn clean test`
- `mvn clean package`

## API Base

- `/api/v1`
- Core endpoints: guests CRUD, room availability, reservations create/get, check-in/out/cancel, invoice by reservation, payments add, services add to reservation.

## Profiles

- `dev` (default): H2 in-memory, ddl-auto=update.
- `prod`: set `SPRING_PROFILES_ACTIVE=prod` and configure PostgreSQL in `application.yml`.

## Data seed

`src/main/resources/data.sql` loads sample guests, room types, amenities, rooms, services.

## Notes

- Business rules and calculations are simplified placeholders; extend per assignment (availability overlap checks, cancel fees, loyalty points, invoice math).
- Add Postman collection under `docs/postman-collection.json` (placeholder file) for testing.
- Add diagrams (ERD, sequence) under `docs/`.
