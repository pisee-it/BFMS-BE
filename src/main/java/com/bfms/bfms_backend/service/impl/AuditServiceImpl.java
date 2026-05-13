package com.bfms.bfms_backend.service.impl;

import com.bfms.bfms_backend.entity.SecurityLog;
import com.bfms.bfms_backend.repository.SecurityLogRepository;
import com.bfms.bfms_backend.service.AuditService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
public class AuditServiceImpl implements AuditService {

    private final SecurityLogRepository securityLogRepository;

    public AuditServiceImpl(SecurityLogRepository securityLogRepository) {
        this.securityLogRepository = securityLogRepository;
    }

    @Override
    @Transactional
    public void log(String action, String description) {
        SecurityLog log = new SecurityLog();
        log.setAction(action);
        log.setDescription(description);

        // 1. Lấy username từ SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() 
                && !"anonymousUser".equals(authentication.getPrincipal())) {
            log.setUsername(authentication.getName());
        } else {
            log.setUsername("SYSTEM/GUEST");
        }

        // 2. Lấy IP Address từ HttpServletRequest
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            String ip = request.getHeader("X-Forwarded-For");
            if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getRemoteAddr();
            }
            log.setIpAddress(ip);
        }

        securityLogRepository.save(log);
    }

    @Override
    public Page<SecurityLog> getAllLogs(String username, String action, Pageable pageable) {
        return securityLogRepository.findByUsernameAndAction(username, action, pageable);
    }
}
