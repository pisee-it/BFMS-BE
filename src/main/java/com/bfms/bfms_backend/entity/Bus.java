package com.bfms.bfms_backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "bus")
@Getter
@Setter
@NoArgsConstructor
public class Bus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;

    @Column(name = "bus_model", nullable = false)
    private String busModel;

    @Column(name = "manufacturer", nullable = false)
    private String manufacturer;

    @Column(name = "capacity", nullable = false)
    private Integer capacity;

    @Column(name = "yom", nullable = false)
    private Integer yom;

    @Column(name = "license_plate")
    private String licensePlate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BusStatus status;

    @Column(name = "is_advertised", nullable = false)
    private Boolean isAdvertised;
}
