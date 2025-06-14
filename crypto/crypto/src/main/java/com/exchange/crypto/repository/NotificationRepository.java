package com.exchange.crypto.repository;

import com.exchange.crypto.model.Channel;
import com.exchange.crypto.model.Notification;
import com.exchange.crypto.model.NotificationStatus;
import com.exchange.crypto.model.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    List<Notification> findByUserId(UUID userId);

    List<Notification> findByUserIdAndSeenFalse(UUID userId);

    boolean existsByUserIdAndSeenFalse(UUID userId);

    List<Notification> findByUserIdAndType(UUID userId, NotificationType type);

    List<Notification> findByUserIdAndChannelContaining(UUID userId, Channel channel);

    List<Notification> findByUserIdAndChannelContainingAndType(UUID userId, Channel channel, NotificationType type);

    List<Notification> findByChannelContainingAndSeenFalse(Channel channel);

    List<Notification> findByStatus(NotificationStatus status);

    List<Notification> findByStatusAndChannelContaining(NotificationStatus status, Channel channel);

    Page<Notification> findByUserId(UUID userId, Pageable pageable);

    Page<Notification> findByUserIdAndType(UUID userId, NotificationType type, Pageable pageable);

    Page<Notification> findByUserIdAndChannelContaining(UUID userId, Channel channel, Pageable pageable);

    Page<Notification> findByUserIdAndChannelContainingAndType(UUID userId, Channel channel, NotificationType type, Pageable pageable);
}
