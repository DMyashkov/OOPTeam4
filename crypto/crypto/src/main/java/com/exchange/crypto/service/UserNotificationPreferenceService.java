package com.exchange.crypto.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.exchange.crypto.model.Channel;
import com.exchange.crypto.model.NotificationType;
import com.exchange.crypto.model.UserNotificationPreference;
import com.exchange.crypto.repository.UserNotificationPreferenceRepository;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class UserNotificationPreferenceService {

    private final UserNotificationPreferenceRepository preferenceRepository;

    public List<Channel> getAllowedChannelsForUser(UUID userId, NotificationType type) {
        System.out.println("Getting allowed channels for user: " + userId + ", type: " + type);
        List<Channel> channels = preferenceRepository.findFirstByUserIdAndNotificationTypeOrderByIdDesc(userId, type)
                .map(UserNotificationPreference::getChannels)
                .orElse(Collections.emptyList());
        System.out.println("Found channels: " + channels);
        return channels;
    }

    @Transactional
    public void savePreference(UserNotificationPreference preference) {
        System.out.println("Saving preference: " + preference);

        Optional<UserNotificationPreference> existingOpt = preferenceRepository
                .findFirstByUserIdAndNotificationTypeOrderByIdDesc(preference.getUserId(), preference.getNotificationType());

        if (existingOpt.isPresent()) {
            UserNotificationPreference existingPreference = existingOpt.get();
            existingPreference.setChannels(preference.getChannels());
            preferenceRepository.save(existingPreference);
            System.out.println("Updated existing preference for user " + preference.getUserId());
        } else {
            preferenceRepository.save(preference);
            System.out.println("Saved new preference for user " + preference.getUserId());
        }
    }
}
