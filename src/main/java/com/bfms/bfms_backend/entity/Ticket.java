package com.bfms.bfms_backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ticket")
@Getter
@Setter
@NoArgsConstructor
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // 1. Thuộc về ca chạy nào (FK BUS_SHIFT)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bus_shift_id", nullable = false)
    private BusShift busShift;

    // 2. Loại vé: "SINGLE" hoặc "MONTHLY"
    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private Integer quantity;
}
