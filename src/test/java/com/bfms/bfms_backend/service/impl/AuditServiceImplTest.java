package com.bfms.bfms_backend.service.impl;

import com.bfms.bfms_backend.entity.SecurityLog;
import com.bfms.bfms_backend.repository.SecurityLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditServiceImplTest {

    @Mock
    private SecurityLogRepository securityLogRepository;

    @InjectMocks
    private AuditServiceImpl auditService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Mock
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.setContext(securityContext);
        ServletRequestAttributes attributes = new ServletRequestAttributes(request);
        RequestContextHolder.setRequestAttributes(attributes);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void log_ShouldSaveSecurityLogWithUserAndIp() {
        // 1. Mock SecurityContext
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("test-user");

        // 2. Mock Request
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");

        // 3. Execute
        auditService.log("TEST_ACTION", "Test Description");

        // 4. Verify
        ArgumentCaptor<SecurityLog> captor = ArgumentCaptor.forClass(SecurityLog.class);
        verify(securityLogRepository).save(captor.capture());

        SecurityLog savedLog = captor.getValue();
        assertEquals("TEST_ACTION", savedLog.getAction());
        assertEquals("Test Description", savedLog.getDescription());
        assertEquals("test-user", savedLog.getUsername());
        assertEquals("127.0.0.1", savedLog.getIpAddress());
    }

    @Test
    void log_ShouldSaveWithSystemUser_WhenNoAuthentication() {
        // 1. Mock empty security context
        when(securityContext.getAuthentication()).thenReturn(null);
        when(request.getRemoteAddr()).thenReturn("192.168.1.1");

        // 2. Execute
        auditService.log("GUEST_ACTION", "Guest Task");

        // 3. Verify
        ArgumentCaptor<SecurityLog> captor = ArgumentCaptor.forClass(SecurityLog.class);
        verify(securityLogRepository).save(captor.capture());

        SecurityLog savedLog = captor.getValue();
        assertEquals("SYSTEM/GUEST", savedLog.getUsername());
        assertEquals("192.168.1.1", savedLog.getIpAddress());
    }
}
