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
import com.bfms.bfms_backend.service.NotificationService;
import org.springframework.stereotype.Service;
import com.bfms.bfms_backend.exception.AppException;
import com.bfms.bfms_backend.exception.ErrorCode;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdServiceImpl implements AdService {

    private final AdCompanyRepository adCompanyRepository;
    private final AdContractRepository adContractRepository;
    private final AdAssignmentRepository adAssignmentRepository;
    private final BusRepository busRepository;
    private final RouteRepository routeRepository;
    private final NotificationService notificationService;
    private final AppUserRepository appUserRepository;
    private final AdMapper adMapper;

    public AdServiceImpl(AdCompanyRepository adCompanyRepository,
            AdContractRepository adContractRepository,
            AdAssignmentRepository adAssignmentRepository,
            BusRepository busRepository,
            RouteRepository routeRepository,
            NotificationService notificationService,
            AppUserRepository appUserRepository,
            AdMapper adMapper) {
        this.adCompanyRepository = adCompanyRepository;
        this.adContractRepository = adContractRepository;
        this.adAssignmentRepository = adAssignmentRepository;
        this.busRepository = busRepository;
        this.routeRepository = routeRepository;
        this.notificationService = notificationService;
        this.appUserRepository = appUserRepository;
        this.adMapper = adMapper;
    }

    @Override
    @Transactional
    public AdCompanyResponse createCompany(AdCompanyRequest request) {
        if (adCompanyRepository.findByTaxCode(request.taxCode()).isPresent()) {
            throw new AppException(ErrorCode.AD_COMPANY_ALREADY_EXISTS);
        }

        AdCompany company = adMapper.toCompanyEntity(request);
        AdCompany saved = adCompanyRepository.save(company);
        return adMapper.toCompanyResponse(saved);
    }

    @Override
    @Transactional
    public AdContractResponse createContract(AdContractRequest request) {
        AdCompany company = adCompanyRepository.findById(request.companyId())
                .orElseThrow(() -> new AppException(ErrorCode.AD_COMPANY_NOT_FOUND));

        Route route = routeRepository.findById(request.routeId())
                .orElseThrow(() -> new AppException(ErrorCode.ROUTE_NOT_FOUND));

        if (request.endDate().isBefore(request.startDate())) {
            throw new AppException(ErrorCode.INVALID_DATE_RANGE);
        }

        AdContract contract = adMapper.toContractEntity(request);
        contract.setCompany(company);
        contract.setRoute(route);
        contract.setApprovalStatus(AdContractStatus.PENDING);

        AdContract saved = adContractRepository.save(contract);
        return adMapper.toContractResponse(saved);
    }

    @Override
    @Transactional
    public AdContractResponse approveContract(Integer contractId) {
        AdContract contract = adContractRepository.findById(contractId)
                .orElseThrow(() -> new AppException(ErrorCode.AD_CONTRACT_NOT_FOUND));

        contract.setApprovalStatus(AdContractStatus.APPROVED);
        AdContract saved = adContractRepository.save(contract);

        List<AppUser> admins = appUserRepository.findByRole(Role.ADMIN);
        String message = String.format("Hợp đồng quảng cáo #%d (%s) đã được phê duyệt bởi Kế toán.",
                saved.getId(), saved.getCompany().getName());

        for (AppUser admin : admins) {
            notificationService.notify(admin.getId(), message);
        }

        return adMapper.toContractResponse(saved);
    }

    @Override
    @Transactional
    public AdAssignmentResponse assignAdToBus(AdAssignmentRequest request) {
        AdContract contract = adContractRepository.findById(request.adContractId())
                .orElseThrow(() -> new AppException(ErrorCode.AD_CONTRACT_NOT_FOUND));

        if (contract.getApprovalStatus() != AdContractStatus.APPROVED
                && contract.getApprovalStatus() != AdContractStatus.PAID) {
            throw new AppException(ErrorCode.AD_CONTRACT_NOT_APPROVED);
        }

        Bus bus = busRepository.findById(request.busId())
                .orElseThrow(() -> new AppException(ErrorCode.BUS_NOT_FOUND));

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
        return adMapper.toAssignmentResponse(saved);
    }

    @Override
    @Transactional
    public AdContractResponse requestDeleteContract(Integer contractId) {
        AdContract contract = adContractRepository.findById(contractId)
                .orElseThrow(() -> new AppException(ErrorCode.AD_CONTRACT_NOT_FOUND));

        contract.setApprovalStatus(AdContractStatus.DELETE_REQUESTED);
        AdContract saved = adContractRepository.save(contract);
        return adMapper.toContractResponse(saved);
    }

    @Override
    @Transactional
    public void deleteContract(Integer contractId) {
        AdContract contract = adContractRepository.findById(contractId)
                .orElseThrow(() -> new AppException(ErrorCode.AD_CONTRACT_NOT_FOUND));

        List<AdAssignment> assignments = adAssignmentRepository.findByAdContractId(contractId);
        for (AdAssignment assignment : assignments) {
            Bus bus = assignment.getBus();
            bus.setIsAdvertised(false);
            busRepository.save(bus);
            adAssignmentRepository.delete(assignment);
        }

        adContractRepository.delete(contract);
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
