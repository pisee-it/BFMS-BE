package com.bfms.bfms_backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Table(name = "node")
@Getter
@Setter
@NoArgsConstructor
public class Node {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // 1. Tham chiếu đến Tuyến xe (FK ROUTE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;

    @Column(name = "node_number")
    private Integer nodeNumber;

    @Column(name = "execution_date", nullable = false)
    private LocalDate executionDate;


    private String description;

    // 3. Derived field: Tổng hành khách (do Service layer tính toán)
    @Column(name = "total_passengers")
    private Integer totalPassengers = 0;
}
