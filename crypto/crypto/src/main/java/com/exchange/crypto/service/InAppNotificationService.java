package com.exchange.crypto.service;

import com.exchange.crypto.dto.NotificationRequest;
import com.exchange.crypto.model.Channel;
import com.exchange.crypto.model.Notification;
import com.exchange.crypto.model.NotificationType;
import com.exchange.crypto.repository.NotificationRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class InAppNotificationService {

    private final NotificationRepository notificationRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final NotificationSendingService notificationSendingService;

    public InAppNotificationService(NotificationRepository notificationRepository, NotificationSendingService notificationSendingService) {
        this.notificationRepository = notificationRepository;
        this.notificationSendingService = notificationSendingService;
    }

    public Notification createNotification(NotificationRequest request) {
        try {
            String jsonDetails = objectMapper.writeValueAsString(request.getDetails());

            Notification notification = Notification.builder()
                    .userId(request.getUser_id())
                    .type(request.getType())
                    .channel(request.getChannel())
                    .details(jsonDetails)
                    .seen(false)
                    .build();

            Notification savedNotification = notificationRepository.save(notification);

            if (savedNotification.getChannel().contains(Channel.TELEGRAM)) {
                notificationSendingService.sendTelegramNotifications();
            }

            return savedNotification;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid JSON in details");
        }
    }

    public Page<Notification> getNotificationsForUser(UUID userId, Pageable pageable, Channel channel, NotificationType type) {
        if (channel != null && type != null) {
            // Need to add this method to repository
            return notificationRepository.findByUserIdAndChannelContainingAndType(userId, channel, type, pageable);
        } else if (channel != null) {
            // Need to add this method to repository
            return notificationRepository.findByUserIdAndChannelContaining(userId, channel, pageable);
        } else if (type != null) {
            // Need to add this method to repository
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