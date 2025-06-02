package com.exchange.crypto.controller;

import com.exchange.crypto.dto.NotificationRequest;
import com.exchange.crypto.model.Channel;
import com.exchange.crypto.model.Notification;
import com.exchange.crypto.model.NotificationType;
import com.exchange.crypto.service.InAppNotificationService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final InAppNotificationService notificationService;

    public NotificationController(InAppNotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping
    public ResponseEntity<Notification> createNotification(@Valid @RequestBody NotificationRequest request) {
        Notification notification = notificationService.createNotification(request);
        return ResponseEntity.ok(notification);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<Page<Notification>> getUserNotifications(
            @PathVariable("id") UUID userId,
            Pageable pageable,
            @RequestParam(name = "channel", required = false) Channel channel,
            @RequestParam(name = "type", required = false) NotificationType type
    ) {
        Page<Notification> notifications = notificationService.getNotificationsForUser(userId, pageable, channel, type);
        return ResponseEntity.ok(notifications);
    }

    @PatchMapping("/{id}/seen")
    public ResponseEntity<Void> markAsSeen(@PathVariable("id") UUID notificationId) {
        notificationService.markAsSeen(notificationId);
        return ResponseEntity.ok().build();
    }
}