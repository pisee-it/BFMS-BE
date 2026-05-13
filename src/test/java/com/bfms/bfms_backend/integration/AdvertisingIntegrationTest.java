package com.bfms.bfms_backend.integration;

import com.bfms.bfms_backend.dtos.req.AdAssignmentRequest;
import com.bfms.bfms_backend.dtos.req.AdContractRequest;
import com.bfms.bfms_backend.entity.*;
import com.bfms.bfms_backend.repository.*;
import com.bfms.bfms_backend.security.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AdvertisingIntegrationTest {

    static {
        // Nạp biến môi trường từ .env cho test context
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));
    }

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private BusRepository busRepository;

    @Autowired
    private AppUserRepository userRepository;

    @Autowired
    private AdCompanyRepository adCompanyRepository;

    @Autowired
    private AdContractRepository adContractRepository;

    @Autowired
    private AdAssignmentRepository adAssignmentRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    private Integer routeId;
    private Integer busId;
    private Integer companyId;
    private Integer adId;
    private Integer accountantId;
    private Integer adminId;

    private String advertisingToken;
    private String accountantToken;
    private String adminToken;

    @BeforeEach
    void setUp() {
        // 1. Setup Route
        Route route = new Route();
        route.setRouteNumber("AD-ROUTE-" + UUID.randomUUID().toString().substring(0, 5));
        route.setStopA("A");
        route.setStopB("B");
        route.setPath("A-B");
        route.setPrice(new BigDecimal("10000"));
        route = routeRepository.save(route);
        routeId = route.getId();

        // 2. Setup Bus
        Bus bus = new Bus();
        bus.setRoute(route);
        bus.setBusModel("AD-BUS");
        bus.setManufacturer("Hyundai");
        bus.setLicensePlate("AD-" + UUID.randomUUID().toString().substring(0, 5));
        bus.setIsAdvertised(false);
        bus.setCapacity(40);
        bus.setStatus(BusStatus.ACTIVE);
        bus.setYom(2023);
        bus = busRepository.save(bus);
        busId = bus.getId();

        // 3. Setup AdCompany
        AdCompany company = new AdCompany();
        company.setName("Ad Corp " + UUID.randomUUID().toString().substring(0, 5));
        company.setTaxCode(UUID.randomUUID().toString().substring(0, 10));
        company.setContact("contact@adcorp.com");
        company = adCompanyRepository.save(company);
        companyId = company.getId();

        // 4. Setup Users
        // Advertising
        String adUsername = "ad_" + UUID.randomUUID().toString().substring(0, 5);
        AppUser adUser = createTestUser(adUsername, Role.ADVERTISING);
        adId = adUser.getId();
        advertisingToken = jwtUtil.generateToken(adUsername);

        // Accountant
        String accUsername = "acc_" + UUID.randomUUID().toString().substring(0, 5);
        AppUser accUser = createTestUser(accUsername, Role.ACCOUNTANT);
        accountantId = accUser.getId();
        accountantToken = jwtUtil.generateToken(accUsername);

        // Admin
        String adminUsername = "admin_" + UUID.randomUUID().toString().substring(0, 5);
        AppUser adminUser = createTestUser(adminUsername, Role.ADMIN);
        adminId = adminUser.getId();
        adminToken = jwtUtil.generateToken(adminUsername);
    }

    @Autowired
    private NodeRepository nodeRepository;

    @Autowired
    private BusShiftRepository busShiftRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private DailyTicketStatRepository dailyTicketStatRepository;

    private AppUser createTestUser(String username, Role role) {
        AppUser user = new AppUser();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode("password123"));
        user.setRole(role);
        user.setFullName("Test " + role);
        return userRepository.save(user);
    }

    @AfterEach
    @Transactional
    void tearDown() {
        try {
            // Xóa theo thứ tự ngược lại của FK để tránh lỗi ràng buộc
            adAssignmentRepository.deleteAll();
            adContractRepository.deleteAll();
            adCompanyRepository.deleteAll();
            ticketRepository.deleteAll();
            busShiftRepository.deleteAll();
            busRepository.deleteAll();
            dailyTicketStatRepository.deleteAll();
            nodeRepository.deleteAll();
            routeRepository.deleteAll();
            notificationRepository.deleteAll();
            userRepository.deleteAll();
        } catch (Exception e) {
            System.err.println("Cleanup failed: " + e.getMessage());
        }
    }

    @Test
    void testAdvertisingFlow_Success() throws Exception {
        // --- BƯỚC 1: TẠO HỢP ĐỒNG (Role: ADVERTISING) ---
        AdContractRequest contractRequest = new AdContractRequest(
                companyId,
                routeId,
                LocalDate.now(),
                LocalDate.now().plusMonths(1),
                new BigDecimal("5000000"),
                1,
                "http://file-storage.com/contract.pdf");

        MvcResult contractResult = mockMvc.perform(post("/api/v1/ads/contracts")
                .header("Authorization", "Bearer " + advertisingToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(contractRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.approvalStatus").value("PENDING"))
                .andReturn();

        String contractResponse = contractResult.getResponse().getContentAsString();
        Integer contractId = objectMapper.readTree(contractResponse).get("id").asInt();

        // --- BƯỚC 2: PHÊ DUYỆT HỢP ĐỒNG (Role: ACCOUNTANT) ---
        mockMvc.perform(patch("/api/v1/ads/contracts/{id}/approve", contractId)
                .header("Authorization", "Bearer " + accountantToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.approvalStatus").value("APPROVED"));

        // Kiểm tra Notification cho Admin
        List<Notification> notifications = notificationRepository.findAll();
        boolean found = notifications.stream().anyMatch(n -> n.getUser().getId().equals(adminId) &&
                n.getMessage().contains("phê duyệt"));
        assertTrue(found, "Phải có thông báo gửi cho Admin sau khi phê duyệt hợp đồng");

        // --- BƯỚC 3: GÁN QUẢNG CÁO LÊN XE (Role: ADMIN) ---
        AdAssignmentRequest assignmentRequest = new AdAssignmentRequest(contractId, busId);

        mockMvc.perform(post("/api/v1/ads/assignments")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(assignmentRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        // Kiểm tra trạng thái xe buýt
        Bus bus = busRepository.findById(busId).orElseThrow();
        assertTrue(bus.getIsAdvertised(), "Trạng thái xe buýt is_advertised phải là true sau khi gán quảng cáo");
    }

    @Test
    void testRejectContract_Success() throws Exception {
        // GIVEN: A pending contract
        AdContract contract = new AdContract();
        contract.setCompany(adCompanyRepository.findById(companyId).orElseThrow());
        contract.setRoute(routeRepository.findById(routeId).orElseThrow());
        contract.setStartDate(LocalDate.now());
        contract.setEndDate(LocalDate.now().plusMonths(1));
        contract.setPricePerBus(new BigDecimal("1000000"));
        contract.setBusQuantity(1);
        contract.setApprovalStatus(AdContractStatus.PENDING);
        contract = adContractRepository.save(contract);

        // WHEN: Accountant rejects it
        mockMvc.perform(patch("/api/v1/ads/contracts/{id}/reject", contract.getId())
                .header("Authorization", "Bearer " + accountantToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.approvalStatus").value("REJECTED"));

        // THEN: Admin should get a notification
        List<Notification> notifications = notificationRepository.findAll();
        boolean found = notifications.stream().anyMatch(n -> n.getUser().getId().equals(adminId) &&
                n.getMessage().contains("từ chối"));
        assertTrue(found, "Phải có thông báo gửi cho Admin sau khi từ chối hợp đồng");
    }
}
