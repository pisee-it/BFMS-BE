package com.bfms.bfms_backend.repository;

import com.bfms.bfms_backend.entity.AdAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AdAssignmentRepository extends JpaRepository<AdAssignment, Integer> {
    List<AdAssignment> findByAdContractId(Integer contractId);
}
