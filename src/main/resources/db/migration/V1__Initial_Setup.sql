-- =============================================================
-- BFMS Database Schema  v2.0
-- Changelog từ v1:
--   [FIX-1]  BUS_SHIFT: Xoá execution_date (redundant, lấy qua NODE join)
--   [ADD-1]  BUS_SHIFT: Thêm driver_id FK → APP_USER
--   [ADD-2]  BUS_SHIFT: Thêm created_at audit field
--   [FIX-2]  BUS.status: Thêm CHECK constraint giá trị hợp lệ
--   [FIX-3]  AD_CONTRACT.approval_status: Thêm CHECK constraint
--   [ADD-3]  AD_CONTRACT: Thêm created_at audit field
--   [ADD-4]  ECONOMY_REPORT: Thêm created_at audit field
--   [DOC-1]  NODE.total_passengers: Ghi chú derived field
--   [DOC-2]  DAILY_TICKET_STAT: Ghi chú derived fields
-- =============================================================

-- 1. Bảng người dùng hệ thống
CREATE TABLE APP_USER (
    id          SERIAL PRIMARY KEY,
    username    VARCHAR(50)  UNIQUE NOT NULL,
    password    VARCHAR(255) NOT NULL,                          -- Lưu mã băm BCrypt
    fullname    VARCHAR(100) NOT NULL,
    age         INT          CHECK (age > 0),
    licence_type VARCHAR(20),                                   -- Cho tài xế hoặc nhân viên
    avatar_url  TEXT,
    role        VARCHAR(20) NOT NULL CHECK (role IN (
                                   'OWNER', 'ACCOUNTANT', 'ADVERTISING', 'STAFF', 'ADMIN'
    ))
);

-- 1.1. Bảng Refresh Token
CREATE TABLE REFRESH_TOKEN (
    id          SERIAL PRIMARY KEY,
    token       VARCHAR(255) UNIQUE NOT NULL,
    expiry_date TIMESTAMPTZ  NOT NULL,
    user_id     INT          NOT NULL REFERENCES APP_USER(id)
);

-- 2. Bảng thông báo cho người dùng
CREATE TABLE NOTIFICATION (
    id         SERIAL PRIMARY KEY,
    user_id    INT  REFERENCES APP_USER(id),
    message    TEXT NOT NULL,
    is_read    BOOLEAN      DEFAULT FALSE,
    created_at TIMESTAMPTZ  DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- 3. Bảng tuyến xe buýt
CREATE TABLE ROUTE (
    id                  SERIAL PRIMARY KEY,
    route_number        VARCHAR(20)    NOT NULL,
    stop_A              VARCHAR(100)   NOT NULL,
    stop_B              VARCHAR(100)   NOT NULL,
    path                TEXT           NOT NULL,
    distance_AB         DECIMAL(10,2)  CHECK (distance_AB >= 0),
    distance_BA         DECIMAL(10,2)  CHECK (distance_BA >= 0),
    operation_start TIME,
    operation_end   TIME,
    price               DECIMAL(15,2)  CHECK (price >= 0)
);

-- 4. Bảng nốt (lượt chạy) của tuyến
CREATE TABLE NODE (
    id               SERIAL PRIMARY KEY,
    route_id         INT  REFERENCES ROUTE(id),
    node_number      INT,
    execution_date   DATE  NOT NULL,
    description      TEXT,
    -- [DOC-1] Derived field: được tính = SUM(BUS_SHIFT.total_single_tickets + total_monthly_tickets)
    --         Không ghi trực tiếp; Service layer cập nhật sau khi hoàn thành ca chạy
    total_passengers INT  DEFAULT 0
);

-- 5. Bảng thông tin xe buýt
CREATE TABLE BUS (
    id            SERIAL PRIMARY KEY,
    route_id      INT  NOT NULL REFERENCES ROUTE(id),
    bus_model     VARCHAR(100) NOT NULL,
    manufacturer  VARCHAR(100) NOT NULL,
    capacity      INT    NOT NULL CHECK (capacity > 0),
    yom           INT    NOT NULL,                               -- Năm sản xuất
    license_plate VARCHAR(20) UNIQUE NOT NULL,
    -- [FIX-2] Thêm CHECK để tránh ghi sai trạng thái
    status        VARCHAR(20) NOT NULL CHECK (status IN ('ACTIVE', 'INACTIVE', 'MAINTENANCE')),
    is_advertised BOOLEAN DEFAULT FALSE NOT NULL                -- Đồng bộ với AD_ASSIGNMENT
);

-- 6. Bảng báo cáo kinh tế
CREATE TABLE ECONOMY_REPORT (
    id                  SERIAL PRIMARY KEY,
    route_id            INT  REFERENCES ROUTE(id),
    report_date         DATE NOT NULL,
    total_ticket_revenue DECIMAL(18,2),                        -- SUM từ DAILY_TICKET_STAT
    total_ad_revenue    DECIMAL(18,2),                         -- Phân bổ từ AD_CONTRACT
    total_passengers    INT,                                    -- SUM từ DAILY_TICKET_STAT
    tax_deduction       DECIMAL(18,2),
    net_profit          DECIMAL(18,2),
    -- [ADD-4] Audit field
    created_at          TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

-- 7. Bảng thống kê vé hàng ngày
CREATE TABLE DAILY_TICKET_STAT (
    id                   SERIAL PRIMARY KEY,
    route_id             INT  NOT NULL REFERENCES ROUTE(id),
    report_date          DATE NOT NULL,
    single_ticket_count  INT  DEFAULT 0,
    monthly_ticket_count INT  DEFAULT 0,
    -- [DOC-2] Derived: total_passengers = single_ticket_count + monthly_ticket_count
    --         Được Service layer tính và lưu; không nhập thủ công
    total_passengers     INT,
    -- [DOC-2] Derived: revenue_single_tickets = single_ticket_count * ROUTE.price
    --         Được Service layer tính tại thời điểm tổng hợp ca chạy
    revenue_single_tickets DECIMAL(18,2)
);

-- 8. Bảng ca chạy của xe
CREATE TABLE BUS_SHIFT (
    id                    SERIAL PRIMARY KEY,
    node_id               INT  REFERENCES NODE(id),
    bus_id                INT  REFERENCES BUS(id),
    -- [ADD-1] Tài xế thực hiện ca — bắt buộc role = 'STAFF' (validate ở Service layer)
    driver_id             INT  REFERENCES APP_USER(id),
    -- [FIX-1] Đã xoá execution_date: dùng NODE.execution_date qua join để tránh inconsistency
    shift_order           INT,
    direction        SMALLINT CHECK (direction IN (1, 2)),      -- 1: A→B, 2: B→A
    planned_departuretime TIME,
    planned_arrivaltime   TIME,
    status                VARCHAR(50),
    total_single_tickets  INT  CHECK (total_single_tickets >= 0),
    total_monthly_tickets INT  CHECK (total_monthly_tickets >= 0),
    -- shift_revenue phải được audit khớp với SUM(TICKET.quantity * ROUTE.price)
    shift_revenue         DECIMAL(15,2),
    -- [ADD-2] Audit field
    created_at            TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

-- 9. Bảng chi tiết vé thu thập
CREATE TABLE TICKET (
    id           SERIAL PRIMARY KEY,
    bus_shift_id INT  REFERENCES BUS_SHIFT(id),
    type         VARCHAR(50) NOT NULL,                          -- 'SINGLE' | 'MONTHLY'
    quantity     INT         NOT NULL CHECK (quantity > 0)
);

-- 10. Bảng công ty quảng cáo
CREATE TABLE AD_COMPANY (
    id       SERIAL PRIMARY KEY,
    name     VARCHAR(200),
    tax_code VARCHAR(50) UNIQUE NOT NULL,
    contact  TEXT
);

-- 11. Bảng hợp đồng quảng cáo
CREATE TABLE AD_CONTRACT (
    id                SERIAL PRIMARY KEY,
    company_id        INT  NOT NULL REFERENCES AD_COMPANY(id),
    route_id          INT  NOT NULL REFERENCES ROUTE(id),
    start_date        DATE NOT NULL,
    end_date          DATE NOT NULL CHECK (end_date > start_date),
    price_per_bus     DECIMAL(15,2)  NOT NULL CHECK (price_per_bus >= 0),
    bus_quantity      INT            NOT NULL CHECK (bus_quantity > 0),
    -- [FIX-3] Thêm CHECK để tránh ghi sai trạng thái phê duyệt
    approval_status   VARCHAR(20) NOT NULL CHECK (approval_status IN (
                                      'PENDING', 'APPROVED', 'PAID', 'REJECTED'
    )),
    contract_file_url TEXT,
    -- [ADD-3] Audit field
    created_at        TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

-- 12. Bảng phân bổ quảng cáo lên xe
CREATE TABLE AD_ASSIGNMENT (
    id              SERIAL PRIMARY KEY,
    ad_contract_id  INT  REFERENCES AD_CONTRACT(id),
    bus_id          INT  REFERENCES BUS(id),
    -- Trạng thái dán: 'ACTIVE' | 'REMOVED'
    status          VARCHAR(20)
);

-- 13. Bảng chi phí vận hành
CREATE TABLE OPERATIONAL_COST (
    id          SERIAL PRIMARY KEY,
    route_id    INT  NOT NULL REFERENCES ROUTE(id),
    cost_date   DATE NOT NULL,
    type        VARCHAR(50) NOT NULL CHECK (type IN ('FUEL', 'MAINTENANCE', 'SALARY', 'OTHER')),
    amount      DECIMAL(18,2) NOT NULL CHECK (amount >= 0),
    description TEXT,
    created_at  TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

-- 14. Index tối ưu hoá truy vấn
-- Hỗ trợ báo cáo doanh thu theo tuyến và ngày (US-01, US-02)
CREATE INDEX idx_daily_ticket_stat_route_date ON DAILY_TICKET_STAT(route_id, report_date);

-- Hỗ trợ tìm kiếm ca chạy theo nốt và trạng thái (US-09)
CREATE INDEX idx_bus_shift_node_status ON BUS_SHIFT(node_id, status);
