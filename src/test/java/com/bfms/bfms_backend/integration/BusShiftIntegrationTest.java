package com.bfms.bfms_backend.integration;

import com.bfms.bfms_backend.dtos.req.CompleteShiftRequest;
import com.bfms.bfms_backend.entity.*;
import com.bfms.bfms_backend.repository.*;
import com.bfms.bfms_backend.security.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.time.LocalTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class BusShiftIntegrationTest {

    static {
        // Nạp biến môi trường từ .env cho test context
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));
    }

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private BusRepository busRepository;

    @Autowired
    private NodeRepository nodeRepository;

    @Autowired
    private AppUserRepository userRepository;

    @Autowired
    private BusShiftRepository busShiftRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private DailyTicketStatRepository dailyTicketStatRepository;

    private Integer shiftId;
    private Integer nodeId;
    private Integer routeId;
    private Integer driverId;
    private Integer busId;
    private String jwtToken;

    @BeforeEach
    void setUp() {
        // 1. Setup Route
        Route route = new Route();
        route.setRouteNumber("R-INT-" + UUID.randomUUID().toString().substring(0, 8));
        route.setStopA("Start");
        route.setStopB("End");
        route.setPath("Start-End");
        route.setPrice(new BigDecimal("8000"));
        route = routeRepository.save(route);
        routeId = route.getId();

        // 2. Setup Node
        Node node = new Node();
        node.setRoute(route);
        node.setNodeNumber(1);
        node.setExecutionDate(LocalDate.now());
        node = nodeRepository.save(node);
        nodeId = node.getId();

        // 3. Setup Staff User & JWT
        String username = "staff_" + UUID.randomUUID().toString().substring(0, 8);
        AppUser staff = new AppUser();
        staff.setUsername(username);
        staff.setPassword(passwordEncoder.encode("password123"));
        staff.setRole(Role.STAFF);
        staff.setFullName("Integration Test Staff");
        staff = userRepository.save(staff);
        driverId = staff.getId();

        jwtToken = jwtUtil.generateToken(username);

        // 4. Setup Bus
        Bus bus = new Bus();
        bus.setRoute(route);
        bus.setBusModel("Standard-45");
        bus.setManufacturer("Hyundai");
        bus.setYom(2024);
        bus.setIsAdvertised(false);
        bus.setLicensePlate("INT-" + UUID.randomUUID().toString().substring(0, 5));
        bus.setCapacity(45);
        bus.setStatus(BusStatus.ACTIVE);
        bus = busRepository.save(bus);
        busId = bus.getId();

        // 5. Setup BusShift
        BusShift shift = new BusShift();
        shift.setNode(node);
        shift.setBus(bus);
        shift.setDriver(staff);
        shift.setStatus(ShiftStatus.IN_PROGRESS);
        shift.setDirection((short) 1);
        shift.setPlannedDepartureTime(LocalTime.now());
        shift = busShiftRepository.save(shift);
        shiftId = shift.getId();
    }

    @AfterEach
    @Transactional
    void tearDown() {
        try {
            if (shiftId != null) {
                ticketRepository.deleteByBusShiftId(shiftId);
                busShiftRepository.deleteById(shiftId);
            }
            if (busId != null) {
                busRepository.deleteById(busId);
            }
            if (driverId != null) {
                userRepository.deleteById(driverId);
            }
            if (nodeId != null) {
                nodeRepository.deleteById(nodeId);
            }

            if (routeId != null) {
                dailyTicketStatRepository.findByRouteIdAndReportDate(routeId, LocalDate.now())
                        .ifPresent(stat -> dailyTicketStatRepository.delete(stat));

                routeRepository.deleteById(routeId);
            }
        } catch (Exception e) {
            System.err.println("Cleanup failed in Integration Test: " + e.getMessage());
        }
    }

    @Test
    void testCompleteShiftFlowSuccess() throws Exception {
        // GIVEN
        CompleteShiftRequest request = new CompleteShiftRequest(50, 20);

        // WHEN & THEN
        mockMvc.perform(post("/api/v1/shifts/{shiftId}/complete", shiftId)
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.singleTicketCount").value(50))
                .andExpect(jsonPath("$.monthlyTicketCount").value(20));

        // VERIFY DB - BusShift
        BusShift shiftCheck = busShiftRepository.findById(shiftId).orElseThrow();
        assertEquals(ShiftStatus.COMPLETED, shiftCheck.getStatus());
        assertEquals(50, shiftCheck.getTotalSingleTickets());
        assertEquals(20, shiftCheck.getTotalMonthlyTickets());

        // Revenue = 50 * 8000 = 400,000
        BigDecimal expectedRevenue = new BigDecimal("400000.00");
        assertEquals(0, expectedRevenue.compareTo(shiftCheck.getShiftRevenue()));

        // VERIFY DB - DailyTicketStat
        DailyTicketStat statCheck = dailyTicketStatRepository.findByRouteIdAndReportDate(routeId, LocalDate.now())
                .orElseThrow(() -> new RuntimeException("DailyTicketStat should be created"));

        assertEquals(50, statCheck.getSingleTicketCount());
        assertEquals(20, statCheck.getMonthlyTicketCount());
        assertEquals(70, statCheck.getTotalPassengers());
        assertEquals(0, expectedRevenue.compareTo(statCheck.getRevenueSingleTickets()));

        // VERIFY DB - Node
        Node nodeCheck = nodeRepository.findById(nodeId).orElseThrow();
        assertEquals(70, nodeCheck.getTotalPassengers());
    }

    @Test
    void testCompleteShift_Unauthorized_ShouldReturn403() throws Exception {
        CompleteShiftRequest request = new CompleteShiftRequest(10, 5);

        // Call without token
        mockMvc.perform(post("/api/v1/shifts/{shiftId}/complete", shiftId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }
}
