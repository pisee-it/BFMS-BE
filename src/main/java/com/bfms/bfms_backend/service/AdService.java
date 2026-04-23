package com.bfms.bfms_backend.service;

import com.bfms.bfms_backend.dtos.req.AdAssignmentRequest;
import com.bfms.bfms_backend.dtos.req.AdCompanyRequest;
import com.bfms.bfms_backend.dtos.req.AdContractRequest;
import com.bfms.bfms_backend.dtos.res.AdAssignmentResponse;
import com.bfms.bfms_backend.dtos.res.AdCompanyResponse;
import com.bfms.bfms_backend.dtos.res.AdContractResponse;

import java.util.List;

public interface AdService {
    AdCompanyResponse createCompany(AdCompanyRequest request);
    AdContractResponse createContract(AdContractRequest request);
    AdContractResponse approveContract(Integer contractId);
    AdAssignmentResponse assignAdToBus(AdAssignmentRequest request);
    
    // Deletion logic requested by user
    AdContractResponse requestDeleteContract(Integer contractId);
    void deleteContract(Integer contractId);
    
    List<AdCompanyResponse> getAllCompanies();
    List<AdContractResponse> getAllContracts();
}
