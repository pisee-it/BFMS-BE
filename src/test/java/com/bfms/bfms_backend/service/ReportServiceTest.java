package com.bfms.bfms_backend.service;

import com.bfms.bfms_backend.dtos.res.RouteReportResponse;
import com.bfms.bfms_backend.entity.Route;
import com.bfms.bfms_backend.repository.ReportRepository;
import com.bfms.bfms_backend.repository.RouteRepository;
import com.bfms.bfms_backend.service.impl.ReportServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock
    private ReportRepository reportRepository;
    @Mock
    private RouteRepository routeRepository;
    @Mock
    private EconomyReportService economyReportService;

    @InjectMocks
    private ReportServiceImpl reportService;

    private Route route;
    private LocalDate startDate;
    private LocalDate endDate;

    @BeforeEach
    void setUp() {
        route = new Route();
        route.setId(1);
        route.setRouteNumber("01");
        route.setStopA("A");
        route.setStopB("B");

        startDate = LocalDate.of(2026, 4, 1);
        endDate = LocalDate.of(2026, 4, 30);
    }

    @Test
    void testGetRouteReport_Success() {
        when(routeRepository.findById(1)).thenReturn(Optional.of(route));
        
        // Mocking summary result: ticket, ad, passengers, tax, net
        Object[] summary = new Object[]{
            new BigDecimal("1000000"), 
            new BigDecimal("500000"), 
            100L, 
            new BigDecimal("100000"), 
            new BigDecimal("1400000")
        };
        when(reportRepository.getSummaryByRouteAndDateRange(eq(1), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(summary);

        RouteReportResponse response = reportService.getRouteReport(1, startDate, endDate);

        assertNotNull(response);
        assertEquals("01", response.routeNumber());
        assertEquals("A - B", response.routeName());
        assertEquals(0, new BigDecimal("1000000").compareTo(response.totalTicketRevenue()));
        assertEquals(100, response.totalPassengers());
        
        verify(economyReportService, atLeastOnce()).syncEconomyReports(eq(1), any(LocalDate.class), any(LocalDate.class));
    }

    @Test
    void testGetRouteReport_NetProfitFormulaValidation() {
        when(routeRepository.findById(1)).thenReturn(Optional.of(route));
        
        Object[] summary = new Object[]{
            new BigDecimal("1000000"), 
            new BigDecimal("1100000"), 
            100L, 
            new BigDecimal("500000"), 
            new BigDecimal("1600000")
        };
        when(reportRepository.getSummaryByRouteAndDateRange(anyInt(), any(), any())).thenReturn(summary);

        RouteReportResponse response = reportService.getRouteReport(1, startDate, endDate);

        BigDecimal calculatedNetProfit = response.totalTicketRevenue()
                .add(response.totalAdRevenue())
                .subtract(response.taxDeduction());
        
        assertEquals(0, calculatedNetProfit.compareTo(response.netProfit()), 
                "Lợi nhuận ròng phải bằng (Vé + Quảng cáo - Thuế) khi chi phí vận hành bằng 0");
    }

    @Test
    void testGetRouteReport_CallsSyncOnceForDateRange() {
        when(routeRepository.findById(1)).thenReturn(Optional.of(route));
        when(reportRepository.getSummaryByRouteAndDateRange(anyInt(), any(), any())).thenReturn(new Object[5]);

        reportService.getRouteReport(1, startDate, endDate);

        // Phải gọi sync 1 lần cho cả dải ngày
        verify(economyReportService, times(1)).syncEconomyReports(eq(1), any(LocalDate.class), any(LocalDate.class));
    }

    @Test
    void testExportExcel_NotNull() {
        when(routeRepository.findById(1)).thenReturn(Optional.of(route));
        Object[] summary = new Object[]{
            new BigDecimal("1000000"), new BigDecimal("500000"), 100L, new BigDecimal("100000"), new BigDecimal("1400000")
        };
        when(reportRepository.getSummaryByRouteAndDateRange(anyInt(), any(), any())).thenReturn(summary);

        byte[] excel = reportService.exportRouteReportToExcel(1, startDate, endDate);

        assertNotNull(excel);
        assertTrue(excel.length > 0);
    }
}
