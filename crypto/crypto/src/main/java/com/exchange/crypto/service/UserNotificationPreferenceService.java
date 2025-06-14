package com.exchange.crypto.service;

import com.exchange.crypto.model.Channel;
import com.exchange.crypto.model.NotificationType;
import com.exchange.crypto.model.UserNotificationPreference;
import com.exchange.crypto.repository.UserNotificationPreferenceRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class UserNotificationPreferenceService {

    private final UserNotificationPreferenceRepository preferenceRepository;

    public UserNotificationPreferenceService(UserNotificationPreferenceRepository preferenceRepository) {
        this.preferenceRepository = preferenceRepository;
    }

    public List<Channel> getAllowedChannelsForUser(UUID userId, NotificationType type) {
        return preferenceRepository.findByUserIdAndNotificationType(userId, type)
                .map(UserNotificationPreference::getChannels)
                .orElse(Collections.emptyList());
    }

    public void savePreference(UserNotificationPreference preference) {
        preferenceRepository.save(preference);
    }
}
