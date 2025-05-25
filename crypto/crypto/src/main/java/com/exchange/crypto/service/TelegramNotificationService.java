package com.exchange.crypto.service;

import com.exchange.crypto.config.TelegramProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class TelegramNotificationService {

    private final TelegramProperties telegramProperties;
    private final RestTemplate restTemplate;

    public TelegramNotificationService(TelegramProperties telegramProperties) {
        this.telegramProperties = telegramProperties;
        this.restTemplate = new RestTemplate();
    }

    public void sendMessage(String message) {
        String url = UriComponentsBuilder
                .fromHttpUrl("https://api.telegram.org/bot" + telegramProperties.getToken() + "/sendMessage")
                .queryParam("chat_id", telegramProperties.getChatId())
                .queryParam("text", message)
                .toUriString();

        restTemplate.getForObject(url, String.class);
    }
}
