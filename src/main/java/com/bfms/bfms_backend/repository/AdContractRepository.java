package com.bfms.bfms_backend.repository;

import com.bfms.bfms_backend.entity.AdContract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdContractRepository extends JpaRepository<AdContract, Integer> {
}
