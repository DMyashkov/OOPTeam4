package com.exchange.crypto.service;

import java.util.List;
import java.util.Map;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.exchange.crypto.model.Channel;
import com.exchange.crypto.model.Notification;
import com.exchange.crypto.model.NotificationStatus;
import com.exchange.crypto.repository.NotificationRepository;
import com.exchange.crypto.utils.TelegramMessageFormatter;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class NotificationSendingService {

    private final NotificationRepository notificationRepository;
    private final TelegramNotificationService telegramNotificationService;
    private final EmailNotificationService emailNotificationService;
    private final ObjectMapper objectMapper;

    // Schedule this method to run periodically (e.g., every 60 seconds)
    @Async
    public void sendTelegramNotifications() {
        // Find all pending notifications for the Telegram channel
        List<Notification> telegramNotifications = notificationRepository
                .findByStatusAndChannelContaining(NotificationStatus.PENDING, Channel.TELEGRAM);

        for (Notification notification : telegramNotifications) {
            try {
                System.out.println("Processing Telegram notification: " + notification.getId());
                Map<String, Object> detailsMap = objectMapper.readValue(notification.getDetails(), Map.class);
                String message = TelegramMessageFormatter.format(notification.getType(), detailsMap);
                telegramNotificationService.sendMessage(message);

                notification.setSeen(true);
                notification.setStatus(NotificationStatus.SENT);
                notificationRepository.save(notification);

                System.out.println("Sent Telegram notification for notification ID: " + notification.getId());

            } catch (Exception e) {
                System.err.println("Error sending Telegram notification: " + e.getMessage());
                notification.setRetryCount(notification.getRetryCount() + 1);
                if (notification.getRetryCount() >= 3) {
                    notification.setStatus(NotificationStatus.FAILED);
                }
                notificationRepository.save(notification);
            }
        }
    }

    @Async
    public void sendEmailNotifications() {
        List<Notification> emailNotifications = notificationRepository
                .findByStatusAndChannelContaining(NotificationStatus.PENDING, Channel.EMAIL);

        for (Notification notification : emailNotifications) {
            try {
                Map<String, Object> detailsMap = objectMapper.readValue(notification.getDetails(), Map.class);

                String email = (String) detailsMap.get("email");
                String subject = "Crypto Exchange Notification - " + notification.getType();
                String body = detailsMap.toString(); // Може да го направиш по-красив с друг форматер

                emailNotificationService.sendEmail(email, subject, body);

                notification.setSeen(true);
                notification.setStatus(NotificationStatus.SENT);
                notificationRepository.save(notification);

                System.out.println("Sent Email notification for notification ID: " + notification.getId());

            } catch (Exception e) {
                notification.setRetryCount(notification.getRetryCount() + 1);
                if (notification.getRetryCount() >= 3) {
                    notification.setStatus(NotificationStatus.FAILED);
                }
                notificationRepository.save(notification);

                System.err.println("Email sending failed: " + e.getMessage());
            }
        }
    }

    @Scheduled(fixedDelay = 60000)
    public void retryFailedNotifications() {
        // Telegram
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
                System.err.println("Telegram retry failed: " + e.getMessage());
            }
            notificationRepository.save(notif);
        }

        // Email
        List<Notification> failedEmail = notificationRepository
                .findByStatusAndChannelContaining(NotificationStatus.FAILED, Channel.EMAIL);

        for (Notification notif : failedEmail) {
            try {
                Map<String, Object> detailsMap = objectMapper.readValue(notif.getDetails(), Map.class);
                String email = (String) detailsMap.get("email");
                String subject = "Crypto Exchange Notification - " + notif.getType();
                String body = detailsMap.toString();

                emailNotificationService.sendEmail(email, subject, body);

                notif.setSeen(true);
                notif.setStatus(NotificationStatus.SENT);
            } catch (Exception e) {
                notif.setRetryCount(notif.getRetryCount() + 1);
                System.err.println("Email retry failed: " + e.getMessage());
            }
            notificationRepository.save(notif);
        }
    }
} 