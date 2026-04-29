package com.bfms.bfms_backend.service.impl;

import com.bfms.bfms_backend.dtos.req.AdAssignmentRequest;
import com.bfms.bfms_backend.dtos.req.AdCompanyRequest;
import com.bfms.bfms_backend.dtos.req.AdContractRequest;
import com.bfms.bfms_backend.dtos.res.AdAssignmentResponse;
import com.bfms.bfms_backend.dtos.res.AdCompanyResponse;
import com.bfms.bfms_backend.dtos.res.AdContractResponse;
import com.bfms.bfms_backend.entity.*;
import com.bfms.bfms_backend.mapper.AdMapper;
import com.bfms.bfms_backend.repository.*;
import com.bfms.bfms_backend.service.AdService;
import com.bfms.bfms_backend.service.AuditService;
import com.bfms.bfms_backend.service.NotificationService;
import org.springframework.stereotype.Service;
import com.bfms.bfms_backend.util.EntityLookupHelper;
import com.bfms.bfms_backend.exception.AppException;
import com.bfms.bfms_backend.exception.ErrorCode;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AdServiceImpl implements AdService {

    private final AdCompanyRepository adCompanyRepository;
    private final AdContractRepository adContractRepository;
    private final AdAssignmentRepository adAssignmentRepository;
    private final BusRepository busRepository;
    private final NotificationService notificationService;
    private final AdMapper adMapper;
    private final EntityLookupHelper lookupHelper;
    private final AuditService auditService;

    public AdServiceImpl(AdCompanyRepository adCompanyRepository,
            AdContractRepository adContractRepository,
            AdAssignmentRepository adAssignmentRepository,
            BusRepository busRepository,
            NotificationService notificationService,
            AdMapper adMapper,
            EntityLookupHelper lookupHelper,
            AuditService auditService) {
        this.adCompanyRepository = adCompanyRepository;
        this.adContractRepository = adContractRepository;
        this.adAssignmentRepository = adAssignmentRepository;
        this.busRepository = busRepository;
        this.notificationService = notificationService;
        this.adMapper = adMapper;
        this.lookupHelper = lookupHelper;
        this.auditService = auditService;
    }

    @Override
    @Transactional
    public AdCompanyResponse createCompany(AdCompanyRequest request) {
        if (adCompanyRepository.findByTaxCode(request.taxCode()).isPresent()) {
            throw new AppException(ErrorCode.AD_COMPANY_ALREADY_EXISTS);
        }

        AdCompany company = adMapper.toCompanyEntity(request);
        AdCompany saved = adCompanyRepository.save(company);

        log.info("Đã tạo mới công ty quảng cáo: {} (MST: {})", saved.getName(), saved.getTaxCode());
        auditService.log("CREATE_AD_COMPANY", "Tạo mới công ty quảng cáo: " + saved.getName());

        return adMapper.toCompanyResponse(saved);
    }

    @Override
    @Transactional
    public AdContractResponse createContract(AdContractRequest request) {
        AdCompany company = lookupHelper.getAdCompany(request.companyId());
        Route route = lookupHelper.getRoute(request.routeId());

        if (request.endDate().isBefore(request.startDate())) {
            throw new AppException(ErrorCode.INVALID_DATE_RANGE);
        }

        AdContract contract = adMapper.toContractEntity(request);
        contract.setCompany(company);
        contract.setRoute(route);
        contract.setApprovalStatus(AdContractStatus.PENDING);

        AdContract saved = adContractRepository.save(contract);
        log.info("Đã tạo mới hợp đồng quảng cáo ID: {} cho công ty: {}. Số xe đăng ký: {}", saved.getId(),
                company.getName(), saved.getBusQuantity());
        auditService.log("CREATE_AD_CONTRACT",
                "Tạo mới hợp đồng quảng cáo ID: " + saved.getId() + " cho đối tác: " + company.getName());
        return adMapper.toContractResponse(saved);
    }

    @Override
    @Transactional
    public AdContractResponse approveContract(Integer contractId) {
        AdContract contract = lookupHelper.getAdContract(contractId);

        contract.setApprovalStatus(AdContractStatus.APPROVED);
        AdContract saved = adContractRepository.save(contract);

        String message = String.format("Hợp đồng quảng cáo #%d (%s) đã được phê duyệt bởi Kế toán.",
                saved.getId(), saved.getCompany().getName());

        notificationService.notifyAdmins(message);

        auditService.log("APPROVE_AD_CONTRACT", "Phê duyệt hợp đồng quảng cáo ID: " + contractId);

        return adMapper.toContractResponse(saved);
    }

    @Override
    @Transactional
    public AdAssignmentResponse assignAdToBus(AdAssignmentRequest request) {
        AdContract contract = lookupHelper.getAdContract(request.adContractId());

        if (contract.getApprovalStatus() != AdContractStatus.APPROVED
                && contract.getApprovalStatus() != AdContractStatus.PAID) {
            throw new AppException(ErrorCode.AD_CONTRACT_NOT_APPROVED);
        }

        Bus bus = lookupHelper.getBus(request.busId());

        if (Boolean.TRUE.equals(bus.getIsAdvertised())) {
            throw new AppException(ErrorCode.BUS_ALREADY_ADVERTISED);
        }

        List<AdAssignment> currentAssignments = adAssignmentRepository.findByAdContractId(request.adContractId());
        if (currentAssignments.size() >= contract.getBusQuantity()) {
            throw new AppException(ErrorCode.AD_CONTRACT_LIMIT_REACHED);
        }

        AdAssignment assignment = adMapper.toAssignmentEntity(request);
        assignment.setAdContract(contract);
        assignment.setBus(bus);
        assignment.setStatus(AdAssignmentStatus.ACTIVE);

        bus.setIsAdvertised(true);
        busRepository.save(bus);

        AdAssignment saved = adAssignmentRepository.save(assignment);

        auditService.log("ASSIGN_AD_TO_BUS", String.format("Gán quảng cáo từ hợp đồng #%d lên xe %s",
                request.adContractId(), bus.getLicensePlate()));

        return adMapper.toAssignmentResponse(saved);
    }

    @Override
    @Transactional
    public AdContractResponse requestDeleteContract(Integer contractId) {
        AdContract contract = lookupHelper.getAdContract(contractId);

        contract.setApprovalStatus(AdContractStatus.DELETE_REQUESTED);
        AdContract saved = adContractRepository.save(contract);
        return adMapper.toContractResponse(saved);
    }

    @Override
    @Transactional
    public void deleteContract(Integer contractId) {
        AdContract contract = lookupHelper.getAdContract(contractId);

        List<AdAssignment> assignments = adAssignmentRepository.findByAdContractId(contractId);
        for (AdAssignment assignment : assignments) {
            Bus bus = assignment.getBus();
            bus.setIsAdvertised(false);
            busRepository.save(bus);
            adAssignmentRepository.delete(assignment);
        }

        adContractRepository.delete(contract);

        auditService.log("DELETE_AD_CONTRACT", "Xóa vĩnh viễn hợp đồng quảng cáo ID: " + contractId);
    }

    @Override
    public List<AdCompanyResponse> getAllCompanies() {
        return adCompanyRepository.findAll().stream()
                .map(adMapper::toCompanyResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<AdContractResponse> getAllContracts() {
        return adContractRepository.findAll().stream()
                .map(adMapper::toContractResponse)
                .collect(Collectors.toList());
    }
}
