package entity;

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

    // 2. Chiều chạy: 1 = A->B, 2 = B->A
    @Column(name = "direction")
    private Integer direction;

    private String description;

    // 3. Derived field: Tổng hành khách (do Service layer tính toán)
    @Column(name = "total_passengers")
    private Integer totalPassengers = 0;
}