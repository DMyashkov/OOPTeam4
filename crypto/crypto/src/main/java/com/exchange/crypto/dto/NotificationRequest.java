package com.exchange.crypto.dto;

import java.util.List;
import java.util.UUID;

import com.exchange.crypto.model.Channel;
import com.exchange.crypto.model.NotificationType;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class NotificationRequest {

    @NotNull(message = "user_id is required")
    private UUID user_id;

    @NotNull(message = "type is required")
    private NotificationType type;

    @NotNull(message = "details are required")
    private Object details;
}