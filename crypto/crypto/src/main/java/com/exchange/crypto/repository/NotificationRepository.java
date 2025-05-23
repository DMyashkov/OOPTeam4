package com.exchange.crypto.repository;

import com.exchange.crypto.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    List<Notification> findByUserIdOrderByTimestampDesc(UUID userId);
    List<Notification> findByUserIdAndSeenFalse(UUID userId);
}