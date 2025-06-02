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

    public UUID getUser_id() {
        return user_id;
    }

    public NotificationType getType() {
        return type;
    }

    public List<Channel> getChannel() {
        return channel;
    }

    public Object getDetails() {
        return details;
    }
}