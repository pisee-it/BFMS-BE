package repository;

import entity.Bus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BusRepository extends JpaRepository<Bus, Integer> {
    // 1. Kiểm tra sự tồn tại của biển số xe (vì license_plate là UNIQUE)
    Optional<Bus> findByLicensePlate(String licensePlate);
}
