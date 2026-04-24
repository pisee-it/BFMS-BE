package com.bfms.bfms_backend.service;

import com.bfms.bfms_backend.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface NotificationService {
    void notify(Integer userId, String message);
    Page<Notification> getNotificationsForUser(Integer userId, Pageable pageable);
    void markAsRead(Integer notificationId);
}
