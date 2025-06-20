package com.exchange.crypto.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.exchange.crypto.dto.NotificationResponse;
import com.exchange.crypto.utils.InAppNotificationFormatter;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.exchange.crypto.dto.NotificationRequest;
import com.exchange.crypto.model.Channel;
import com.exchange.crypto.model.Notification;
import com.exchange.crypto.model.NotificationStatus;
import com.exchange.crypto.model.NotificationType;
import com.exchange.crypto.repository.NotificationRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class InAppNotificationService {

    private final NotificationRepository notificationRepository;
    private final UserNotificationPreferenceService preferenceService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final InAppNotificationFormatter inAppFormatter;

    public Notification createNotification(NotificationRequest request) {
        try {
            System.out.println("Received notification request: " + request);
            String jsonDetails = objectMapper.writeValueAsString(request.getDetails());
            System.out.println("Serialized details: " + jsonDetails);

            List<Channel> allowedChannels = preferenceService.getAllowedChannelsForUser(request.getUser_id(), request.getType());
            System.out.println("User's allowed channels for this type: " + allowedChannels);

            if (allowedChannels.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User has not subscribed to this notification type or has no channels enabled for it.");
            }

            Notification notification = Notification.builder()
                    .userId(request.getUser_id())
                    .type(request.getType())
                    .channel(allowedChannels)
                    .details(jsonDetails)
                    .seen(false)
                    .status(NotificationStatus.PENDING)
                    .retryCount(0)
                    .build();

            System.out.println("Created notification object: " + notification);
            Notification savedNotification = notificationRepository.save(notification);
            System.out.println("Saved notification: " + savedNotification);

            return savedNotification;
        } catch (Exception e) {
            System.err.println("Error creating notification: " + e.getMessage());
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error creating notification: " + e.getMessage());
        }
    }

    public Page<Notification> getNotificationsForUser(UUID userId, Pageable pageable, Channel channel, NotificationType type) {
        if (channel != null && type != null) {
            return notificationRepository.findByUserIdAndChannelContainingAndType(userId, channel, type, pageable);
        } else if (channel != null) {
            return notificationRepository.findByUserIdAndChannelContaining(userId, channel, pageable);
        } else if (type != null) {
            return notificationRepository.findByUserIdAndType(userId, type, pageable);
        } else {
            return notificationRepository.findByUserId(userId, pageable);
        }
    }

    public void markAsSeen(UUID notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification not found"));

        notification.setSeen(true);
        notificationRepository.save(notification);
    }

    public Page<NotificationResponse> getFormattedNotificationsForUser(UUID userId, Pageable pageable, NotificationType type) {
        Page<Notification> notifications;
        if (type != null) {
            notifications = notificationRepository.findByUserIdAndType(userId, type, pageable);
        } else {
            notifications = notificationRepository.findByUserId(userId, pageable);
        }
        return notifications.map(this::toResponse);
    }

    private NotificationResponse toResponse(Notification notification) {
        try {
            Map<String, Object> details = objectMapper.readValue(notification.getDetails(), new TypeReference<>() {});
            String message = inAppFormatter.format(notification.getType(), details); // Use formatter
            return NotificationResponse.builder()
                    .id(notification.getId())
                    .type(notification.getType())
                    .message(message)
                    .seen(notification.isSeen())
                    .createdAt(notification.getCreatedAt())
                    .build();
        } catch (Exception e) {
            return NotificationResponse.builder()
                    .id(notification.getId())
                    .message("Could not format notification.")
                    .build();
        }
    }
}