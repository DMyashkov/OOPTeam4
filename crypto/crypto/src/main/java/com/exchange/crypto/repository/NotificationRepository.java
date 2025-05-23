package com.exchange.crypto.repository;

import com.exchange.crypto.model.Notification;
import com.exchange.crypto.model.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    List<Notification> findByUserId(UUID userId);

    List<Notification> findByUserIdAndSeenFalse(UUID userId);

    List<Notification> findByUserIdAndType(UUID userId, NotificationType type);

    boolean existsByUserIdAndSeenFalse(UUID userId);
}
