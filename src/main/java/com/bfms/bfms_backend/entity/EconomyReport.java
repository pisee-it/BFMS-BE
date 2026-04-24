package com.bfms.bfms_backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

@Entity
@Table(name = "economy_report")
@Getter
@Setter
@NoArgsConstructor
public class EconomyReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id")
    private Route route;

    @Column(name = "report_date", nullable = false)
    private LocalDate reportDate;

    @Column(name = "total_ticket_revenue")
    private BigDecimal totalTicketRevenue = BigDecimal.ZERO;

    @Column(name = "total_ad_revenue")
    private BigDecimal totalAdRevenue = BigDecimal.ZERO;

    @Column(name = "total_passengers")
    private Integer totalPassengers = 0;

    @Column(name = "tax_deduction")
    private BigDecimal taxDeduction = BigDecimal.ZERO;

    @Column(name = "net_profit")
    private BigDecimal netProfit = BigDecimal.ZERO;

    @Column(name = "created_at", insertable = false, updatable = false)
    private OffsetDateTime createdAt;

    /**
     * Tính toán báo cáo kinh tế dựa trên doanh thu và chi phí vận hành.
     * 
     * @param ticketRevenue Doanh thu từ vé (đã được tổng hợp từ DailyTicketStat)
     * @param adRevenue Doanh thu quảng cáo tổng cộng (bao gồm cả VAT)
     * @param totalPassengers Tổng số hành khách
     * @param operationalCosts Danh sách chi phí vận hành trong ngày
     */
    public void calculateReport(BigDecimal ticketRevenue, BigDecimal adRevenue, Integer totalPassengers, List<OperationalCost> operationalCosts) {
        this.totalTicketRevenue = ticketRevenue;
        this.totalAdRevenue = adRevenue;
        this.totalPassengers = totalPassengers;

        // 1. Tính Doanh thu quảng cáo thuần (Net Ad Revenue)
        // Theo Quy_dinh_thue_BFMS.md, dịch vụ quảng cáo chịu thuế GTGT 10%.
        // Công thức: Net = Gross / 1.1
        BigDecimal netAdRevenue = adRevenue.divide(BigDecimal.valueOf(1.1), 2, RoundingMode.HALF_UP);
        
        // 2. Tính thuế GTGT (VAT) từ quảng cáo
        // VAT = Gross - Net
        BigDecimal vat = adRevenue.subtract(netAdRevenue);

        // 3. Tổng chi phí vận hành
        BigDecimal totalCosts = (operationalCosts == null) ? BigDecimal.ZERO : operationalCosts.stream()
                .map(OperationalCost::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 4. Lợi nhuận kinh doanh (Operating Profit)
        // Lợi nhuận = (Doanh thu vé + Doanh thu quảng cáo thuần) - Tổng chi phí
        // Lưu ý: Vé xe buýt chịu thuế GTGT 0% nên không cần khấu trừ VAT đầu ra cho vé.
        BigDecimal operatingProfit = ticketRevenue.add(netAdRevenue).subtract(totalCosts);

        // 5. Thuế thu nhập doanh nghiệp (TNDN)
        // Theo Quy_dinh_thue_BFMS.md, thuế suất TNDN là 20% trên lợi nhuận sau khi trừ chi phí.
        // Thuế này chỉ tính khi công ty có lãi (operatingProfit > 0).
        BigDecimal tndn = BigDecimal.ZERO;
        if (operatingProfit.compareTo(BigDecimal.ZERO) > 0) {
            tndn = operatingProfit.multiply(BigDecimal.valueOf(0.2)).setScale(2, RoundingMode.HALF_UP);
        }

        // 6. Tổng các khoản khấu trừ thuế (Tax Deduction)
        // Bao gồm thuế GTGT đầu ra của quảng cáo và thuế TNDN phải nộp.
        this.taxDeduction = vat.add(tndn);

        // 7. Lợi nhuận ròng (Net Profit)
        // Là phần lợi nhuận còn lại sau khi đã trừ thuế TNDN.
        this.netProfit = operatingProfit.subtract(tndn);
    }
}
