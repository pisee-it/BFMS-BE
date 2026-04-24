package com.bfms.bfms_backend.service;

import com.bfms.bfms_backend.dtos.res.TicketStatisticsResponse;
import java.time.LocalDate;

public interface TicketService {
    /**
     * Lấy thống kê vé theo tuyến và ngày.
     * @param routeId ID của tuyến xe
     * @param date Ngày thống kê
     * @return DTO chứa thông tin thống kê
     */
    TicketStatisticsResponse getTicketStatistics(Integer routeId, LocalDate date);
}
