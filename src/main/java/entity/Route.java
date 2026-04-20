package entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity ánh xạ bảng ROUTE.
 *
 * Lưu ý mapping tên cột:
 *   Java field       DB column
 *   routeNumber   -> route_number
 *   stopA         -> stop_a
 *   stopB         -> stop_b
 *   distanceAb    -> distance_ab
 *   distanceBa    -> distance_ba
 *   operationStart-> operation_start
 *   operationEnd  -> operation_end
 */
@Entity
@Table(name = "route")
@Getter
@Setter
@NoArgsConstructor
public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "route_number")
    private String routeNumber;

    @Column(name = "stop_a")
    private String stopA;

    @Column(name = "stop_b")
    private String stopB;

    @Column(name = "path")
    private String path;

    @Column(name = "distance_ab")
    private BigDecimal distanceAb;

    @Column(name = "distance_ba")
    private BigDecimal distanceBa;

    @Column(name = "operation_start")
    private LocalTime operationStart;

    @Column(name = "operation_end")
    private LocalTime operationEnd;

    @Column(name = "price")
    private BigDecimal price;

    @OneToMany(mappedBy = "route", fetch = FetchType.LAZY)
    private List<Bus> buses = new ArrayList<>();
}