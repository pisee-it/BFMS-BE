package com.bfms.bfms_backend.controller;

import com.bfms.bfms_backend.dtos.res.NotificationResponse;
import com.bfms.bfms_backend.entity.AppUser;
import com.bfms.bfms_backend.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notifications")
@Tag(name = "Thông báo (Notifications)", description = "Các API truy vấn và quản lý trạng thái thông báo hệ thống")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Lấy danh sách thông báo", description = "Lấy danh sách thông báo của người dùng hiện tại (phân trang). Quyền: ADMIN")
    public ResponseEntity<Page<NotificationResponse>> getMyNotifications(Pageable pageable) {
        AppUser currentUser = (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Page<NotificationResponse> responses = notificationService
                .getNotificationsForUser(currentUser.getId(), pageable)
                .map(n -> new NotificationResponse(n.getId(), n.getMessage(), n.getIsRead(), n.getCreatedAt()));
        return ResponseEntity.ok(responses);
    }

    @PatchMapping("/{id}/read")
    @Operation(summary = "Đánh dấu đã đọc", description = "Cập nhật trạng thái thông báo thành đã đọc.")
    public ResponseEntity<Void> markAsRead(@PathVariable Integer id) {
        notificationService.markAsRead(id);
        return ResponseEntity.noContent().build();
    }
}
