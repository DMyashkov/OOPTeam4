package com.exchange.crypto.service;

import com.exchange.crypto.model.Channel;
import com.exchange.crypto.model.Notification;
import com.exchange.crypto.repository.NotificationRepository;
import com.exchange.crypto.utils.TelegramMessageFormatter;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Async;
import com.exchange.crypto.model.NotificationStatus;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Service
public class NotificationSendingService {

    private final NotificationRepository notificationRepository;
    private final TelegramNotificationService telegramNotificationService;
    private final ObjectMapper objectMapper;

    // Schedule this method to run periodically (e.g., every 60 seconds)
    @Async
    public void sendTelegramNotifications() {
        // Find unseen notifications for the Telegram channel
        List<Notification> telegramNotifications = notificationRepository
                .findByStatusAndChannelContaining(NotificationStatus.PENDING, Channel.TELEGRAM);

        for (Notification notification : telegramNotifications) {
            try {
                // Log the raw details string
                System.out.println("Raw notification details: " + notification.getDetails());

                // Parse the details JSON string into a Map
                Map<String, Object> detailsMap = objectMapper.readValue(notification.getDetails(), Map.class);

                // Format the message using the details map
                String message = TelegramMessageFormatter.format(notification.getType(), detailsMap);

                // Send the message via Telegram
                telegramNotificationService.sendMessage(message);

                // Mark the notification as seen
                notification.setSeen(true);
                notification.setStatus(NotificationStatus.SENT);
                notificationRepository.save(notification);

                System.out.println("Sent Telegram notification for notification ID: " + notification.getId());

            } catch (Exception e) {
                notification.setRetryCount(notification.getRetryCount() + 1);
                if (notification.getRetryCount() >= 3) {
                    notification.setStatus(NotificationStatus.FAILED);
                }
                notificationRepository.save(notification);
            }
        }
    }

    @Scheduled(fixedDelay = 60000)
    public void retryFailedNotifications() {
        List<Notification> failedTelegram = notificationRepository
                .findByStatusAndChannelContaining(NotificationStatus.FAILED, Channel.TELEGRAM);

        for (Notification notif : failedTelegram) {
            try {
                Map<String, Object> detailsMap = objectMapper.readValue(notif.getDetails(), Map.class);
                String message = TelegramMessageFormatter.format(notif.getType(), detailsMap);
                telegramNotificationService.sendMessage(message);

                notif.setSeen(true);
                notif.setStatus(NotificationStatus.SENT);
            } catch (Exception e) {
                notif.setRetryCount(notif.getRetryCount() + 1);
                System.err.println("Retry failed: " + e.getMessage());
            }
            notificationRepository.save(notif);
        }
    }
} 