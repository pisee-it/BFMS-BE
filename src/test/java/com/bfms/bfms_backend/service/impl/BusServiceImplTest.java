package com.bfms.bfms_backend.service.impl;

import com.bfms.bfms_backend.dtos.req.BusRequest;
import com.bfms.bfms_backend.dtos.res.BusResponse;
import com.bfms.bfms_backend.entity.Bus;
import com.bfms.bfms_backend.entity.BusStatus;
import com.bfms.bfms_backend.entity.Route;
import com.bfms.bfms_backend.exception.AppException;
import com.bfms.bfms_backend.exception.ErrorCode;
import com.bfms.bfms_backend.mapper.BusMapper;
import com.bfms.bfms_backend.repository.BusRepository;
import com.bfms.bfms_backend.service.AuditService;
import com.bfms.bfms_backend.util.EntityLookupHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BusServiceImplTest {

    @Mock
    private BusRepository busRepository;

    @Mock
    private BusMapper busMapper;

    @Mock
    private EntityLookupHelper lookupHelper;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private BusServiceImpl busService;

    private BusRequest busRequest;
    private Bus mockBus;
    private Route mockRoute;

    @BeforeEach
    void setUp() {
        busRequest = new BusRequest(1, "Hyundai", "Manufacturer", 45, 2020, "29B-99999", BusStatus.ACTIVE, false);
        mockRoute = new Route();
        mockRoute.setId(1);
        
        mockBus = new Bus();
        mockBus.setId(10);
        mockBus.setLicensePlate("29B-99999");
    }

    @Test
    void createBus_ShouldReturnResponse_WhenValid() {
        // 1. Mock
        when(busRepository.findByLicensePlate(anyString())).thenReturn(Optional.empty());
        when(lookupHelper.getRoute(1)).thenReturn(mockRoute);
        when(busMapper.toEntity(busRequest)).thenReturn(mockBus);
        when(busRepository.save(any(Bus.class))).thenReturn(mockBus);
        when(busMapper.toResponse(any(Bus.class))).thenReturn(new BusResponse(10, 1, "T01", "Hyundai", "Manufacturer", 45, 2020, "29B-99999", BusStatus.ACTIVE, false));

        // 2. Execute
        BusResponse response = busService.createBus(busRequest);

        // 3. Verify
        assertNotNull(response);
        assertEquals("29B-99999", response.licensePlate());
        verify(auditService).log(eq("CREATE_BUS"), anyString());
    }

    @Test
    void createBus_ShouldThrowException_WhenLicensePlateExists() {
        // 1. Mock duplicate
        when(busRepository.findByLicensePlate("29B-99999")).thenReturn(Optional.of(mockBus));

        // 2. Execute & Verify
        AppException exception = assertThrows(AppException.class, () -> busService.createBus(busRequest));
        assertEquals(ErrorCode.BUS_ALREADY_EXISTS, exception.getErrorCode());
    }

    @Test
    void sellBus_ShouldUpdateStatusToSold() {
        // 1. Mock
        when(lookupHelper.getBus(10)).thenReturn(mockBus);
        
        // 2. Execute
        busService.sellBus(10);

        // 3. Verify
        assertEquals(BusStatus.SOLD, mockBus.getStatus());
        verify(busRepository).save(mockBus);
        verify(auditService).log(eq("SELL_BUS"), anyString());
    }
}
