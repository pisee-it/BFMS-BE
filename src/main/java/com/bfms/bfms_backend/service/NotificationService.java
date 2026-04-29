package com.bfms.bfms_backend.service;

import com.bfms.bfms_backend.entity.Notification;
import com.bfms.bfms_backend.repository.projection.NotificationProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface NotificationService {
    void notify(Integer userId, String message);
    void notifyAdmins(String message);
    Page<NotificationProjection> getNotificationsForUser(Integer userId, Pageable pageable);

    void markAsRead(Integer notificationId);
}
