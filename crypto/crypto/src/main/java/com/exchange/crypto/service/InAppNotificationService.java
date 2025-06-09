package com.exchange.crypto.service;

import com.exchange.crypto.dto.NotificationRequest;
import com.exchange.crypto.model.Channel;
import com.exchange.crypto.model.Notification;
import com.exchange.crypto.model.NotificationType;
import com.exchange.crypto.repository.NotificationRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Service
public class InAppNotificationService {

    private final NotificationRepository notificationRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final NotificationSendingService notificationSendingService;
    private final UserNotificationPreferenceService preferenceService;

    public Notification createNotification(NotificationRequest request) {
        try {
            String jsonDetails = objectMapper.writeValueAsString(request.getDetails());

            List<Channel> allowedChannels = preferenceService.getAllowedChannelsForUser(request.getUser_id(), request.getType());

            if (allowedChannels.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No channels enabled for this user and notification type");
            }

            List<Channel> filteredChannels = request.getChannel().stream()
                    .filter(allowedChannels::contains)
                    .toList();

            if (filteredChannels.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Requested channels not allowed for this user");
            }

            Notification notification = Notification.builder()
                    .userId(request.getUser_id())
                    .type(request.getType())
                    .channel(filteredChannels)
                    .details(jsonDetails)
                    .seen(false)
                    .build();

            Notification savedNotification = notificationRepository.save(notification);

            if (filteredChannels.contains(Channel.TELEGRAM)) {
                notificationSendingService.sendTelegramNotifications();
            }

            return savedNotification;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid JSON in details");
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

    // The following methods are no longer needed as filtering is integrated into getNotificationsForUser
    // public List<Notification> getNotificationsForUserByChannel(UUID userId, Channel channel) {
    //     return notificationRepository.findByUserIdAndChannelContaining(userId, channel);
    // }

    // public List<Notification> getNotificationsForUserByType(UUID userId, NotificationType type) {
    //     return notificationRepository.findByUserIdAndType(userId, type);
    // }

    // public List<Notification> getNotificationsForUserByChannelAndType(UUID userId, Channel channel, NotificationType type) {
    //     return notificationRepository.findByUserIdAndChannelContainingAndType(userId, channel, type);
    // }
}