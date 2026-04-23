package com.bfms.bfms_backend.repository;

import com.bfms.bfms_backend.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Integer> {
    // 1. Phục vụ việc tổng hợp vé theo ca chạy
}
