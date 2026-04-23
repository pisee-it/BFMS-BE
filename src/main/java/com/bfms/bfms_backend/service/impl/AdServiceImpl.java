package com.bfms.bfms_backend.service.impl;

import com.bfms.bfms_backend.dtos.req.AdAssignmentRequest;
import com.bfms.bfms_backend.dtos.req.AdCompanyRequest;
import com.bfms.bfms_backend.dtos.req.AdContractRequest;
import com.bfms.bfms_backend.dtos.res.AdAssignmentResponse;
import com.bfms.bfms_backend.dtos.res.AdCompanyResponse;
import com.bfms.bfms_backend.dtos.res.AdContractResponse;
import com.bfms.bfms_backend.entity.*;
import com.bfms.bfms_backend.repository.*;
import com.bfms.bfms_backend.service.AdService;
import org.springframework.stereotype.Service;
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

    public AdServiceImpl(AdCompanyRepository adCompanyRepository,
                         AdContractRepository adContractRepository,
                         AdAssignmentRepository adAssignmentRepository,
                         BusRepository busRepository,
                         RouteRepository routeRepository) {
        this.adCompanyRepository = adCompanyRepository;
        this.adContractRepository = adContractRepository;
        this.adAssignmentRepository = adAssignmentRepository;
        this.busRepository = busRepository;
        this.routeRepository = routeRepository;
    }

    @Override
    @Transactional
    public AdCompanyResponse createCompany(AdCompanyRequest request) {
        // Kiểm tra xem mã số thuế đã tồn tại chưa
        if (adCompanyRepository.findByTaxCode(request.taxCode()).isPresent()) {
            throw new RuntimeException("Mã số thuế này đã tồn tại trong hệ thống.");
        }

        AdCompany company = new AdCompany();
        company.setName(request.name());
        company.setTaxCode(request.taxCode());
        company.setContact(request.contact());

        AdCompany saved = adCompanyRepository.save(company);
        return mapToCompanyResponse(saved);
    }

    @Override
    @Transactional
    public AdContractResponse createContract(AdContractRequest request) {
        AdCompany company = adCompanyRepository.findById(request.companyId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy công ty quảng cáo."));
        
        Route route = routeRepository.findById(request.routeId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tuyến xe."));

        if (request.endDate().isBefore(request.startDate())) {
            throw new RuntimeException("Ngày kết thúc hợp đồng phải sau ngày bắt đầu.");
        }

        AdContract contract = new AdContract();
        contract.setCompany(company);
        contract.setRoute(route);
        contract.setStartDate(request.startDate());
        contract.setEndDate(request.endDate());
        contract.setPricePerBus(request.pricePerBus());
        contract.setBusQuantity(request.busQuantity());
        contract.setContractFileUrl(request.contractFileUrl());
        contract.setApprovalStatus(AdContractStatus.PENDING);

        AdContract saved = adContractRepository.save(contract);
        return mapToContractResponse(saved);
    }

    @Override
    @Transactional
    public AdContractResponse approveContract(Integer contractId) {
        AdContract contract = adContractRepository.findById(contractId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hợp đồng quảng cáo."));
        
        contract.setApprovalStatus(AdContractStatus.APPROVED);
        AdContract saved = adContractRepository.save(contract);
        return mapToContractResponse(saved);
    }

    @Override
    @Transactional
    public AdAssignmentResponse assignAdToBus(AdAssignmentRequest request) {
        AdContract contract = adContractRepository.findById(request.adContractId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hợp đồng quảng cáo."));

        if (contract.getApprovalStatus() != AdContractStatus.APPROVED && contract.getApprovalStatus() != AdContractStatus.PAID) {
            throw new RuntimeException("Hợp đồng chưa được phê duyệt hoặc thanh toán, không thể gán quảng cáo.");
        }

        Bus bus = busRepository.findById(request.busId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy xe buýt."));

        // Kiểm tra xem số lượng xe đã gán có vượt quá hợp đồng không
        List<AdAssignment> currentAssignments = adAssignmentRepository.findByAdContractId(request.adContractId());
        if (currentAssignments.size() >= contract.getBusQuantity()) {
            throw new RuntimeException("Số lượng xe được gán đã đạt giới hạn của hợp đồng.");
        }

        AdAssignment assignment = new AdAssignment();
        assignment.setAdContract(contract);
        assignment.setBus(bus);
        assignment.setPosition(request.position());
        assignment.setStatus(AdAssignmentStatus.ACTIVE);

        // Cập nhật trạng thái xe
        bus.setIsAdvertised(true);
        busRepository.save(bus);

        AdAssignment saved = adAssignmentRepository.save(assignment);
        return mapToAssignmentResponse(saved);
    }

    @Override
    @Transactional
    public AdContractResponse requestDeleteContract(Integer contractId) {
        AdContract contract = adContractRepository.findById(contractId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hợp đồng quảng cáo."));
        
        // Chỉ cho phép yêu cầu xóa nếu hợp đồng không ở trạng thái DELETE_REQUESTED hoặc đã bị REJECTED
        contract.setApprovalStatus(AdContractStatus.DELETE_REQUESTED);
        AdContract saved = adContractRepository.save(contract);
        return mapToContractResponse(saved);
    }

    @Override
    @Transactional
    public void deleteContract(Integer contractId) {
        AdContract contract = adContractRepository.findById(contractId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hợp đồng quảng cáo."));

        // Logic bổ sung: Khi xóa hợp đồng, cần gỡ các quảng cáo đang dán trên xe
        List<AdAssignment> assignments = adAssignmentRepository.findByAdContractId(contractId);
        for (AdAssignment assignment : assignments) {
            Bus bus = assignment.getBus();
            bus.setIsAdvertised(false); // Có thể cần logic phức tạp hơn nếu một xe có nhiều hợp đồng (tương lai)
            busRepository.save(bus);
            adAssignmentRepository.delete(assignment);
        }

        adContractRepository.delete(contract);
    }

    @Override
    public List<AdCompanyResponse> getAllCompanies() {
        return adCompanyRepository.findAll().stream()
                .map(this::mapToCompanyResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<AdContractResponse> getAllContracts() {
        return adContractRepository.findAll().stream()
                .map(this::mapToContractResponse)
                .collect(Collectors.toList());
    }

    private AdCompanyResponse mapToCompanyResponse(AdCompany company) {
        return new AdCompanyResponse(
                company.getId(),
                company.getName(),
                company.getTaxCode(),
                company.getContact()
        );
    }

    private AdContractResponse mapToContractResponse(AdContract contract) {
        return new AdContractResponse(
                contract.getId(),
                contract.getCompany().getId(),
                contract.getCompany().getName(),
                contract.getRoute().getId(),
                contract.getRoute().getRouteNumber(),
                contract.getStartDate(),
                contract.getEndDate(),
                contract.getPricePerBus(),
                contract.getBusQuantity(),
                contract.getApprovalStatus(),
                contract.getContractFileUrl(),
                contract.getCreatedAt()
        );
    }

    private AdAssignmentResponse mapToAssignmentResponse(AdAssignment assignment) {
        return new AdAssignmentResponse(
                assignment.getId(),
                assignment.getAdContract().getId(),
                assignment.getBus().getId(),
                assignment.getBus().getLicensePlate(),
                assignment.getPosition(),
                assignment.getStatus()
        );
    }
}
