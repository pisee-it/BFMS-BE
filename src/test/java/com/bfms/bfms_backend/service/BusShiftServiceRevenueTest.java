package com.bfms.bfms_backend.service;

import com.bfms.bfms_backend.dtos.req.CompleteShiftRequest;
import com.bfms.bfms_backend.entity.*;
import com.bfms.bfms_backend.repository.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class BusShiftServiceRevenueTest {

    @Autowired
    private BusShiftService busShiftService;

    @Autowired
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

    @Autowired
    private TicketRepository ticketRepository;

    private Integer shiftId;
    private Integer nodeId;
    private Integer routeId;
    private Integer driverId;
    private Integer busId;

    @BeforeEach
    void setUp() {
        // 1. Setup Route với giá vé 7000 VNĐ
        Route route = new Route();
        route.setRouteNumber("R-TEST-" + UUID.randomUUID().toString().substring(0, 8));
        route.setStopA("A");
        route.setStopB("B");
        route.setPath("A-B");
        route.setPrice(new BigDecimal("7000"));
        route = routeRepository.save(route);
        routeId = route.getId();

        // 2. Setup Node cho ngày hôm nay
        Node node = new Node();
        node.setRoute(route);
        node.setNodeNumber(1);
        node.setExecutionDate(LocalDate.now());
        node = nodeRepository.save(node);
        nodeId = node.getId();

        // 3. Setup Driver (Staff)
        AppUser driver = new AppUser();
        driver.setUsername("staff_" + UUID.randomUUID().toString().substring(0, 8));
        driver.setPassword("password");
        driver.setRole(Role.STAFF);
        driver.setFullName("Test Staff");
        driver = userRepository.save(driver);
        driverId = driver.getId();

        // 4. Setup Bus
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

        // 5. Setup BusShift ở trạng thái IN_PROGRESS
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
    @Transactional
    void tearDown() {
        try {
            ticketRepository.deleteByBusShiftId(shiftId);
            busShiftRepository.deleteById(shiftId);
            busRepository.deleteById(busId);
            userRepository.deleteById(driverId);
            nodeRepository.deleteById(nodeId);

            dailyTicketStatRepository.findByRouteIdAndReportDate(routeId, LocalDate.now())
                    .ifPresent(stat -> dailyTicketStatRepository.delete(stat));

            routeRepository.deleteById(routeId);
        } catch (Exception e) {
            System.err.println("Cleanup failed: " + e.getMessage());
        }
    }

    @Test
    void testCompleteShiftRevenueAndPassengersCalculation() {
        // GIVEN: Yêu cầu hoàn thành ca chạy với 45 vé lượt và 15 vé tháng
        CompleteShiftRequest request = new CompleteShiftRequest(45, 15);

        // WHEN: Thực hiện hoàn thành ca chạy
        busShiftService.completeShift(shiftId, request);

        // THEN: Kiểm tra tính toán trong BusShift
        BusShift shiftCheck = busShiftRepository.findById(shiftId).orElseThrow();
        assertEquals(ShiftStatus.COMPLETED, shiftCheck.getStatus());
        assertEquals(45, shiftCheck.getTotalSingleTickets());
        assertEquals(15, shiftCheck.getTotalMonthlyTickets());

        // shift_revenue = 45 * 7000 = 315000
        BigDecimal expectedRevenue = new BigDecimal("315000.00");
        assertEquals(0, expectedRevenue.compareTo(shiftCheck.getShiftRevenue()),
                "Revenue should be 315000 (45 * 7000)");

        // THEN: Kiểm tra tính toán trong Node (total_passengers = SUM từ các shift
        // COMPLETED)
        Node nodeCheck = nodeRepository.findById(nodeId).orElseThrow();
        assertEquals(60, nodeCheck.getTotalPassengers(),
                "Node total passengers should be 60 (45 + 15)");

        // THEN: Kiểm tra DailyTicketStat
        DailyTicketStat statCheck = dailyTicketStatRepository.findByRouteIdAndReportDate(routeId, LocalDate.now())
                .orElseThrow(() -> new RuntimeException("DailyTicketStat not created"));

        assertEquals(45, statCheck.getSingleTicketCount());
        assertEquals(15, statCheck.getMonthlyTicketCount());
        assertEquals(60, statCheck.getTotalPassengers(),
                "Stat total passengers should be 60");
        assertEquals(0, expectedRevenue.compareTo(statCheck.getRevenueSingleTickets()),
                "Stat revenue should be 315000");
    }
}
