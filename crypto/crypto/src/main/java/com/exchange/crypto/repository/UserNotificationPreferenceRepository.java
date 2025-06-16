package com.exchange.crypto.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.exchange.crypto.model.NotificationType;
import com.exchange.crypto.model.UserNotificationPreference;

public interface UserNotificationPreferenceRepository extends JpaRepository<UserNotificationPreference, UUID> {
    
    @Query("SELECT p FROM UserNotificationPreference p WHERE p.userId = :userId AND p.notificationType = :type ORDER BY p.id DESC")
    List<UserNotificationPreference> findAllByUserIdAndNotificationType(@Param("userId") UUID userId, @Param("type") NotificationType type);

    default Optional<UserNotificationPreference> findByUserIdAndNotificationType(UUID userId, NotificationType type) {
        List<UserNotificationPreference> preferences = findAllByUserIdAndNotificationType(userId, type);
        return preferences.isEmpty() ? Optional.empty() : Optional.of(preferences.get(0));
    }
}
