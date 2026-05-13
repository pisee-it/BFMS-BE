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
import java.util.*;
import java.util.stream.Collectors;

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

        // Đồng bộ dữ liệu báo cáo cho khoảng thời gian yêu cầu (Tối ưu: Chỉ gọi 1 lần cho cả dải ngày)
        syncEconomyReports(startDate, endDate);

        // Lấy dữ liệu tổng hợp
        List<Object[]> results = reportRepository.getTotalSystemSummary(startDate, endDate);
        
        if (results == null || results.isEmpty() || results.get(0)[0] == null) {
            return new RevenueResponse(
                BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, 0,
                timeframe, date
            );
        }

        Object[] result = results.get(0);

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
    public void syncEconomyReports(LocalDate startDate, LocalDate endDate) {
        List<Route> routes = routeRepository.findAll();
        List<AdContractStatus> validAdStatuses = Arrays.asList(AdContractStatus.APPROVED, AdContractStatus.PAID);

        // Bulk fetch dữ liệu cho toàn bộ dải ngày để tránh N+1
        List<DailyTicketStat> allStats = dailyTicketStatRepository.findAllByReportDateBetween(startDate, endDate);
        List<AdContract> allContracts = adContractRepository.findAllByStartDateBetweenAndApprovalStatusIn(startDate, endDate, validAdStatuses);
        List<OperationalCost> allCosts = operationalCostRepository.findAllByCostDateBetween(startDate, endDate);
        List<EconomyReport> allReports = reportRepository.findAllByReportDateBetween(startDate, endDate);

        // Sử dụng Map để truy xuất nhanh O(1) trong vòng lặp
        Map<String, DailyTicketStat> statMap = allStats.stream()
                .collect(Collectors.toMap(s -> s.getRoute().getId() + "_" + s.getReportDate(), s -> s, (a, b) -> a));
        
        Map<String, List<AdContract>> contractMap = allContracts.stream()
                .filter(c -> c.getRoute() != null)
                .collect(Collectors.groupingBy(c -> c.getRoute().getId() + "_" + c.getStartDate()));
                
        Map<String, List<OperationalCost>> costMap = allCosts.stream()
                .collect(Collectors.groupingBy(c -> c.getRoute().getId() + "_" + c.getCostDate()));
                
        Map<String, EconomyReport> reportMap = allReports.stream()
                .collect(Collectors.toMap(r -> r.getRoute().getId() + "_" + r.getReportDate(), r -> r, (a, b) -> a));

        List<EconomyReport> reportsToSave = new ArrayList<>();

        for (LocalDate d = startDate; !d.isAfter(endDate); d = d.plusDays(1)) {
            for (Route route : routes) {
                String key = route.getId() + "_" + d;
                
                DailyTicketStat stat = statMap.get(key);
                BigDecimal ticketRevenue = (stat != null) ? stat.getRevenueSingleTickets() : BigDecimal.ZERO;
                Integer totalPassengers = (stat != null) ? stat.getTotalPassengers() : 0;

                List<AdContract> contracts = contractMap.getOrDefault(key, Collections.emptyList());
                BigDecimal adRevenue = contracts.stream()
                        .map(c -> c.getPricePerBus().multiply(BigDecimal.valueOf(c.getBusQuantity())))
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                List<OperationalCost> costs = costMap.getOrDefault(key, Collections.emptyList());

                // 5. Tính toán và lưu báo cáo
                // Chi tiết công thức tại EconomyReport.calculateReport():
                // - Vé xe buýt: Thuế GTGT 0% (vận tải công cộng)
                // - Quảng cáo: Thuế GTGT 10% (dịch vụ thương mại) -> Net = Gross / 1.1
                // - Thuế TNDN: 20% tính trên lợi nhuận sau khi trừ chi phí (chỉ tính khi có lãi)
                EconomyReport report = reportMap.getOrDefault(key, new EconomyReport());
                report.setRoute(route);
                report.setReportDate(d);
                report.calculateReport(ticketRevenue, adRevenue, totalPassengers, costs);
                reportsToSave.add(report);
            }
        }
        reportRepository.saveAll(reportsToSave);
    }

    @Override
    @Transactional
    public void syncEconomyReports(Integer routeId, LocalDate startDate, LocalDate endDate) {
        Route route = routeRepository.findById(routeId).orElse(null);
        if (route == null) return;

        List<AdContractStatus> validAdStatuses = Arrays.asList(AdContractStatus.APPROVED, AdContractStatus.PAID);

        // Bulk fetch chỉ cho routeId cụ thể
        List<DailyTicketStat> allStats = dailyTicketStatRepository.findAllByRouteIdAndReportDateBetween(routeId, startDate, endDate);
        List<AdContract> allContracts = adContractRepository.findAllByStartDateBetweenAndApprovalStatusIn(startDate, endDate, validAdStatuses);
        List<OperationalCost> allCosts = operationalCostRepository.findAllByRouteIdAndCostDateBetween(routeId, startDate, endDate);
        List<EconomyReport> allReports = reportRepository.findAllByRouteIdAndReportDateBetween(routeId, startDate, endDate);

        Map<LocalDate, DailyTicketStat> statMap = allStats.stream()
                .collect(Collectors.toMap(DailyTicketStat::getReportDate, s -> s, (a, b) -> a));
        
        Map<LocalDate, List<AdContract>> contractMap = allContracts.stream()
                .filter(c -> c.getRoute() != null && c.getRoute().getId().equals(routeId))
                .collect(Collectors.groupingBy(AdContract::getStartDate));
                
        Map<LocalDate, List<OperationalCost>> costMap = allCosts.stream()
                .collect(Collectors.groupingBy(OperationalCost::getCostDate));
                
        Map<LocalDate, EconomyReport> reportMap = allReports.stream()
                .collect(Collectors.toMap(EconomyReport::getReportDate, r -> r, (a, b) -> a));

        List<EconomyReport> reportsToSave = new ArrayList<>();

        for (LocalDate d = startDate; !d.isAfter(endDate); d = d.plusDays(1)) {
            DailyTicketStat stat = statMap.get(d);
            BigDecimal ticketRevenue = (stat != null) ? stat.getRevenueSingleTickets() : BigDecimal.ZERO;
            Integer totalPassengers = (stat != null) ? stat.getTotalPassengers() : 0;

            List<AdContract> contracts = contractMap.getOrDefault(d, Collections.emptyList());
            BigDecimal adRevenue = contracts.stream()
                    .map(c -> c.getPricePerBus().multiply(BigDecimal.valueOf(c.getBusQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            List<OperationalCost> costs = costMap.getOrDefault(d, Collections.emptyList());

            // 5. Tính toán báo cáo cho tuyến đơn lẻ
            // Formula: Net Profit = (Ticket + AdNet) - Costs - TaxTNDN
            // Trong đó TaxTNDN = 20% * (Ticket + AdNet - Costs)
            EconomyReport report = reportMap.getOrDefault(d, new EconomyReport());
            report.setRoute(route);
            report.setReportDate(d);
            report.calculateReport(ticketRevenue, adRevenue, totalPassengers, costs);
            reportsToSave.add(report);
        }
        reportRepository.saveAll(reportsToSave);
    }
}
