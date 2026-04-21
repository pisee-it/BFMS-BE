package com.bfms.service.impl;

import dtos.req.RouteRequest;
import dtos.res.RouteResponse;
import entity.Route;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.RouteRepository;
import service.impl.RouteServiceImpl;

import java.math.BigDecimal;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RouteServiceImplTest {

    @Mock
    private RouteRepository routeRepository;

    @InjectMocks
    private RouteServiceImpl routeService;

    // --- 1. TEST VALIDATION ---
    // Các test case này sẽ dừng lại ngay khi gặp lỗi nên không được khai báo stubbing save() chung

    @Test
    void createRoute_ShouldThrowException_WhenDistanceABIsNegative() {
        // 1. Giả lập dữ liệu có khoảng cách đi âm
        RouteRequest req = createRequest(new BigDecimal("-1"), BigDecimal.TEN);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            routeService.createRoute(req);
        });
        assertEquals("Khoảng cách không được nhỏ hơn 0", exception.getMessage());
    }

    @Test
    void createRoute_ShouldThrowException_WhenDistanceBAIsNegative() {
        // 2. Giả lập dữ liệu có khoảng cách về âm
        RouteRequest req = createRequest(BigDecimal.TEN, new BigDecimal("-5"));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            routeService.createRoute(req);
        });
        assertEquals("Khoảng cách không được nhỏ hơn 0", exception.getMessage());
    }

    // --- 2. TEST AUTOMATIC PRICING LOGIC ---
    // Chỉ khai báo stubbing trong các hàm thực sự đi tới bước lưu vào Database

    @Test
    void createRoute_ShouldSetPrice8000_WhenAverageDistanceUnder15() {
        // 3. Khai báo stubbing cục bộ để tránh UnnecessaryStubbingException
        mockSaveOperation();

        RouteResponse res = routeService.createRoute(createRequest(BigDecimal.valueOf(10), BigDecimal.valueOf(10)));
        assertEquals(0, res.price().compareTo(BigDecimal.valueOf(8000)));
    }

    @Test
    void createRoute_ShouldSetPrice10000_WhenAverageDistanceBetween15And25() {
        mockSaveOperation();

        RouteResponse res = routeService.createRoute(createRequest(BigDecimal.valueOf(15), BigDecimal.valueOf(25)));
        assertEquals(0, res.price().compareTo(BigDecimal.valueOf(10000)));
    }

    @Test
    void createRoute_ShouldSetPrice12000_WhenAverageDistanceBetween25And30() {
        mockSaveOperation();

        RouteResponse res = routeService.createRoute(createRequest(BigDecimal.valueOf(27), BigDecimal.valueOf(27)));
        assertEquals(0, res.price().compareTo(BigDecimal.valueOf(12000)));
    }

    @Test
    void createRoute_ShouldSetPrice15000_WhenAverageDistanceBetween30And40() {
        mockSaveOperation();

        RouteResponse res = routeService.createRoute(createRequest(BigDecimal.valueOf(35), BigDecimal.valueOf(35)));
        assertEquals(0, res.price().compareTo(BigDecimal.valueOf(15000)));
    }

    @Test
    void createRoute_ShouldSetPrice20000_WhenAverageDistanceAbove40() {
        mockSaveOperation();

        RouteResponse res = routeService.createRoute(createRequest(BigDecimal.valueOf(45), BigDecimal.valueOf(45)));
        assertEquals(0, res.price().compareTo(BigDecimal.valueOf(20000)));
    }

    // --- 3. HELPER METHODS ---

    private void mockSaveOperation() {
        // 4. Trả về chính đối tượng Route sau khi đã được Service tính toán và setPrice
        when(routeRepository.save(any(Route.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    private RouteRequest createRequest(BigDecimal distAB, BigDecimal distBA) {
        return new RouteRequest(
                "T01", "Stop A", "Stop B", "Path Description",
                distAB, distBA, LocalTime.of(5, 0), LocalTime.of(22, 0),
                null
        );
    }
}