package com.bfms.bfms_backend.service.impl;

import com.bfms.bfms_backend.dtos.res.TicketStatisticsResponse;
import com.bfms.bfms_backend.entity.DailyTicketStat;
import com.bfms.bfms_backend.entity.Route;
import com.bfms.bfms_backend.repository.DailyTicketStatRepository;
import com.bfms.bfms_backend.repository.RouteRepository;
import com.bfms.bfms_backend.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

    private final DailyTicketStatRepository dailyTicketStatRepository;
    private final com.bfms.bfms_backend.util.EntityLookupHelper lookupHelper;

    @Override
    @Transactional(readOnly = true)
    public TicketStatisticsResponse getTicketStatistics(Integer routeId, LocalDate date) {
        Route route = lookupHelper.getRoute(routeId);

        return dailyTicketStatRepository.findByRouteIdAndReportDate(routeId, date)
                .map(stat -> mapToResponse(stat, route))
                .orElseGet(() -> createEmptyResponse(route, date));
    }


    private TicketStatisticsResponse mapToResponse(DailyTicketStat stat, Route route) {
        return new TicketStatisticsResponse(
                route.getId(),
                route.getRouteNumber() + " (" + route.getStopA() + " - " + route.getStopB() + ")",
                stat.getReportDate(),
                stat.getSingleTicketCount(),
                stat.getMonthlyTicketCount(),
                stat.getTotalPassengers(),
                stat.getRevenueSingleTickets()
        );
    }

    private TicketStatisticsResponse createEmptyResponse(Route route, LocalDate date) {
        return new TicketStatisticsResponse(
                route.getId(),
                route.getRouteNumber() + " (" + route.getStopA() + " - " + route.getStopB() + ")",
                date,
                0,
                0,
                0,
                BigDecimal.ZERO
        );
    }
}
