package com.bfms.bfms_backend.dtos.res;

import java.time.LocalDateTime;

public record NotificationResponse(
    Integer id,
    String message,
    Boolean isRead,
    LocalDateTime createdAt
) {}
