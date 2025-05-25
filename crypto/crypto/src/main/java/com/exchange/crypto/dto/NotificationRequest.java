package com.exchange.crypto.dto;

import com.exchange.crypto.model.Channel;
import com.exchange.crypto.model.NotificationType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationRequest {

    @NotNull
    private UUID user_id;

    @NotNull
    private NotificationType type;

    @NotEmpty
    private List<Channel> channel;

    @NotNull
    private Object details; // Приема всякакъв JSON обект (ще го сериализираме като String)
}