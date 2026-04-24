package com.bfms.bfms_backend.repository;

import com.bfms.bfms_backend.entity.AdContract;
import com.bfms.bfms_backend.entity.AdContractStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AdContractRepository extends JpaRepository<AdContract, Integer> {
    List<AdContract> findAllByStartDateAndApprovalStatusIn(LocalDate startDate, List<AdContractStatus> statuses);
}
