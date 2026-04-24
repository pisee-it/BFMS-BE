package com.bfms.bfms_backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "operational_cost")
@Getter
@Setter
@NoArgsConstructor
public class OperationalCost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id")
    private Route route;

    @Column(name = "cost_date", nullable = false)
    private LocalDate costDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private CostType type;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "description")
    private String description;

    @Column(name = "created_at", insertable = false, updatable = false)
    private OffsetDateTime createdAt;
}
