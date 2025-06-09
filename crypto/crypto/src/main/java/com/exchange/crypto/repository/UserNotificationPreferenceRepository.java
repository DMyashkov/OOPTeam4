// UserNotificationPreferenceRepository.java
package com.exchange.crypto.repository;

import com.exchange.crypto.model.UserNotificationPreference;
import com.exchange.crypto.model.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserNotificationPreferenceRepository extends JpaRepository<UserNotificationPreference, UUID> {
    Optional<UserNotificationPreference> findByUserIdAndNotificationType(UUID userId, NotificationType notificationType);
}
