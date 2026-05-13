package com.bfms.bfms_backend.controller;

import com.bfms.bfms_backend.entity.SecurityLog;
import com.bfms.bfms_backend.service.AuditService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/security-logs")
@RequiredArgsConstructor
@Tag(name = "Security Logs", description = "Truy vấn nhật ký bảo mật hệ thống")
public class SecurityLogController {

    private final AuditService auditService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Lấy danh sách nhật ký bảo mật", description = "Chỉ Admin mới có quyền truy cập")
    public ResponseEntity<Page<SecurityLog>> getAllLogs(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String action,
            Pageable pageable) {
        return ResponseEntity.ok(auditService.getAllLogs(username, action, pageable));
    }
}
