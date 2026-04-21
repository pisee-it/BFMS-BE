package entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Entity
@Table(name = "bus_shift")
@Getter
@Setter
@NoArgsConstructor
public class BusShift {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // 1. Tham chiếu nốt chạy (FK NODE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "node_id", nullable = false)
    private Node node;

    // 2. Tham chiếu xe thực hiện (FK BUS)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bus_id", nullable = false)
    private Bus bus;

    // 3. Tài xế thực hiện (FK APP_USER - role STAFF)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id")
    private AppUser driver;

    @Column(name = "shift_order")
    private Integer shiftOrder;

    @Column(name = "planned_departuretime")
    private LocalTime plannedDepartureTime;

    @Column(name = "planned_arrivaltime")
    private LocalTime plannedArrivalTime;

    @Enumerated(EnumType.STRING)
    private ShiftStatus status;

    @Column(name = "total_single_tickets")
    private Integer totalSingleTickets = 0;

    @Column(name = "total_monthly_tickets")
    private Integer totalMonthlyTickets = 0;

    // 4. Doanh thu ca: SUM(Ticket) x Route.price
    @Column(name = "shift_revenue")
    private BigDecimal shiftRevenue;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
