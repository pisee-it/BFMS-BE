package com.bfms.bfms_backend.repository.projection;

import java.time.LocalDateTime;

public interface NotificationProjection {
    Integer getId();
    String getMessage();
    Boolean getIsRead();
    LocalDateTime getCreatedAt();
}
