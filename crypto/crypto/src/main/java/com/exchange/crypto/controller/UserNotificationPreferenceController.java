package com.exchange.crypto.controller;

import com.exchange.crypto.model.UserNotificationPreference;
import com.exchange.crypto.service.UserNotificationPreferenceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/preferences")
public class UserNotificationPreferenceController {

    private final UserNotificationPreferenceService preferenceService;

    public UserNotificationPreferenceController(UserNotificationPreferenceService preferenceService) {
        this.preferenceService = preferenceService;
    }

    @PostMapping
    public ResponseEntity<Void> setPreference(@RequestBody UserNotificationPreference preference) {
        preferenceService.savePreference(preference);
        return ResponseEntity.ok().build();
    }
}
