package com.bfms.bfms_backend.service;

import com.bfms.bfms_backend.dtos.res.TicketStatisticsResponse;
import com.bfms.bfms_backend.entity.DailyTicketStat;
import com.bfms.bfms_backend.entity.Route;
import com.bfms.bfms_backend.repository.DailyTicketStatRepository;
import com.bfms.bfms_backend.repository.RouteRepository;
import com.bfms.bfms_backend.service.impl.TicketServiceImpl;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TicketServiceTest {

    @Mock
    private DailyTicketStatRepository dailyTicketStatRepository;

    @Mock
    private RouteRepository routeRepository;

    @InjectMocks
    private TicketServiceImpl ticketService;

    private Route route;
    private LocalDate date;

    @BeforeEach
    void setUp() {
        route = new Route();
        route.setId(1);
        route.setRouteNumber("01");
        route.setStopA("Ben xe My Dinh");
        route.setStopB("Ben xe Giap Bat");
        route.setPrice(new BigDecimal("8000"));

        date = LocalDate.of(2026, 4, 24);
    }

    @Test
    void testGetTicketStatistics_Success() {
        DailyTicketStat stat = new DailyTicketStat();
        stat.setRoute(route);
        stat.setReportDate(date);
        stat.setSingleTicketCount(100);
        stat.setMonthlyTicketCount(50);
        stat.setTotalPassengers(150);
        stat.setRevenueSingleTickets(new BigDecimal("800000"));

        when(routeRepository.findById(1)).thenReturn(Optional.of(route));
        when(dailyTicketStatRepository.findByRouteIdAndReportDate(eq(1), eq(date)))
                .thenReturn(Optional.of(stat));

        TicketStatisticsResponse response = ticketService.getTicketStatistics(1, date);

        assertNotNull(response);
        assertEquals(1, response.routeId());
        assertTrue(response.routeName().contains("01"));
        assertEquals(100, response.singleTicketCount());
        assertEquals(50, response.monthlyTicketCount());
        assertEquals(150, response.totalPassengers());
        assertEquals(0, new BigDecimal("800000").compareTo(response.revenueSingleTickets()));
    }

    @Test
    void testGetTicketStatistics_NoData_ShouldReturnEmpty() {
        when(routeRepository.findById(1)).thenReturn(Optional.of(route));
        when(dailyTicketStatRepository.findByRouteIdAndReportDate(eq(1), eq(date)))
                .thenReturn(Optional.empty());

        TicketStatisticsResponse response = ticketService.getTicketStatistics(1, date);

        assertNotNull(response);
        assertEquals(1, response.routeId());
        assertEquals(0, response.singleTicketCount());
        assertEquals(0, response.monthlyTicketCount());
        assertEquals(0, response.totalPassengers());
        assertEquals(0, BigDecimal.ZERO.compareTo(response.revenueSingleTickets()));
    }

    @Test
    void testGetTicketStatistics_RouteNotFound_ShouldThrowException() {
        when(routeRepository.findById(99)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            ticketService.getTicketStatistics(99, date);
        });

        assertTrue(exception.getMessage().contains("Không tìm thấy tuyến xe"));
    }
}
