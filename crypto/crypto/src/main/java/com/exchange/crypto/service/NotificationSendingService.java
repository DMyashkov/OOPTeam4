package com.exchange.crypto.service;

import com.exchange.crypto.model.Channel;
import com.exchange.crypto.model.Notification;
import com.exchange.crypto.model.NotificationType;
import com.exchange.crypto.repository.NotificationRepository;
import com.exchange.crypto.utils.TelegramMessageFormatter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Async;

import java.util.List;
import java.util.Map;

@Service
public class NotificationSendingService {

    private final NotificationRepository notificationRepository;
    private final TelegramNotificationService telegramNotificationService;
    private final ObjectMapper objectMapper;

    public NotificationSendingService(NotificationRepository notificationRepository, TelegramNotificationService telegramNotificationService, ObjectMapper objectMapper) {
        this.notificationRepository = notificationRepository;
        this.telegramNotificationService = telegramNotificationService;
        this.objectMapper = objectMapper;
    }

    // Schedule this method to run periodically (e.g., every 60 seconds)
    @Async
    public void sendTelegramNotifications() {
        // Find unseen notifications for the Telegram channel
        List<Notification> telegramNotifications = notificationRepository.findByChannelContainingAndSeenFalse(Channel.TELEGRAM);

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
                notificationRepository.save(notification);

                System.out.println("Sent Telegram notification for notification ID: " + notification.getId());

            } catch (Exception e) {
                System.err.println("Error sending Telegram notification for notification ID " + notification.getId() + ": " + e.getMessage());
                // In a real application, you would handle retries and error logging more robustly
            }
        }
    }
} 