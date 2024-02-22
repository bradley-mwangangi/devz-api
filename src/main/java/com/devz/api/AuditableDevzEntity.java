package com.devz.api;

import java.time.LocalDateTime;

public interface AuditableDevzEntity {

    void setCreatedAt(LocalDateTime createdAt);
    LocalDateTime getCreatedAt();

    void setUpdatedAt(LocalDateTime updatedAt);
    LocalDateTime getUpdatedAt();
}
