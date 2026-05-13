package com.bfms.bfms_backend.repository;

import com.bfms.bfms_backend.entity.SecurityLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SecurityLogRepository extends JpaRepository<SecurityLog, Integer> {
    @Query("SELECT s FROM SecurityLog s WHERE (:username IS NULL OR s.username LIKE %:username%) " +
            "AND (:action IS NULL OR s.action = :action)")
    Page<SecurityLog> findByUsernameAndAction(String username, String action, Pageable pageable);
}
