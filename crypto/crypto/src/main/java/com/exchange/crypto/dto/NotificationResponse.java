package com.exchange.crypto.dto;

import com.exchange.crypto.model.NotificationType;
import lombok.Builder;
import lombok.Data;
import java.sql.Timestamp;
import java.util.UUID;

@Data
@Builder
public class NotificationResponse {
    private UUID id;
    private NotificationType type;
    private String message; // The formatted message
    private boolean seen;
    private Timestamp createdAt;
}