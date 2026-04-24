package com.bfms.bfms_backend.service.impl;

import com.bfms.bfms_backend.dtos.res.RevenueResponse;
import com.bfms.bfms_backend.entity.*;
import com.bfms.bfms_backend.repository.*;
import com.bfms.bfms_backend.service.EconomyReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EconomyReportServiceImpl implements EconomyReportService {

    private final ReportRepository reportRepository;
    private final DailyTicketStatRepository dailyTicketStatRepository;
    private final AdContractRepository adContractRepository;
    private final OperationalCostRepository operationalCostRepository;
    private final RouteRepository routeRepository;

    @Override
    @Transactional
    public RevenueResponse getSystemTotalRevenue(String timeframe, LocalDate date) {
        LocalDate startDate;
        LocalDate endDate;

        switch (timeframe.toLowerCase()) {
            case "month" -> {
                startDate = date.with(TemporalAdjusters.firstDayOfMonth());
                endDate = date.with(TemporalAdjusters.lastDayOfMonth());
            }
            case "year" -> {
                startDate = date.with(TemporalAdjusters.firstDayOfYear());
                endDate = date.with(TemporalAdjusters.lastDayOfYear());
            }
            default -> { // "day"
                startDate = date;
                endDate = date;
            }
        }

        // Đồng bộ dữ liệu báo cáo cho khoảng thời gian yêu cầu
        for (LocalDate d = startDate; !d.isAfter(endDate); d = d.plusDays(1)) {
            syncEconomyReports(d);
        }

        // Lấy dữ liệu tổng hợp
        Object[] result = reportRepository.getTotalSystemSummary(startDate, endDate);
        
        if (result == null || result.length == 0 || result[0] == null) {
            return new RevenueResponse(
                BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, 0,
                timeframe, date
            );
        }

        // Mapping index từ Query trong ReportRepository:
        // 0: SUM(totalTicketRevenue)
        // 1: SUM(totalAdRevenue)
        // 2: SUM(taxDeduction)
        // 3: SUM(netProfit)
        // 4: SUM(totalPassengers)
        return new RevenueResponse(
            (BigDecimal) result[0],
            (BigDecimal) result[1],
            (BigDecimal) result[2],
            (BigDecimal) result[3],
            ((Long) result[4]).intValue(),
            timeframe,
            date
        );
    }

    @Override
    @Transactional
    public void syncEconomyReports(LocalDate date) {
        List<Route> routes = routeRepository.findAll();
        
        // Trạng thái hợp đồng quảng cáo được tính vào doanh thu
        List<AdContractStatus> validAdStatuses = Arrays.asList(AdContractStatus.APPROVED, AdContractStatus.PAID);

        for (Route route : routes) {
            // 1. Lấy dữ liệu vé
            DailyTicketStat stat = dailyTicketStatRepository.findByRouteIdAndReportDate(route.getId(), date)
                    .orElse(null);
            
            BigDecimal ticketRevenue = (stat != null) ? stat.getRevenueSingleTickets() : BigDecimal.ZERO;
            Integer totalPassengers = (stat != null) ? stat.getTotalPassengers() : 0;

            // 2. Lấy dữ liệu quảng cáo (Chỉ tính vào ngày startDate của hợp đồng)
            List<AdContract> contracts = adContractRepository.findAllByStartDateAndApprovalStatusIn(date, validAdStatuses);
            
            BigDecimal adRevenue = contracts.stream()
                    .filter(c -> c.getRoute() != null && c.getRoute().getId().equals(route.getId()))
                    .map(c -> c.getPricePerBus().multiply(BigDecimal.valueOf(c.getBusQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // 3. Lấy chi phí vận hành
            List<OperationalCost> costs = operationalCostRepository.findByRouteIdAndCostDate(route.getId(), date);

            // 4. Tìm hoặc tạo EconomyReport
            EconomyReport report = reportRepository.findByRouteIdAndReportDate(route.getId(), date)
                    .orElse(new EconomyReport());
            
            report.setRoute(route);
            report.setReportDate(date);
            
            // 5. Tính toán và lưu
            report.calculateReport(ticketRevenue, adRevenue, totalPassengers, costs);
            reportRepository.save(report);
        }
    }
}
