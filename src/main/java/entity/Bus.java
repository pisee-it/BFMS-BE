package entity;

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
    @JoinColumn(name = "route_id")
    private Route route;

    @Column(name = "bus_model")
    private String busModel;

    @Column(name = "manufacturer")
    private String manufacturer;

    @Column(name = "capacity")
    private Integer capacity;

    @Column(name = "yom")
    private Integer yom;

    @Column(name = "license_plate")
    private String licensePlate;

    @Column(name = "status")
    private String status;

    @Column(name = "is_advertised")
    private Boolean isAdvertised;
}