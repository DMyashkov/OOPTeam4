package com.exchange.crypto.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import com.exchange.crypto.model.NotificationType;
import com.exchange.crypto.model.UserNotificationPreference;

public interface UserNotificationPreferenceRepository extends JpaRepository<UserNotificationPreference, UUID> {
    Optional<UserNotificationPreference> findFirstByUserIdAndNotificationTypeOrderByIdDesc(UUID userId, NotificationType type);
}