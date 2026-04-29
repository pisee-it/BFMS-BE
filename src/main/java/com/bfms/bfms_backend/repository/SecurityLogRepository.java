package com.bfms.bfms_backend.repository;

import com.bfms.bfms_backend.entity.SecurityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SecurityLogRepository extends JpaRepository<SecurityLog, Integer> {
}
