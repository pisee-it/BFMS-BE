-- 1. Bảng người dùng hệ thống
CREATE TABLE APP_USER (
                          id SERIAL PRIMARY KEY,
                          username VARCHAR(50) UNIQUE NOT NULL,
                          password VARCHAR(255) NOT NULL, -- Lưu mã băm BCrypt
                          fullname VARCHAR(100) NOT NULL,
                          age INT CHECK (age > 0),
                          licence_type VARCHAR(20), -- Cho tài xế hoặc nhân viên
                          avatar_url TEXT,
                          role VARCHAR(20) CHECK (role IN ('OWNER', 'ACCOUNTANT', 'ADVERTISING', 'STAFF', 'ADMIN'))
);

-- 2. Bảng thông báo cho người dùng
CREATE TABLE NOTIFICATION (
                              id SERIAL PRIMARY KEY,
                              user_id INT REFERENCES APP_USER(id),
                              message TEXT NOT NULL,
                              is_read BOOLEAN DEFAULT FALSE,
                              created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- 3. Bảng tuyến xe buýt
CREATE TABLE ROUTE (
                       id SERIAL PRIMARY KEY,
                       route_number VARCHAR(20) NOT NULL,
                       stop_A VARCHAR(100),
                       stop_B VARCHAR(100),
                       path TEXT,
                       distance_AB DECIMAL(10,2) CHECK (distance_AB >= 0),
                       distance_BA DECIMAL(10,2) CHECK (distance_BA >= 0),
                       operation_starttime TIME,
                       operation_endtime TIME,
                       price DECIMAL(15,2) CHECK (price >= 0)
);

-- 4. Bảng nốt (lượt chạy) của tuyến
CREATE TABLE NODE (
                      id SERIAL PRIMARY KEY,
                      route_id INT REFERENCES ROUTE(id),
                      node_number INT,
                      execution_date DATE NOT NULL,
                      direction SMALLINT CHECK (direction IN (1, 2)), -- 1: A-B, 2: B-A
                      description TEXT,
                      total_passengers INT DEFAULT 0
);

-- 5. Bảng thông tin xe buýt
CREATE TABLE BUS (
                     id SERIAL PRIMARY KEY,
                     route_id INT REFERENCES ROUTE(id),
                     bus_model VARCHAR(100),
                     manufacturer VARCHAR(100),
                     capacity INT CHECK (capacity > 0),
                     yom INT, -- Năm sản xuất
                     license_plate VARCHAR(20) UNIQUE NOT NULL,
                     status VARCHAR(50),
                     is_advertised BOOLEAN DEFAULT FALSE
);

-- 6. Bảng báo cáo kinh tế
CREATE TABLE ECONOMY_REPORT (
                                id SERIAL PRIMARY KEY,
                                route_id INT REFERENCES ROUTE(id),
                                report_date DATE NOT NULL,
                                total_ticket_revenue DECIMAL(18,2),
                                total_ad_revenue DECIMAL(18,2),
                                total_passengers INT,
                                tax_deduction DECIMAL(18,2),
                                net_profit DECIMAL(18,2)
);

-- 7. Bảng thống kê vé hàng ngày
CREATE TABLE DAILY_TICKET_STAT (
                                   id SERIAL PRIMARY KEY,
                                   route_id INT REFERENCES ROUTE(id),
                                   report_date DATE NOT NULL,
                                   single_ticket_count INT DEFAULT 0,
                                   monthly_ticket_count INT DEFAULT 0,
                                   total_passengers INT,
                                   revenue_single_tickets DECIMAL(18,2)
);

-- 8. Bảng ca chạy của xe
CREATE TABLE BUS_SHIFT (
                           id SERIAL PRIMARY KEY,
                           node_id INT REFERENCES NODE(id),
                           bus_id INT REFERENCES BUS(id),
                           execution_date DATE, -- Logic cần khớp với NODE.execution_date
                           shift_order INT,
                           planned_departuretime TIME,
                           planned_arrivaltime TIME,
                           status VARCHAR(50),
                           total_single_tickets INT CHECK (total_single_tickets >= 0),
                           total_monthly_tickets INT CHECK (total_monthly_tickets >= 0),
                           shift_revenue DECIMAL(15,2)
);

-- 9. Bảng chi tiết vé thu thập
CREATE TABLE TICKET (
                        id SERIAL PRIMARY KEY,
                        bus_shift_id INT REFERENCES BUS_SHIFT(id),
                        type VARCHAR(50) NOT NULL, -- Vé lượt/Vé tháng
                        quantity INT NOT NULL CHECK (quantity > 0)
);

-- 10. Bảng công ty quảng cáo
CREATE TABLE AD_COMPANY (
                            id SERIAL PRIMARY KEY,
                            name VARCHAR(200),
                            tax_code VARCHAR(50) UNIQUE NOT NULL,
                            contact TEXT
);

-- 11. Bảng hợp đồng quảng cáo
CREATE TABLE AD_CONTRACT (
                             id SERIAL PRIMARY KEY,
                             company_id INT REFERENCES AD_COMPANY(id),
                             route_id INT REFERENCES ROUTE(id),
                             start_date DATE,
                             end_date DATE CHECK (end_date > start_date),
                             price_per_bus DECIMAL(15,2) CHECK (price_per_bus >= 0),
                             bus_quantity INT CHECK (bus_quantity > 0),
                             approval_status VARCHAR(50),
                             contract_file_url TEXT
);

-- 12. Bảng phân bổ quảng cáo lên xe
CREATE TABLE AD_ASSIGNMENT (
                               id SERIAL PRIMARY KEY,
                               ad_contract_id INT REFERENCES AD_CONTRACT(id),
                               bus_id INT REFERENCES BUS(id),
                               position VARCHAR(100),
                               status VARCHAR(50)
);