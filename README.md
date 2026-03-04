# 🏨 Hotel Management System

Hệ thống quản lý khách sạn xây dựng bằng **Spring Boot 3**, tích hợp giao diện web đầy đủ cho cả khách hàng và quản trị viên.

---

## 👤 Thông Tin Sinh Viên

| Họ và tên | Mã sinh viên | Email |
|-----------|-------------|-------|
| Phạm Đình Hưng | 22010145 | phamhung080604@gmail.com |

---

## 🛠️ Công Nghệ Sử Dụng

| Thành phần | Phiên bản |
|-----------|----------|
| Java (JDK) | **21** hoặc cao hơn |
| Spring Boot | 3.2.4 |
| Maven | 3.6+ |
| Cơ sở dữ liệu | H2 in-memory (tự động khởi tạo, không cần cài thêm) |
| Frontend | Bootstrap 5.3, Font Awesome 6 |
| API Docs | Springdoc OpenAPI (Swagger UI) |

> ⚠️ **Lưu ý**: Không cần cài đặt database riêng. H2 in-memory tự động khởi tạo và nạp dữ liệu mẫu mỗi lần chạy.

---

## ✅ Yêu Cầu Trước Khi Chạy

Kiểm tra máy đã có các công cụ sau:

### 1. JDK 21+
```bash
java -version
# Kết quả phải hiện: openjdk version "21.x.x" hoặc cao hơn
```
Nếu chưa có: Tải tại https://adoptium.net (chọn JDK 21, LTS)

### 2. Apache Maven 3.6+
```bash
mvn -version
# Kết quả phải hiện: Apache Maven 3.x.x
```
Nếu chưa có: Tải tại https://maven.apache.org/download.cgi  
_(Hoặc dùng `mvnw` / `mvnw.cmd` đi kèm dự án — không cần cài Maven riêng)_

### 3. Git (để clone)
```bash
git --version
```

---

## 🚀 Hướng Dẫn Chạy Dự Án

### Bước 1 — Lấy mã nguồn

**Cách A — Git clone:**
```bash
git clone https://github.com/phamhung080604/ttcn.git
cd ttcn
```

**Cách B — Tải ZIP:**  
Tải file ZIP từ GitHub → Giải nén → Mở terminal trong thư mục vừa giải nén.

### Bước 2 — Chạy ứng dụng

```bash
mvn spring-boot:run
```

Hoặc dùng Maven Wrapper (không cần cài Maven):
```bash
# Windows
mvnw.cmd spring-boot:run

# macOS / Linux
./mvnw spring-boot:run
```

> Lần đầu chạy, Maven sẽ tải dependencies (~2-3 phút, cần kết nối Internet).  
> Các lần sau khởi động trong vòng vài giây.

### Bước 3 — Mở trình duyệt

Khi terminal hiện dòng `Started HotelApplication in X.XXX seconds`, truy cập:

| Trang | URL |
|-------|-----|
| 🏠 Trang khách hàng | http://localhost:8082 |
| 👔 Trang quản trị Admin | http://localhost:8082/admin.html |
| 🛏️ Quản lý phòng | http://localhost:8082/admin-rooms.html |
| 📋 Swagger / API Docs | http://localhost:8082/swagger-ui/index.html |
| 🗄️ H2 Database Console | http://localhost:8082/h2-console |

> **Mật khẩu trang Admin**: `admin123`  
> **H2 Console**: JDBC URL = `jdbc:h2:mem:hotel` · User = `sa` · Password = _(để trống)_

---

## 🗂️ Dữ Liệu Mẫu Có Sẵn

Dữ liệu được tự động nạp từ `src/main/resources/data.sql` mỗi lần khởi động:

| Dữ liệu | Số lượng | Chi tiết |
|---------|---------|---------|
| Loại phòng | 5 | Đơn TC, Đôi TC, Đôi Sang Trọng, Thương Gia, Căn Hộ Áp Mái |
| Phòng | 20 | Tầng 1–5, giá $50–$500/đêm |
| Khách hàng | 20 | Nhiều khách hàng mẫu |
| Đặt phòng | 20+ | Đủ các trạng thái: PENDING, CHECKED_IN, CHECKED_OUT... |
| Dịch vụ | 10 | Ăn sáng, Spa, Giặt ủi, Đưa đón sân bay, v.v. |

---

## 🎯 Tính Năng Chính

### 🧑‍💼 Trang Khách Hàng (`index.html`)
- Tìm phòng trống theo ngày check-in / check-out
- Xem danh sách phòng với giá, loại phòng, trạng thái
- Đặt phòng online — nhập thông tin cá nhân, chọn phòng
- Tra cứu và xem lại đặt phòng bằng mã số

### 🛎️ Trang Quản Trị (`admin.html`) — Mật khẩu: `admin123`
- Dashboard: Tổng doanh thu, số đặt phòng active, tổng khách, tổng phòng
- Danh sách đặt phòng theo thời gian thực
- Check-in / Check-out khách
- Thêm dịch vụ vào đặt phòng (ăn sáng, spa, giặt ủi, v.v.)
- Xem hóa đơn chi tiết: tiền phòng + dịch vụ + thuế 10%
- Ghi nhận thanh toán: CASH / CARD / BANK_TRANSFER
- Hủy đặt phòng

### 🛏️ Quản Lý Phòng (`admin-rooms.html`) — Mật khẩu: `admin123`
- Xem danh sách phòng với trạng thái màu sắc
- Thêm / Sửa / Xóa phòng
- Cập nhật trạng thái: Available, Occupied, Reserved, Needs Cleaning, Out of Service

---

## 📡 API Endpoints Chính

Base URL: `http://localhost:8082/api/v1`

| Method | Endpoint | Mô tả |
|--------|---------|-------|
| GET | `/rooms/available?checkIn=YYYY-MM-DD&checkOut=YYYY-MM-DD` | Phòng trống theo ngày |
| GET | `/rooms` | Tất cả phòng |
| GET | `/rooms/types` | Danh sách loại phòng |
| GET | `/guests` | Tất cả khách hàng |
| POST | `/guests` | Tạo khách hàng mới |
| GET | `/reservations` | Tất cả đặt phòng |
| POST | `/reservations` | Tạo đặt phòng mới |
| PUT | `/reservations/{id}/check-in` | Check-in |
| PUT | `/reservations/{id}/check-out` | Check-out (tự động tạo hóa đơn) |
| PUT | `/reservations/{id}/cancel` | Hủy đặt phòng |
| POST | `/reservations/{id}/services` | Thêm dịch vụ |
| GET | `/reservations/{id}/invoice` | Xem hóa đơn |
| POST | `/invoices/{id}/payments` | Ghi nhận thanh toán |
| GET | `/services` | Danh sách dịch vụ |

> Xem toàn bộ tại Swagger UI: http://localhost:8082/swagger-ui/index.html

---

## 📁 Cấu Trúc Dự Án

```
ttcn/
├── src/
│   ├── main/
│   │   ├── java/com/example/hotel/
│   │   │   ├── controller/        # REST Controllers
│   │   │   ├── service/impl/      # Business Logic
│   │   │   ├── repository/        # JPA Repositories
│   │   │   ├── domain/entity/     # JPA Entities
│   │   │   ├── dto/               # Request / Response DTOs
│   │   │   └── exception/         # Global Exception Handler
│   │   └── resources/
│   │       ├── static/
│   │       │   ├── index.html     # Trang khách hàng
│   │       │   ├── admin.html     # Trang quản trị
│   │       │   ├── admin-rooms.html
│   │       │   ├── js/            # app.js, admin.js, admin-rooms.js
│   │       │   └── css/
│   │       ├── application.yml    # Cấu hình ứng dụng
│   │       └── data.sql           # Dữ liệu mẫu
│   └── test/
├── docs/
│   ├── diagrams.md                # ERD & Sequence Diagram
│   └── postman-collection.json    # Bộ test Postman
└── pom.xml
```

---

## 🔧 Build & Test

```bash
# Biên dịch dự án
mvn clean compile

# Chạy unit tests
mvn clean test

# Đóng gói thành file JAR
mvn clean package -DskipTests

# Chạy file JAR trực tiếp (sau khi build)
java -jar target/hotel-management-0.0.1-SNAPSHOT.jar
```

---

## 📊 Tài Liệu Bổ Sung

- **ERD & Sequence Diagram**: [`docs/diagrams.md`](docs/diagrams.md)
- **Postman Collection**: [`docs/postman-collection.json`](docs/postman-collection.json) — Import vào Postman để test API thủ công
- **Swagger UI**: http://localhost:8082/swagger-ui/index.html — Test API trực tiếp trên trình duyệt

---

## ❓ Xử Lý Lỗi Thường Gặp

| Vấn đề | Nguyên nhân | Giải pháp |
|--------|-------------|-----------|
| `java: command not found` | Chưa cài JDK hoặc chưa thêm vào PATH | Cài JDK 21+ từ https://adoptium.net |
| `mvn: command not found` | Chưa cài Maven | Cài Maven hoặc dùng `mvnw.cmd spring-boot:run` |
| `Port 8082 already in use` | Cổng 8082 đang bị ứng dụng khác chiếm | Mở `src/main/resources/application.yml`, đổi `server.port: 8082` thành `8083` |
| Trang trắng khi vào admin | Bỏ qua hộp thoại nhập mật khẩu | Nhập mật khẩu `admin123` khi được hỏi; tải lại trang nếu cần |
| Không thấy dữ liệu | App chưa khởi động xong | Chờ dòng `Started HotelApplication` xuất hiện trong terminal |
| Lỗi build `JAVA_HOME` | JDK chưa được cấu hình | Đặt biến môi trường `JAVA_HOME` trỏ đến thư mục JDK 21 |
