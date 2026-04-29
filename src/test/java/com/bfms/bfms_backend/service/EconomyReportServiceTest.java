package com.bfms.bfms_backend.service;

import com.bfms.bfms_backend.dtos.res.RevenueResponse;
import com.bfms.bfms_backend.entity.*;
import com.bfms.bfms_backend.repository.*;
import com.bfms.bfms_backend.service.impl.EconomyReportServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EconomyReportServiceTest {

    @Mock
    private ReportRepository reportRepository;
    @Mock
    private DailyTicketStatRepository dailyTicketStatRepository;
    @Mock
    private AdContractRepository adContractRepository;
    @Mock
    private OperationalCostRepository operationalCostRepository;
    @Mock
    private RouteRepository routeRepository;

    @InjectMocks
    private EconomyReportServiceImpl economyReportService;

    private Route route;
    private LocalDate date;

    @BeforeEach
    void setUp() {
        route = new Route();
        route.setId(1);
        route.setRouteNumber("01");
        route.setStopA("Stop A");
        route.setStopB("Stop B");
        
        date = LocalDate.of(2026, 4, 24);
    }

    @Test
    void testGetSystemTotalRevenue_Day() {
        // Mocking route
        when(routeRepository.findAll()).thenReturn(Collections.singletonList(route));
        
        // Mocking bulk fetches
        DailyTicketStat stat = new DailyTicketStat();
        stat.setRoute(route);
        stat.setReportDate(date);
        stat.setRevenueSingleTickets(new BigDecimal("1000000"));
        stat.setTotalPassengers(100);
        when(dailyTicketStatRepository.findAllByReportDateBetween(any(), any()))
                .thenReturn(Collections.singletonList(stat));

        // Mocking ad contracts
        AdContract contract = new AdContract();
        contract.setRoute(route);
        contract.setStartDate(date);
        contract.setPricePerBus(new BigDecimal("550000")); 
        contract.setBusQuantity(2);
        when(adContractRepository.findAllByStartDateBetweenAndApprovalStatusIn(any(), any(), anyList()))
                .thenReturn(Collections.singletonList(contract));

        // Mocking operational costs
        OperationalCost cost = new OperationalCost();
        cost.setRoute(route);
        cost.setCostDate(date);
        cost.setAmount(new BigDecimal("500000"));
        when(operationalCostRepository.findAllByCostDateBetween(any(), any()))
                .thenReturn(Collections.singletonList(cost));

        when(reportRepository.findAllByReportDateBetween(any(), any()))
                .thenReturn(Collections.emptyList());

        // Mocking summary result
        Object[] summary = new Object[]{
            new BigDecimal("1000000.00"), 
            new BigDecimal("1100000.00"), 
            new BigDecimal("400000.00"), 
            new BigDecimal("1200000.00"), 
            100L
        };
        when(reportRepository.getTotalSystemSummary(eq(date), eq(date)))
                .thenReturn(summary);

        RevenueResponse response = economyReportService.getSystemTotalRevenue("day", date);

        assertNotNull(response);
        assertEquals(0, new BigDecimal("1000000.00").compareTo(response.totalTicketRevenue()));
        assertEquals(100, response.totalPassengers());
        
        verify(reportRepository, times(1)).saveAll(anyList());
    }

    @Test
    void testGetSystemTotalRevenue_Month() {
        LocalDate april15 = LocalDate.of(2026, 4, 15);
        LocalDate firstDay = LocalDate.of(2026, 4, 1);
        LocalDate lastDay = LocalDate.of(2026, 4, 30);

        when(routeRepository.findAll()).thenReturn(Collections.singletonList(route));
        when(reportRepository.getTotalSystemSummary(eq(firstDay), eq(lastDay)))
                .thenReturn(new Object[]{BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, 0L});

        economyReportService.getSystemTotalRevenue("month", april15);

        // Kiểm tra xem có gọi sync 1 lần cho cả tháng không
        verify(routeRepository, times(1)).findAll();
        verify(reportRepository, times(1)).getTotalSystemSummary(eq(firstDay), eq(lastDay));
        verify(reportRepository, times(1)).saveAll(anyList());
    }

    @Test
    void testSyncEconomyReports() {
        when(routeRepository.findAll()).thenReturn(Collections.singletonList(route));
        when(dailyTicketStatRepository.findAllByReportDateBetween(any(), any()))
                .thenReturn(Collections.emptyList());
        when(adContractRepository.findAllByStartDateBetweenAndApprovalStatusIn(any(), any(), anyList()))
                .thenReturn(Collections.emptyList());
        when(operationalCostRepository.findAllByCostDateBetween(any(), any()))
                .thenReturn(Collections.emptyList());
        when(reportRepository.findAllByReportDateBetween(any(), any()))
                .thenReturn(Collections.emptyList());

        economyReportService.syncEconomyReports(date, date);

        verify(reportRepository, times(1)).saveAll(anyList());
    }
}
