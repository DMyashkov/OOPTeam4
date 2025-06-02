package com.exchange.crypto.service;

import com.exchange.crypto.dto.NotificationRequest;
import com.exchange.crypto.model.Notification;
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

    public InAppNotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
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

            return notificationRepository.save(notification);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid JSON in details");
        }
    }

    public Page<Notification> getNotificationsForUser(UUID userId, Pageable pageable) {
        return notificationRepository.findByUserId(userId, pageable);
    }

    public void markAsSeen(UUID notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification not found"));

        notification.setSeen(true);
        notificationRepository.save(notification);
    }
}