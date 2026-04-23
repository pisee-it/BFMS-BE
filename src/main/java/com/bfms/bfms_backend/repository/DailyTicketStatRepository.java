package com.bfms.bfms_backend.repository;

import com.bfms.bfms_backend.entity.DailyTicketStat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface DailyTicketStatRepository extends JpaRepository<DailyTicketStat, Integer> {
    Optional<DailyTicketStat> findByRouteIdAndReportDate(Integer routeId, LocalDate reportDate);
}
