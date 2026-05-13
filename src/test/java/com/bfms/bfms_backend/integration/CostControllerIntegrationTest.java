package com.bfms.bfms_backend.integration;

import com.bfms.bfms_backend.dtos.req.CostRequest;
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
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CostControllerIntegrationTest {

    static {
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
    private AppUserRepository userRepository;

    @Autowired
    private OperationalCostRepository costRepository;

    private Integer routeId;
    private Integer adminId;
    private String jwtToken;

    @BeforeEach
    void setUp() {
        // Setup Route
        Route route = new Route();
        route.setRouteNumber("R-COST-" + UUID.randomUUID().toString().substring(0, 8));
        route.setStopA("A");
        route.setStopB("B");
        route.setPath("A-B");
        route.setPrice(new BigDecimal("10000"));
        route = routeRepository.save(route);
        routeId = route.getId();

        // Setup Admin User
        String username = "admin_" + UUID.randomUUID().toString().substring(0, 8);
        AppUser admin = new AppUser();
        admin.setUsername(username);
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setRole(Role.ADMIN);
        admin.setFullName("Test Admin");
        admin = userRepository.save(admin);
        adminId = admin.getId();

        jwtToken = jwtUtil.generateToken(username);
    }

    @AfterEach
    @Transactional
    void tearDown() {
        costRepository.deleteAll();
        if (adminId != null) userRepository.deleteById(adminId);
        if (routeId != null) routeRepository.deleteById(routeId);
    }

    @Test
    void testCreateCostSuccess() throws Exception {
        CostRequest request = new CostRequest(routeId, LocalDate.now(), CostType.FUEL, new BigDecimal("500000"), "Fuel for today");

        mockMvc.perform(post("/api/v1/costs")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(500000))
                .andExpect(jsonPath("$.type").value("FUEL"));
    }

    @Test
    void testGetCostsSuccess() throws Exception {
        // Create a cost first
        OperationalCost cost = new OperationalCost();
        cost.setRoute(routeRepository.findById(routeId).orElseThrow());
        cost.setCostDate(LocalDate.now());
        cost.setType(CostType.MAINTENANCE);
        cost.setAmount(new BigDecimal("1000000"));
        costRepository.save(cost);

        mockMvc.perform(get("/api/v1/costs")
                .header("Authorization", "Bearer " + jwtToken)
                .param("startDate", LocalDate.now().toString())
                .param("endDate", LocalDate.now().toString())
                .param("routeId", routeId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].type").value("MAINTENANCE"));
    }
}
