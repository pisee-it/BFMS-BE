package com.bfms.bfms_backend.service.impl;

import com.bfms.bfms_backend.entity.AppUser;
import com.bfms.bfms_backend.entity.Notification;
import com.bfms.bfms_backend.repository.AppUserRepository;
import com.bfms.bfms_backend.repository.NotificationRepository;
import com.bfms.bfms_backend.service.NotificationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.bfms.bfms_backend.exception.AppException;
import com.bfms.bfms_backend.exception.ErrorCode;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final AppUserRepository appUserRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository, AppUserRepository appUserRepository) {
        this.notificationRepository = notificationRepository;
        this.appUserRepository = appUserRepository;
    }

    @Override
    @Transactional
    public void notify(Integer userId, String message) {
        AppUser user = appUserRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setMessage(message);
        notification.setIsRead(false);
        notification.setCreatedAt(LocalDateTime.now());

        notificationRepository.save(notification);
    }

    @Override
    public Page<Notification> getNotificationsForUser(Integer userId, Pageable pageable) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }

    @Override
    @Transactional
    public void markAsRead(Integer notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new AppException(ErrorCode.NOTIFICATION_NOT_FOUND));
        notification.setIsRead(true);
        notificationRepository.save(notification);
    }
}
