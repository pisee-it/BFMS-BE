package com.bfms.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bfms.bfms_backend.dtos.res.BusShiftResponse;
import com.bfms.bfms_backend.entity.AppUser;
import com.bfms.bfms_backend.entity.Bus;
import com.bfms.bfms_backend.entity.Node;
import com.bfms.bfms_backend.entity.Route;
import com.bfms.bfms_backend.entity.BusShift;
import com.bfms.bfms_backend.entity.ShiftStatus;
import com.bfms.bfms_backend.repository.BusShiftRepository;
import com.bfms.bfms_backend.service.impl.BusShiftServiceImpl;

import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BusShiftServiceImplTest {

    @Mock
    private BusShiftRepository busShiftRepository;

    @InjectMocks
    private BusShiftServiceImpl busShiftService;

    private BusShift mockShift;
    private final Integer ROUTE_ID = 1;

    @BeforeEach
    void setUp() {
        // 1. Khởi tạo dữ liệu giả lập (Mock Data)
        Route route = new Route();
        route.setId(ROUTE_ID);

        Node node = new Node();
        node.setRoute(route);

        Bus bus = new Bus();
        bus.setLicensePlate("29B-12345");

        AppUser driver = new AppUser();
        driver.setFullName("Nguyen Van A");

        mockShift = new BusShift();
        mockShift.setId(100);
        mockShift.setNode(node);
        mockShift.setBus(bus);
        mockShift.setDriver(driver);
        mockShift.setShiftOrder(1);
        mockShift.setStatus(ShiftStatus.IN_PROGRESS);
        mockShift.setPlannedDepartureTime(LocalTime.of(8, 0));
    }

    @Test
    void getActiveShiftsByRoute_ShouldReturnOnlyInProgressShifts() {
        // 2. Giả lập Repository trả về danh sách có status IN_PROGRESS
        when(busShiftRepository.findActiveShifts(ROUTE_ID, "IN_PROGRESS"))
                .thenReturn(List.of(mockShift));

        // 3. Gọi service
        List<BusShiftResponse> responses = busShiftService.getActiveShiftsByRoute(ROUTE_ID);

        // 4. Kiểm chứng kết quả
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("IN_PROGRESS", responses.get(0).status().toString());

        // 5. Xác minh Repository được gọi đúng 1 lần với đúng tham số
        verify(busShiftRepository, times(1)).findActiveShifts(ROUTE_ID, "IN_PROGRESS");
    }

    @Test
    void getActiveShiftsByRoute_ShouldFilterCorrectRouteId() {
        // 6. Test với một Route ID khác để đảm bảo filter hoạt động
        Integer differentRouteId = 99;
        when(busShiftRepository.findActiveShifts(differentRouteId, "IN_PROGRESS"))
                .thenReturn(List.of());

        List<BusShiftResponse> responses = busShiftService.getActiveShiftsByRoute(differentRouteId);

        // 7. Kết quả phải trống nếu Repository không tìm thấy nốt nào thuộc Route 99
        assertTrue(responses.isEmpty());
        verify(busShiftRepository).findActiveShifts(differentRouteId, "IN_PROGRESS");
    }
}