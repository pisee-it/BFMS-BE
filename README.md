# Bus Finance Management System (BFMS) - Backend

[![Java Version](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

## 🚌 Overview
The **Bus Finance Management System (BFMS)** is a comprehensive financial and operational management platform designed for bus enterprises. The system focuses on controlling and optimizing two primary revenue streams:
1.  **Ticket Sales**: Tracking daily single and monthly ticket revenue across all routes.
2.  **Body Advertising**: Managing advertising contracts and decal placements on the bus fleet.

BFMS streamlines operations from the field (drivers/staff) to the back office (accountants/owners), ensuring financial transparency and data-driven decision-making.

---

## 🚀 Key Features

### 🔐 Authentication & RBAC
- **JWT-based Security**: Secure stateless authentication using Bearer tokens.
- **Refresh Token Mechanism**: Extended session management with database-backed refresh tokens.
- **Role-Based Access Control (RBAC)**: Detailed permissions for `OWNER`, `ADMIN`, `ACCOUNTANT`, `ADVERTISING`, and `STAFF`.
- **Logout Endpoint**: Secure session termination by invalidating Refresh Tokens in the database.

### 🛣 Infrastructure Management
- **Routes & Nodes**: Full CRUD for bus routes with auto-calculated pricing based on distance.
- **Bus Fleet**: Management of vehicle specifications, status tracking, and advertising availability.

### 💰 Revenue & Operations
- **Shift Management**: Real-time tracking of bus shifts.
- **US-03 Workflow**: Secure transaction pattern for completing shifts, auditing ticket counts, and updating aggregate statistics.
- **Automated Calculations**: Derived fields for revenue, taxes (VAT, Corporate Tax), and net profit.

### 📢 Advertising Module
- **Contract Lifecycle**: From creation (Advertising) to approval/payment (Accountant).
- **Ad Assignment**: Precise tracking of decal placements on specific buses.
- **Expiry Alerts**: Automated `needsAttention` flags for expiring contracts.

### 📊 Reporting & Notifications
- **Financial Reports**: Daily, monthly, and yearly revenue summaries.
- **Excel Export**: Generate detailed route-based financial reports in `.xlsx` format.
- **Internal Notifications**: Polling-based notification system for system alerts and approval workflows.

---

## 🛠 Tech Stack

- **Framework**: Spring Boot 4.0.5
- **Language**: Java 17
- **Security**: Spring Security + JJWT
- **Database**: PostgreSQL (Supabase)
- **Persistence**: Spring Data JPA (Hibernate)
- **Migrations**: Flyway
- **Mappers**: MapStruct
- **Logging**: SLF4J + Logback + Spring AOP (Performance monitoring)
- **Documentation**: SpringDoc OpenAPI (Swagger UI)
- **Utilities**: Lombok, Dotenv, Apache POI

---

## ⚙️ Setup & Installation

### Prerequisites
- JDK 17
- Maven 3.9.x
- PostgreSQL Instance

### Configuration
1.  Clone the repository.
2.  Create a `.env` file in the root directory with the following variables:
    ```env
    DB_URL=jdbc:postgresql://your-db-url:5432/postgres
    DB_USERNAME=your-username
    DB_PASSWORD=your-password
    JWT_SECRET=your-secure-jwt-secret
    ```
3.  The application will automatically load these variables via `dotenv-java`.

### Running the App
```bash
./mvnw spring-boot:run
```

### Running Tests
```bash
./mvnw test
```

---

## 📖 API Documentation
Once the application is running, you can access the interactive Swagger UI at:
`http://localhost:8080/swagger-ui.html`

The API is fully documented in **Vietnamese**, providing detailed descriptions for every endpoint, request body, and response structure. It is grouped into 11 logical tags: `Authentication`, `Buses`, `Routes`, `Nodes`, `Shifts`, `Advertising`, `Notifications`, `Revenue`, `Tickets`, `Files`, and `Reports`.

---

## 🏗 Architecture & Conventions
The project follows a strict **Layered Architecture**:
`Controller` ➔ `Service (Interface)` ➔ `ServiceImpl` ➔ `Repository` ➔ `Entity`

- **DTOs**: All API interactions use Java `record` for immutable Data Transfer Objects.
- **Exception Handling**: Centralized handling via `GlobalExceptionHandler` and custom `AppException` with standardized `ErrorCode`.
- **Transactions**: Service-level `@Transactional` management for data integrity.

---

## 🤖 Agent Context
This project is optimized for AI-driven development. The `agent-context/` directory contains the "collective memory" and instructions for AI agents:
- `PROJECT_CONTEXT.md`: High-level business and technical overview.
- `PROJECT_MEMORY.md`: Accumulation of decisions and completed tasks.
- `CODING_CONVENTIONS.md`: Strict rules for code style and architecture.

---
© 2026 BFMS Project Team. All rights reserved.
