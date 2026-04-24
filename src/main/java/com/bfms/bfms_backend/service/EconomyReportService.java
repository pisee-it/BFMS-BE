package com.bfms.bfms_backend.service;

import com.bfms.bfms_backend.dtos.res.RevenueResponse;
import java.time.LocalDate;

public interface EconomyReportService {
    /**
     * Lấy báo cáo tổng doanh thu hệ thống theo timeframe và ngày cụ thể.
     */
    RevenueResponse getSystemTotalRevenue(String timeframe, LocalDate date);

    /**
     * Đồng bộ dữ liệu báo cáo kinh tế cho tất cả các tuyến trong một ngày cụ thể.
     */
    void syncEconomyReports(LocalDate date);
}
