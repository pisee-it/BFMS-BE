package com.bfms.bfms_backend.service.impl;

import com.bfms.bfms_backend.entity.AppUser;
import com.bfms.bfms_backend.entity.Notification;
import com.bfms.bfms_backend.entity.Role;
import com.bfms.bfms_backend.exception.AppException;
import com.bfms.bfms_backend.exception.ErrorCode;
import com.bfms.bfms_backend.repository.AppUserRepository;
import com.bfms.bfms_backend.repository.NotificationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private AppUserRepository appUserRepository;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    @Test
    void notify_ShouldSaveNotification_WhenUserExists() {
        // 1. Mock
        AppUser user = new AppUser();
        user.setId(1);
        when(appUserRepository.findById(1)).thenReturn(Optional.of(user));

        // 2. Execute
        notificationService.notify(1, "Test Message");

        // 3. Verify
        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository).save(captor.capture());
        
        Notification saved = captor.getValue();
        assertEquals(user, saved.getUser());
        assertEquals("Test Message", saved.getMessage());
        assertFalse(saved.getIsRead());
    }

    @Test
    void notify_ShouldThrowException_WhenUserNotFound() {
        when(appUserRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(AppException.class, () -> notificationService.notify(999, "Msg"));
    }

    @Test
    void notifyAdmins_ShouldCallNotifyForEachAdmin() {
        // 1. Mock Admins
        AppUser admin1 = new AppUser(); admin1.setId(101);
        AppUser admin2 = new AppUser(); admin2.setId(102);
        when(appUserRepository.findByRole(Role.ADMIN)).thenReturn(List.of(admin1, admin2));
        
        // Cần mock cả findById vì notify() gọi findById
        when(appUserRepository.findById(101)).thenReturn(Optional.of(admin1));
        when(appUserRepository.findById(102)).thenReturn(Optional.of(admin2));

        // 2. Execute
        notificationService.notifyAdmins("Alert");

        // 3. Verify
        verify(notificationRepository, times(2)).save(any(Notification.class));
    }

    @Test
    void markAsRead_ShouldUpdateIsReadStatus() {
        // 1. Mock
        Notification n = new Notification();
        n.setId(50);
        n.setIsRead(false);
        when(notificationRepository.findById(50)).thenReturn(Optional.of(n));

        // 2. Execute
        notificationService.markAsRead(50);

        // 3. Verify
        assertTrue(n.getIsRead());
        verify(notificationRepository).save(n);
    }
}
