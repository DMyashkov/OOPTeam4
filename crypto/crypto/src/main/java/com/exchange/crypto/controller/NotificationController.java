package com.exchange.crypto.controller;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.exchange.crypto.dto.DirectEmailNotificationRequest;
import com.exchange.crypto.dto.EmailContent;
import com.exchange.crypto.dto.NotificationRequest;
import com.exchange.crypto.dto.NotificationResponse;
import com.exchange.crypto.model.Channel;
import com.exchange.crypto.model.Notification;
import com.exchange.crypto.model.NotificationType;
import com.exchange.crypto.service.EmailNotificationService;
import com.exchange.crypto.service.InAppNotificationService;
import com.exchange.crypto.utils.EmailFormatter;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final InAppNotificationService notificationService;
    private final EmailNotificationService emailNotificationService;
    private final EmailFormatter emailFormatter;

    @PostMapping
    public ResponseEntity<Notification> createNotification(@Valid @RequestBody NotificationRequest request) {
        Notification notification = notificationService.createNotification(request);
        return ResponseEntity.ok(notification);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<Page<Notification>> getUserNotifications(
            @PathVariable("id") UUID userId,
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(name = "channel", required = false) Channel channel,
            @RequestParam(name = "type", required = false) NotificationType type
    ) {
        Page<Notification> notifications = notificationService.getNotificationsForUser(userId, pageable, channel, type);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/user/{id}/in-app")
    public ResponseEntity<Page<NotificationResponse>> getInAppUserNotifications(
            @PathVariable("id") UUID userId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(name = "type", required = false) NotificationType type // Add this parameter
    ) {
        Page<NotificationResponse> responsePage = notificationService.getFormattedNotificationsForUser(userId, pageable, type);
        return ResponseEntity.ok(responsePage);
    }

    @PatchMapping("/{id}/seen")
    public ResponseEntity<Void> markAsSeen(@PathVariable("id") UUID notificationId) {
        notificationService.markAsSeen(notificationId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/direct-email")
    public ResponseEntity<String> sendDirectEmailNotification(@RequestBody DirectEmailNotificationRequest request) {
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            return ResponseEntity.badRequest().body("Email is required");
        }
        if (request.getType() == null || request.getType().isBlank()) {
            return ResponseEntity.badRequest().body("Type is required");
        }
        NotificationType type;
        try {
            type = NotificationType.valueOf(request.getType());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid notification type");
        }
        EmailContent content = emailFormatter.format(type, request.getDetails());
        emailNotificationService.sendEmail(request.getEmail(), content);
        return ResponseEntity.ok("Email sent to " + request.getEmail());
    }
}