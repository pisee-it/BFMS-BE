package com.bfms.bfms_backend.service;

public interface AuditService {
    /**
     * Ghi lại nhật ký bảo mật hoặc thao tác dữ liệu quan trọng.
     * @param action Hành động thực hiện (VD: LOGIN_SUCCESS, CREATE_ROUTE)
     * @param description Mô tả chi tiết hành động
     */
    void log(String action, String description);

    org.springframework.data.domain.Page<com.bfms.bfms_backend.entity.SecurityLog> getAllLogs(String username, String action, org.springframework.data.domain.Pageable pageable);
}
