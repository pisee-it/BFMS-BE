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
public class AdServiceTest {

    @Autowired
    private AdService adService;

    @Autowired
    private AdCompanyRepository adCompanyRepository;

    @Autowired
    private AdContractRepository adContractRepository;

    @Autowired
    private AdAssignmentRepository adAssignmentRepository;

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private BusRepository busRepository;

    private Integer routeId;
    private Integer busId;

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
        bus.setLicensePlate("BUS-" + UUID.randomUUID().toString().substring(0, 5));
        bus.setCapacity(45);
        bus.setStatus(BusStatus.ACTIVE);
        bus = busRepository.save(bus);
        busId = bus.getId();
    }

    @Test
    void testAdModuleWorkflow() {
        // 1. Create Company
        AdCompanyRequest companyReq = new AdCompanyRequest("Test Co", "TAX-" + UUID.randomUUID(), "Contact Info");
        AdCompanyResponse companyRes = adService.createCompany(companyReq);
        assertNotNull(companyRes.id());
        assertEquals("Test Co", companyRes.name());

        // 2. Create Contract
        AdContractRequest contractReq = new AdContractRequest(
                companyRes.id(),
                routeId,
                LocalDate.now(),
                LocalDate.now().plusMonths(1),
                new BigDecimal("5000000"),
                5,
                "http://file.pdf"
        );
        AdContractResponse contractRes = adService.createContract(contractReq);
        assertNotNull(contractRes.id());
        assertEquals(AdContractStatus.PENDING, contractRes.approvalStatus());

        // 3. Approve Contract
        AdContractResponse approvedRes = adService.approveContract(contractRes.id());
        assertEquals(AdContractStatus.APPROVED, approvedRes.approvalStatus());

        // 4. Assign Ad to Bus
        AdAssignmentRequest assignReq = new AdAssignmentRequest(approvedRes.id(), busId, "Sides");
        AdAssignmentResponse assignRes = adService.assignAdToBus(assignReq);
        assertNotNull(assignRes.id());
        assertEquals(AdAssignmentStatus.ACTIVE, assignRes.status());

        // Verify Bus state
        Bus busCheck = busRepository.findById(busId).orElseThrow();
        assertTrue(busCheck.getIsAdvertised());

        // 5. Request Delete
        AdContractResponse deleteReqRes = adService.requestDeleteContract(approvedRes.id());
        assertEquals(AdContractStatus.DELETE_REQUESTED, deleteReqRes.approvalStatus());

        // 6. Delete Contract
        adService.deleteContract(approvedRes.id());
        
        // Verify deletion
        assertFalse(adContractRepository.existsById(approvedRes.id()));
        assertTrue(adAssignmentRepository.findByAdContractId(approvedRes.id()).isEmpty());
        
        // Verify Bus state revert
        Bus busCheckFinal = busRepository.findById(busId).orElseThrow();
        assertFalse(busCheckFinal.getIsAdvertised());
    }

    @Test
    void testDuplicateTaxCode() {
        String taxCode = "TAX-DUP";
        adService.createCompany(new AdCompanyRequest("Co 1", taxCode, "info"));
        
        assertThrows(RuntimeException.class, () -> {
            adService.createCompany(new AdCompanyRequest("Co 2", taxCode, "info"));
        });
    }
}
