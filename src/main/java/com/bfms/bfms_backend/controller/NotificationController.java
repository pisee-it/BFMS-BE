package com.bfms.bfms_backend.controller;

import com.bfms.bfms_backend.dtos.res.NotificationResponse;
import com.bfms.bfms_backend.entity.AppUser;
import com.bfms.bfms_backend.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getMyNotifications() {
        AppUser currentUser = (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<NotificationResponse> responses = notificationService.getNotificationsForUser(currentUser.getId())
                .stream()
                .map(n -> new NotificationResponse(n.getId(), n.getMessage(), n.getIsRead(), n.getCreatedAt()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Integer id) {
        notificationService.markAsRead(id);
        return ResponseEntity.noContent().build();
    }
}
