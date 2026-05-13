package com.bfms.bfms_backend.integration;

import com.bfms.bfms_backend.dtos.req.NodeRequest;
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
public class NodeControllerIntegrationTest {

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
    private NodeRepository nodeRepository;

    private Integer routeId;
    private Integer adminId;
    private String jwtToken;

    @BeforeEach
    void setUp() {
        // Setup Route
        Route route = new Route();
        route.setRouteNumber("R-NODE-" + UUID.randomUUID().toString().substring(0, 8));
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
        nodeRepository.deleteAll();
        if (adminId != null) userRepository.deleteById(adminId);
        if (routeId != null) routeRepository.deleteById(routeId);
    }

    @Test
    void testUpdateNodeSuccess() throws Exception {
        // Create a node first
        Node node = new Node();
        node.setRoute(routeRepository.findById(routeId).orElseThrow());
        node.setNodeNumber(1);
        node.setExecutionDate(LocalDate.now());
        node = nodeRepository.save(node);

        NodeRequest updateRequest = new NodeRequest(2, LocalDate.now(), "Updated description");

        mockMvc.perform(put("/api/v1/nodes/{id}", node.getId())
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nodeNumber").value(2))
                .andExpect(jsonPath("$.description").value("Updated description"));
    }

    @Test
    void testDeleteNodeSuccess() throws Exception {
        // Create a node first
        Node node = new Node();
        node.setRoute(routeRepository.findById(routeId).orElseThrow());
        node.setNodeNumber(1);
        node.setExecutionDate(LocalDate.now());
        node = nodeRepository.save(node);

        mockMvc.perform(delete("/api/v1/nodes/{id}", node.getId())
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNoContent());
    }
}
