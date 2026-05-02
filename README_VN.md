# Hệ thống Quản lý Tài chính Xe buýt (BFMS) - Backend

[![Java Version](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.5-brightgreen.svg)](https://spring.io/projects/spring-boot)

## 🚌 Tổng quan
**BFMS (Bus Finance Management System)** là một nền tảng quản lý tài chính và vận hành toàn diện dành cho các xí nghiệp xe buýt. Hệ thống tập trung vào việc kiểm soát và tối ưu hóa hai nguồn thu chính:
1.  **Doanh thu vé**: Theo dõi vé lượt và vé tháng hàng ngày trên tất cả các tuyến.
2.  **Quảng cáo trên thân xe**: Quản lý hợp đồng quảng cáo và phân bổ decal lên đội xe buýt.

BFMS giúp số hóa quy trình từ hiện trường (nhân viên/tài xế) đến văn phòng (kế toán/chủ doanh nghiệp), đảm bảo tính minh bạch tài chính và hỗ trợ ra quyết định dựa trên dữ liệu.

---

## 🚀 Tính năng chính

### 🔐 Xác thực & Phân quyền (RBAC)
- **Bảo mật JWT**: Xác thực không trạng thái (stateless) qua Bearer token.
- **Cơ chế Refresh Token**: Quản lý phiên đăng nhập dài hạn, lưu trữ an toàn trong Database.
- **Phân quyền chi tiết**: Hỗ trợ các vai trò `OWNER` (Chủ), `ADMIN` (Quản trị), `ACCOUNTANT` (Kế toán), `ADVERTISING` (Quảng cáo), và `STAFF` (Nhân viên).
- **Endpoint Đăng xuất**: Hủy hiệu lực phiên làm việc bằng cách xóa Refresh Token trong DB một cách an toàn.

### 🛣 Quản lý hạ tầng
- **Tuyến xe & Nốt chạy**: Quản lý đầy đủ thông tin tuyến, tự động tính giá vé dựa trên khoảng cách.
- **Đội xe buýt**: Theo dõi thông số xe, trạng thái hoạt động và tình trạng quảng cáo.

### 💰 Vận hành & Doanh thu
- **Quản lý Ca chạy (Shift)**: Theo dõi lộ trình thực tế của từng xe.
- **Luồng nghiệp vụ US-03**: Quy trình chốt ca bảo mật, đối soát số lượng vé và cập nhật thống kê tự động qua Transaction.
- **Tính toán tự động**: Tự động tính toán doanh thu, các loại thuế (VAT, TNDN) và lợi nhuận ròng.

### 📢 Module Quảng cáo
- **Vòng đời hợp đồng**: Từ khâu tạo mới (Nhân viên QC) đến phê duyệt/xác nhận thanh toán (Kế toán).
- **Phân bổ quảng cáo**: Theo dõi chính xác vị trí dán decal trên từng xe cụ thể.
- **Cảnh báo hết hạn**: Tự động đánh dấu `needsAttention` cho các hợp đồng sắp hoặc đã hết hạn.

### 📊 Báo cáo & Thông báo
- **Báo cáo tài chính**: Tổng hợp doanh thu theo ngày, tháng, năm.
- **Xuất Excel**: Sinh báo cáo chi tiết tuyến xe định dạng `.xlsx` chuyên nghiệp.
- **Thông báo nội bộ**: Hệ thống thông báo thời gian thực (polling) cho các phê duyệt và cảnh báo hệ thống.

---

## 🛠 Công nghệ sử dụng

- **Framework**: Spring Boot 4.0.5
- **Ngôn ngữ**: Java 17
- **Bảo mật**: Spring Security + JJWT
- **Cơ sở dữ liệu**: PostgreSQL (Supabase)
- **ORM**: Spring Data JPA (Hibernate)
- **Migration**: Flyway
- **Mapping**: MapStruct
- **Logging**: SLF4J + Logback + Spring AOP (Giám sát hiệu năng)
- **Tài liệu API**: SpringDoc OpenAPI (Swagger UI)
- **Tiện ích**: Lombok, Dotenv, Apache POI

---

## ⚙️ Cài đặt và Vận hành

### Yêu cầu hệ thống
- JDK 17 trở lên.
- Maven 3.9.x.
- Cơ sở dữ liệu PostgreSQL.

### Cấu hình
1.  Clone repository.
2.  Tạo file `.env` tại thư mục gốc với các biến sau:
    ```env
    DB_URL=jdbc:postgresql://your-db-url:5432/postgres
    DB_USERNAME=your-username
    DB_PASSWORD=your-password
    JWT_SECRET=your-secure-jwt-secret
    ```
3.  Ứng dụng sẽ tự động nạp các biến này thông qua thư viện `dotenv-java`.

### Khởi chạy
```bash
./mvnw spring-boot:run
```

Sau khi ứng dụng khởi động, truy cập Swagger UI tại:
`http://localhost:8080/swagger-ui.html`

Tài liệu API được **Việt hóa hoàn toàn**, mô tả chi tiết từng endpoint, cấu trúc dữ liệu đầu vào (Request) và đầu ra (Response) kèm theo ví dụ minh họa sinh động.

---

## 🏗 Kiến trúc và Quy ước
Dự án tuân thủ chặt chẽ **Kiến trúc phân tầng (Layered Architecture)**:
`Controller` ➔ `Service (Interface)` ➔ `ServiceImpl` ➔ `Repository` ➔ `Entity`

- **DTOs**: Sử dụng Java `record` để đảm bảo tính bất biến (immutable).
- **Xử lý lỗi**: Tập trung qua `GlobalExceptionHandler` với mã lỗi `ErrorCode` chuẩn hóa.
- **Transaction**: Quản lý tại tầng Service để đảm bảo tính toàn vẹn dữ liệu.

---

## 🤖 Bối cảnh Agent
Dự án này được tối ưu hóa cho phát triển bằng AI. Thư mục `agent-context/` chứa "trí nhớ tập thể" và chỉ dẫn cho các Agent:
- `PROJECT_CONTEXT.md`: Tổng quan nghiệp vụ và kỹ thuật.
- `PROJECT_MEMORY.md`: Nhật ký các quyết định và tác vụ đã hoàn thành.
- `CODING_CONVENTIONS.md`: Các quy tắc bắt buộc về phong cách code và kiến trúc.

---
© 2026 Đội ngũ phát triển BFMS. Bảo lưu mọi quyền.
