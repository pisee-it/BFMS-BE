package com.bfms.bfms_backend.repository;

import com.bfms.bfms_backend.entity.Node;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface NodeRepository extends JpaRepository<Node, Integer> {
    // 1. Phục vụ việc kiểm tra lịch trình của tuyến trong ngày
    boolean existsByRouteIdAndExecutionDateAndNodeNumber(Integer routeId, LocalDate executionDate, Integer nodeNumber);

    List<Node> findByRouteId(Integer routeId);
}

