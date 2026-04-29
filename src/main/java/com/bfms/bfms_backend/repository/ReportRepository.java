package com.bfms.bfms_backend.repository;

import com.bfms.bfms_backend.entity.EconomyReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReportRepository extends JpaRepository<EconomyReport, Integer> {

    /**
     * Tìm báo cáo kinh tế của một tuyến xe trong một ngày cụ thể.
     */
    Optional<EconomyReport> findByRouteIdAndReportDate(Integer routeId, LocalDate reportDate);

    /**
     * Tổng hợp doanh thu và lợi nhuận của một tuyến trong một khoảng thời gian.
     * Dùng cho báo cáo Tháng, Quý, Năm của từng tuyến.
     */
    @Query("SELECT SUM(er.totalTicketRevenue), SUM(er.totalAdRevenue), SUM(er.totalPassengers), " +
           "SUM(er.taxDeduction), SUM(er.netProfit) " +
           "FROM EconomyReport er " +
           "WHERE er.route.id = :routeId " +
           "AND er.reportDate BETWEEN :startDate AND :endDate")
    Object[] getSummaryByRouteAndDateRange(@Param("routeId") Integer routeId, 
                                          @Param("startDate") LocalDate startDate, 
                                          @Param("endDate") LocalDate endDate);

    /**
     * Tổng hợp toàn bộ hệ thống trong một khoảng thời gian.
     * Phục vụ User Story US-01 cho Owner.
     */
    @Query("SELECT SUM(er.totalTicketRevenue), SUM(er.totalAdRevenue), SUM(er.taxDeduction), SUM(er.netProfit), SUM(er.totalPassengers) " +
           "FROM EconomyReport er " +
           "WHERE er.reportDate BETWEEN :startDate AND :endDate")
    Object[] getTotalSystemSummary(@Param("startDate") LocalDate startDate, 
                                   @Param("endDate") LocalDate endDate);

    List<EconomyReport> findAllByReportDateBetween(LocalDate startDate, LocalDate endDate);

    List<EconomyReport> findAllByRouteIdAndReportDateBetween(Integer routeId, LocalDate startDate, LocalDate endDate);
}
