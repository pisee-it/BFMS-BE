package com.bfms.bfms_backend.service;

import com.bfms.bfms_backend.dtos.res.RouteReportResponse;
import java.time.LocalDate;

/**
 * Service xử lý các yêu cầu liên quan đến báo cáo và xuất dữ liệu.
 */
public interface ReportService {
    
    /**
     * Lấy dữ liệu báo cáo cho một tuyến xe trong khoảng thời gian.
     */
    RouteReportResponse getRouteReport(Integer routeId, LocalDate startDate, LocalDate endDate);

    /**
     * Xuất báo cáo tuyến xe ra file Excel.
     * @return byte array của file Excel .xlsx
     */
    byte[] exportRouteReportToExcel(Integer routeId, LocalDate startDate, LocalDate endDate);
}
