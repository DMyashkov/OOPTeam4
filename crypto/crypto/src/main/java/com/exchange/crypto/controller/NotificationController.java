package com.exchange.crypto.controller;

import com.exchange.crypto.dto.NotificationRequest;
import com.exchange.crypto.model.Channel;
import com.exchange.crypto.model.Notification;
import com.exchange.crypto.model.NotificationType;
import com.exchange.crypto.service.InAppNotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final InAppNotificationService notificationService;

    @PostMapping
    public ResponseEntity<Notification> createNotification(@Valid @RequestBody NotificationRequest request) {
        Notification notification = notificationService.createNotification(request);
        return ResponseEntity.ok(notification);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<List<Notification>> getUserNotifications(
            @PathVariable("id") UUID userId,
            @RequestParam(name = "channel", required = false) Channel channel,
            @RequestParam(name = "type", required = false) NotificationType type
    ) {
        List<Notification> notifications;

        if (channel != null && type != null) {
            notifications = notificationService.getNotificationsForUserByChannelAndType(userId, channel, type);
        } else if (channel != null) {
            notifications = notificationService.getNotificationsForUserByChannel(userId, channel);
        } else if (type != null) {
            notifications = notificationService.getNotificationsForUserByType(userId, type);
        } else {
            notifications = notificationService.getNotificationsForUser(userId);
        }
        // Example requests:
        // - "/notifications/user/user123"
        // - "/notifications/user/user123?channel=EMAIL"
        // - "/notifications/user/user123?type=TRANSACTION_SUCCESS"
        // - "/notifications/user/user123?channel=EMAIL&type=TRANSACTION_SUCCESS"

        return ResponseEntity.ok(notifications);
    }


    @PatchMapping("/{id}/seen")
    public ResponseEntity<Void> markAsSeen(@PathVariable("id") UUID notificationId) {
        notificationService.markAsSeen(notificationId);
        return ResponseEntity.ok().build();
    }
}