package com.bfms.bfms_backend.service;

import com.bfms.bfms_backend.dtos.req.AdAssignmentRequest;
import com.bfms.bfms_backend.dtos.req.AdCompanyRequest;
import com.bfms.bfms_backend.dtos.req.AdContractRequest;
import com.bfms.bfms_backend.dtos.res.AdAssignmentResponse;
import com.bfms.bfms_backend.dtos.res.AdCompanyResponse;
import com.bfms.bfms_backend.dtos.res.AdContractResponse;
import com.bfms.bfms_backend.entity.*;
import com.bfms.bfms_backend.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class AdAssignmentTest {

    @Autowired
    private AdService adService;

    @Autowired
    private AdCompanyRepository adCompanyRepository;

    @Autowired
    private AdContractRepository adContractRepository;

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private BusRepository busRepository;

    private Integer routeId;
    private Integer busId;
    private Integer companyId;

    @BeforeEach
    void setUp() {
        // Setup Route
        Route route = new Route();
        route.setRouteNumber("R-" + UUID.randomUUID().toString().substring(0, 5));
        route.setPrice(new BigDecimal("10000"));
        route = routeRepository.save(route);
        routeId = route.getId();

        // Setup Bus
        Bus bus = new Bus();
        bus.setRoute(route);
        bus.setBusModel("Universe");
        bus.setManufacturer("Hyundai");
        bus.setYom(2022);
        bus.setLicensePlate("TEST-" + UUID.randomUUID().toString().substring(0, 5));
        bus.setCapacity(45);
        bus.setStatus(BusStatus.ACTIVE);
        bus.setIsAdvertised(false);
        bus = busRepository.save(bus);
        busId = bus.getId();

        // Setup Company
        AdCompanyRequest companyReq = new AdCompanyRequest("Test Co", "TAX-" + UUID.randomUUID(), "Contact");
        AdCompanyResponse companyRes = adService.createCompany(companyReq);
        companyId = companyRes.id();
    }

    @Test
    void testAssignAdToBus_UpdatesIsAdvertised() {
        // 1. Create and approve contract
        AdContractRequest contractReq = new AdContractRequest(
                companyId, routeId, LocalDate.now(), LocalDate.now().plusMonths(1),
                new BigDecimal("5000000"), 5, "http://file.pdf");
        AdContractResponse contractRes = adService.createContract(contractReq);
        adService.approveContract(contractRes.id());

        // 2. Assign
        AdAssignmentRequest assignReq = new AdAssignmentRequest(contractRes.id(), busId);
        AdAssignmentResponse assignRes = adService.assignAdToBus(assignReq);

        // 3. Verify
        Bus bus = busRepository.findById(busId).orElseThrow();
        assertTrue(bus.getIsAdvertised(), "Bus should be marked as advertised");
        assertFalse(assignRes.needsAttention(), "New assignment should not need attention");
    }

    @Test
    void testAssignAdToBus_AlreadyAdvertised_ThrowsException() {
        // 1. First assignment
        AdContractRequest contractReq = new AdContractRequest(
                companyId, routeId, LocalDate.now(), LocalDate.now().plusMonths(1),
                new BigDecimal("5000000"), 5, "http://file.pdf");
        AdContractResponse contractRes = adService.createContract(contractReq);
        adService.approveContract(contractRes.id());
        adService.assignAdToBus(new AdAssignmentRequest(contractRes.id(), busId));

        // 2. Second assignment to the same bus
        assertThrows(RuntimeException.class, () -> {
            adService.assignAdToBus(new AdAssignmentRequest(contractRes.id(), busId));
        }, "Xe này đã được dán quảng cáo, không thể phân bổ thêm.");
    }

    @Test
    void testNeedsAttention_WhenContractExpired() {
        // 1. Create a contract that expired yesterday
        AdContract contract = new AdContract();
        contract.setCompany(adCompanyRepository.findById(companyId).orElseThrow());
        contract.setRoute(routeRepository.findById(routeId).orElseThrow());
        contract.setStartDate(LocalDate.now().minusMonths(1));
        contract.setEndDate(LocalDate.now().minusDays(1)); // Expired yesterday
        contract.setPricePerBus(new BigDecimal("1000000"));
        contract.setBusQuantity(5);
        contract.setApprovalStatus(AdContractStatus.APPROVED);
        contract = adContractRepository.save(contract);

        // 2. Assign to bus (Force assignment even if expired for testing the mapping)
        AdAssignmentRequest assignReq = new AdAssignmentRequest(contract.getId(), busId);
        AdAssignmentResponse assignRes = adService.assignAdToBus(assignReq);

        // 3. Verify needsAttention is true
        assertTrue(assignRes.needsAttention(), "Expired assignment should need attention");
    }

    @Test
    void testAssignAdToBus_ContractNotApproved_ThrowsException() {
        // 1. Create contract but DON'T approve
        AdContractRequest contractReq = new AdContractRequest(
                companyId, routeId, LocalDate.now(), LocalDate.now().plusMonths(1),
                new BigDecimal("5000000"), 5, "http://file.pdf");
        AdContractResponse contractRes = adService.createContract(contractReq);

        // 2. Try to assign
        assertThrows(RuntimeException.class, () -> {
            adService.assignAdToBus(new AdAssignmentRequest(contractRes.id(), busId));
        });
    }
}
