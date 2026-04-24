package com.bfms.bfms_backend.service;

import com.bfms.bfms_backend.entity.Notification;
import java.util.List;

public interface NotificationService {
    void notify(Integer userId, String message);
    List<Notification> getNotificationsForUser(Integer userId);
    void markAsRead(Integer notificationId);
}
