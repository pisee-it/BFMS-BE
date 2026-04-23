package com.bfms.bfms_backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "daily_ticket_stat")
@Getter
@Setter
@NoArgsConstructor
public class DailyTicketStat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;

    @Column(name = "report_date", nullable = false)
    private LocalDate reportDate;

    @Column(name = "single_ticket_count")
    private Integer singleTicketCount = 0;

    @Column(name = "monthly_ticket_count")
    private Integer monthlyTicketCount = 0;

    @Column(name = "total_passengers")
    private Integer totalPassengers = 0;

    @Column(name = "revenue_single_tickets")
    private BigDecimal revenueSingleTickets = BigDecimal.ZERO;

    public void addTickets(int singleCount, int monthlyCount) {
        this.singleTicketCount = (this.singleTicketCount == null ? 0 : this.singleTicketCount) + singleCount;
        this.monthlyTicketCount = (this.monthlyTicketCount == null ? 0 : this.monthlyTicketCount) + monthlyCount;
        calculateDerivedData();
    }

    public void calculateDerivedData() {
        this.totalPassengers = this.singleTicketCount + this.monthlyTicketCount;
        if (this.route != null && this.route.getPrice() != null) {
            this.revenueSingleTickets = this.route.getPrice().multiply(BigDecimal.valueOf(this.singleTicketCount));
        } else {
            this.revenueSingleTickets = BigDecimal.ZERO;
        }
    }
}
