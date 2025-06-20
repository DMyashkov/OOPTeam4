package com.exchange.crypto.service;

import com.exchange.crypto.model.Channel;
import com.exchange.crypto.model.Notification;
import com.exchange.crypto.model.NotificationStatus;
import com.exchange.crypto.repository.NotificationRepository;
import com.exchange.crypto.utils.TelegramMessageFormatter;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Service
public class NotificationSendingService {

    private static final int MAX_RETRIES = 3;

    private final NotificationRepository notificationRepository;
    private final TelegramNotificationService telegramNotificationService;
    private final EmailNotificationService emailNotificationService;
    private final ObjectMapper objectMapper;

    @Scheduled(fixedDelay = 60000) // Run every 60 seconds
    public void sendPendingNotifications() {
        System.out.println("Scheduler running: Looking for pending notifications...");
        List<Notification> pendingNotifications = notificationRepository.findByStatus(NotificationStatus.PENDING);

        for (Notification notification : pendingNotifications) {
            if (notification.getRetryCount() >= MAX_RETRIES) {
                notification.setStatus(NotificationStatus.FAILED);
                notificationRepository.save(notification);
                continue;
            }

            if (notification.getChannel().contains(Channel.TELEGRAM)) {
                sendTelegramNotification(notification);
            }
            if (notification.getChannel().contains(Channel.EMAIL)) {
                sendEmailNotification(notification);
            }
        }
    }

    private void sendTelegramNotification(Notification notification) {
        try {
            System.out.println("Processing Telegram notification: " + notification.getId());
            Map<String, Object> detailsMap = objectMapper.readValue(notification.getDetails(), new TypeReference<>() {});
            String message = TelegramMessageFormatter.format(notification.getType(), detailsMap);
            telegramNotificationService.sendMessage(message);

            notification.setStatus(NotificationStatus.SENT);
            notificationRepository.save(notification);
            System.out.println("Sent Telegram notification for notification ID: " + notification.getId());

        } catch (Exception e) {
            System.err.println("Error sending Telegram notification ID " + notification.getId() + ": " + e.getMessage());
            handleFailedAttempt(notification);
        }
    }

    private void sendEmailNotification(Notification notification) {
        try {
            System.out.println("Processing Email notification: " + notification.getId());
            Map<String, Object> detailsMap = objectMapper.readValue(notification.getDetails(), new TypeReference<>() {});

            String email = (String) detailsMap.get("email");
            if (email == null || email.isBlank()) {
                throw new IllegalArgumentException("Email address is missing in notification details.");
            }
            String subject = "Crypto Exchange Notification - " + notification.getType();
            String body = "Details: " + detailsMap.toString();

            emailNotificationService.sendEmail(email, subject, body);

            notification.setStatus(NotificationStatus.SENT);
            notificationRepository.save(notification);
            System.out.println("Sent Email notification for notification ID: " + notification.getId());

        } catch (Exception e) {
            System.err.println("Error sending Email notification ID " + notification.getId() + ": " + e.getMessage());
            handleFailedAttempt(notification);
        }
    }

    private void handleFailedAttempt(Notification notification) {
        notification.setRetryCount(notification.getRetryCount() + 1);
        if (notification.getRetryCount() >= MAX_RETRIES) {
            notification.setStatus(NotificationStatus.FAILED);
            System.err.println("Notification ID " + notification.getId() + " has failed permanently.");
        }
        notificationRepository.save(notification);
    }
}