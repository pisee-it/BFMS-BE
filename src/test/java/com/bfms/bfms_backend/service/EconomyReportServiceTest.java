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
        
        // Mocking ticket stats
        DailyTicketStat stat = new DailyTicketStat();
        stat.setRoute(route);
        stat.setRevenueSingleTickets(new BigDecimal("1000000"));
        stat.setTotalPassengers(100);
        when(dailyTicketStatRepository.findByRouteIdAndReportDate(eq(1), eq(date)))
                .thenReturn(Optional.of(stat));

        // Mocking ad contracts
        AdContract contract = new AdContract();
        contract.setRoute(route);
        contract.setPricePerBus(new BigDecimal("550000")); // Gross: 550k * 2 = 1.1M
        contract.setBusQuantity(2);
        when(adContractRepository.findAllByStartDateAndApprovalStatusIn(eq(date), anyList()))
                .thenReturn(Collections.singletonList(contract));

        // Mocking operational costs
        OperationalCost cost = new OperationalCost();
        cost.setAmount(new BigDecimal("500000"));
        when(operationalCostRepository.findByRouteIdAndCostDate(eq(1), eq(date)))
                .thenReturn(Collections.singletonList(cost));

        // Mocking summary result (Object array from repository query)
        // SUM(ticket), SUM(ad), SUM(tax), SUM(net), SUM(passengers)
        // Ticket: 1M
        // Ad Gross: 1.1M -> Net Ad: 1M, VAT: 100k
        // Costs: 0.5M
        // Operating Profit: (1M + 1M) - 0.5M = 1.5M
        // TNDN (20%): 300k
        // Tax Deduction: 100k + 300k = 400k
        // Net Profit: 1.5M - 300k = 1.2M
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
        assertEquals(0, new BigDecimal("1100000.00").compareTo(response.totalAdRevenue()));
        assertEquals(0, new BigDecimal("400000.00").compareTo(response.taxDeduction()));
        assertEquals(0, new BigDecimal("1200000.00").compareTo(response.netProfit()));
        assertEquals(100, response.totalPassengers());
        
        verify(reportRepository, times(1)).save(any(EconomyReport.class));
    }

    @Test
    void testSyncEconomyReports() {
        when(routeRepository.findAll()).thenReturn(Collections.singletonList(route));
        when(dailyTicketStatRepository.findByRouteIdAndReportDate(anyInt(), any(LocalDate.class)))
                .thenReturn(Optional.empty());
        when(adContractRepository.findAllByStartDateAndApprovalStatusIn(any(LocalDate.class), anyList()))
                .thenReturn(Collections.emptyList());
        when(operationalCostRepository.findByRouteIdAndCostDate(anyInt(), any(LocalDate.class)))
                .thenReturn(Collections.emptyList());
        when(reportRepository.findByRouteIdAndReportDate(anyInt(), any(LocalDate.class)))
                .thenReturn(Optional.empty());

        economyReportService.syncEconomyReports(date);

        verify(reportRepository, times(1)).save(any(EconomyReport.class));
    }
}
