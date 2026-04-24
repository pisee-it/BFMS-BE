package com.bfms.bfms_backend.entity;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class EconomyReportTest {

    @Test
    void testCalculateReport_PositiveProfit() {
        EconomyReport report = new EconomyReport();
        BigDecimal ticketRevenue = new BigDecimal("1000000"); // 1M
        BigDecimal adRevenue = new BigDecimal("1100000"); // 1.1M (Gross)
        
        List<OperationalCost> costs = new ArrayList<>();
        OperationalCost fuel = new OperationalCost();
        fuel.setAmount(new BigDecimal("500000")); // 0.5M
        costs.add(fuel);

        report.calculateReport(ticketRevenue, adRevenue, 100, costs);

        // Net Ad Revenue = 1.1M / 1.1 = 1M
        // VAT = 1.1M - 1M = 100k
        // Operating Profit = (1M + 1M) - 0.5M = 1.5M
        // TNDN = 1.5M * 0.2 = 300k
        // Tax Deduction = 100k + 300k = 400k
        // Net Profit = 1.5M - 300k = 1.2M

        assertEquals(0, new BigDecimal("400000.00").compareTo(report.getTaxDeduction()));
        assertEquals(0, new BigDecimal("1200000.00").compareTo(report.getNetProfit()));
    }

    @Test
    void testCalculateReport_NegativeProfit() {
        EconomyReport report = new EconomyReport();
        BigDecimal ticketRevenue = new BigDecimal("100000"); // 100k
        BigDecimal adRevenue = new BigDecimal("110000"); // 110k (Gross) -> Net = 100k, VAT = 10k
        
        List<OperationalCost> costs = new ArrayList<>();
        OperationalCost repair = new OperationalCost();
        repair.setAmount(new BigDecimal("500000")); // 500k cost
        costs.add(repair);

        report.calculateReport(ticketRevenue, adRevenue, 50, costs);

        // Operating Profit = (100k + 100k) - 500k = -300k
        // TNDN = 0 (since profit <= 0)
        // Tax Deduction = 10k (VAT only)
        // Net Profit = -300k - 0 = -300k

        assertEquals(0, new BigDecimal("10000.00").compareTo(report.getTaxDeduction()));
        assertEquals(0, new BigDecimal("-300000.00").compareTo(report.getNetProfit()));
    }
}
