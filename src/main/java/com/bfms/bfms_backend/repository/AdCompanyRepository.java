package com.bfms.bfms_backend.repository;

import com.bfms.bfms_backend.entity.AdCompany;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface AdCompanyRepository extends JpaRepository<AdCompany, Integer> {
    Optional<AdCompany> findByTaxCode(String taxCode);
}
