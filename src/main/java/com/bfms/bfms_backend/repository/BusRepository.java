package com.bfms.bfms_backend.repository;

import com.bfms.bfms_backend.entity.Bus;
import com.bfms.bfms_backend.repository.projection.BusProjection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BusRepository extends JpaRepository<Bus, Integer> {
    // 1. Kiểm tra sự tồn tại của biển số xe (vì license_plate là UNIQUE)
    Optional<Bus> findByLicensePlate(String licensePlate);

    // 2. Lấy toàn bộ xe buýt dưới dạng Projection để tối ưu hiệu năng
    List<BusProjection> findBy();
}
