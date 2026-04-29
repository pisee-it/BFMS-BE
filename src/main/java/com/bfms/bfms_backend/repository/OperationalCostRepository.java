package com.bfms.bfms_backend.repository;

import com.bfms.bfms_backend.entity.OperationalCost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface OperationalCostRepository extends JpaRepository<OperationalCost, Integer> {
    List<OperationalCost> findByRouteIdAndCostDate(Integer routeId, LocalDate costDate);

    List<OperationalCost> findAllByCostDateBetween(LocalDate startDate, LocalDate endDate);

    List<OperationalCost> findAllByRouteIdAndCostDateBetween(Integer routeId, LocalDate startDate, LocalDate endDate);
}
