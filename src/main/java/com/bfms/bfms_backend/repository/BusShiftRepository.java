package com.bfms.bfms_backend.repository;

import com.bfms.bfms_backend.entity.BusShift;
import com.bfms.bfms_backend.entity.ShiftStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BusShiftRepository extends JpaRepository<BusShift, Integer> {

    // 1. Tìm các ca chạy theo Route ID và Trạng thái (Sử dụng JOIN FETCH để tránh N+1)
    @Query("SELECT b FROM BusShift b JOIN FETCH b.bus JOIN FETCH b.driver JOIN FETCH b.node WHERE b.node.route.id = :routeId AND b.status = :status")
    List<BusShift> findActiveShifts(@Param("routeId") Integer routeId, @Param("status") ShiftStatus status);

    @Query("SELECT COALESCE(SUM(b.totalSingleTickets + b.totalMonthlyTickets), 0) FROM BusShift b WHERE b.node.id = :nodeId AND b.status = :status")
    Integer sumPassengersByNodeId(@Param("nodeId") Integer nodeId, @Param("status") ShiftStatus status);
}
