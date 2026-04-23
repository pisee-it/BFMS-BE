package com.bfms.bfms_backend.service;

import com.bfms.bfms_backend.dtos.req.CompleteShiftRequest;
import com.bfms.bfms_backend.entity.*;
import com.bfms.bfms_backend.repository.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class BusShiftServiceRollbackTest {

    @Autowired
    private BusShiftService busShiftService;

    @MockitoSpyBean
    private BusShiftRepository busShiftRepository;

    @Autowired
    private NodeRepository nodeRepository;

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private BusRepository busRepository;

    @Autowired
    private AppUserRepository userRepository;

    @Autowired
    private DailyTicketStatRepository dailyTicketStatRepository;

    private Integer shiftId;
    private Integer nodeId;
    private Integer routeId;
    private Integer driverId;
    private Integer busId;

    @BeforeEach
    void setUp() {
        // Setup data mẫu
        Route route = new Route();
        route.setRouteNumber("R-ROLLBACK-" + UUID.randomUUID().toString().substring(0, 8));
        route.setStopA("A");
        route.setStopB("B");
        route.setPath("A-B");
        route.setPrice(new BigDecimal("10000"));
        route = routeRepository.save(route);
        routeId = route.getId();

        Node node = new Node();
        node.setRoute(route);
        node.setNodeNumber(1);
        node.setExecutionDate(LocalDate.now());
        node = nodeRepository.save(node);
        nodeId = node.getId();

        AppUser driver = new AppUser();
        driver.setUsername("staff_" + UUID.randomUUID().toString().substring(0, 8));
        driver.setPassword("password");
        driver.setRole(Role.STAFF);
        driver.setFullName("Test Staff");
        driver = userRepository.save(driver);
        driverId = driver.getId();

        Bus bus = new Bus();
        bus.setRoute(route);
        bus.setBusModel("Test Model");
        bus.setManufacturer("Test Brand");
        bus.setCapacity(45);
        bus.setYom(2023);
        bus.setLicensePlate("TEST-" + UUID.randomUUID().toString().substring(0, 5));
        bus.setStatus(BusStatus.ACTIVE);
        bus.setIsAdvertised(false);
        bus = busRepository.save(bus);
        busId = bus.getId();

        BusShift shift = new BusShift();
        shift.setNode(node);
        shift.setBus(bus);
        shift.setDriver(driver);
        shift.setStatus(ShiftStatus.IN_PROGRESS);
        shift.setPlannedDepartureTime(LocalTime.of(8, 0));
        shift.setDirection((short) 1);
        shift = busShiftRepository.save(shift);
        shiftId = shift.getId();
    }

    @AfterEach
    void tearDown() {
        // Dọn dẹp dữ liệu theo thứ tự ngược lại
        try {
            busShiftRepository.deleteById(shiftId);
            busRepository.deleteById(busId);
            userRepository.deleteById(driverId);
            nodeRepository.deleteById(nodeId);

            // Xóa DailyTicketStat nếu có tạo ra (trong trường hợp rollback thất bại)
            dailyTicketStatRepository.findByRouteIdAndReportDate(routeId, LocalDate.now())
                    .ifPresent(stat -> dailyTicketStatRepository.delete(stat));

            routeRepository.deleteById(routeId);
        } catch (Exception e) {
            System.err.println("Cleanup failed: " + e.getMessage());
        }
    }

    @Test
    void testCompleteShiftRollback_WhenExceptionOccursAtEnd() {
        // GIVEN
        CompleteShiftRequest request = new CompleteShiftRequest(50, 10);

        // Giả lập lỗi tại lần gọi save() cuối cùng (line 120 trong BusShiftServiceImpl)
        doAnswer(invocation -> {
            BusShift s = invocation.getArgument(0);
            if (s.getStatus() == ShiftStatus.COMPLETED && s.getShiftRevenue() != null) {
                throw new RuntimeException("Simulated DB Failure at the end of transaction");
            }
            return invocation.callRealMethod();
        }).when(busShiftRepository).save(any(BusShift.class));

        // WHEN
        assertThrows(RuntimeException.class, () -> {
            busShiftService.completeShift(shiftId, request);
        });

        // THEN: Kiểm tra Rollback
        BusShift shiftCheck = busShiftRepository.findById(shiftId).orElseThrow();
        assertEquals(ShiftStatus.IN_PROGRESS, shiftCheck.getStatus(), "BusShift status must be rolled back");
        assertEquals(0, shiftCheck.getTotalSingleTickets(), "BusShift tickets must be rolled back");

        Node nodeCheck = nodeRepository.findById(nodeId).orElseThrow();
        assertEquals(0, nodeCheck.getTotalPassengers(), "Node passengers must be rolled back");

        boolean statExists = dailyTicketStatRepository.findByRouteIdAndReportDate(routeId, LocalDate.now()).isPresent();
        assertFalse(statExists, "DailyTicketStat must be rolled back (not created)");
    }
}
