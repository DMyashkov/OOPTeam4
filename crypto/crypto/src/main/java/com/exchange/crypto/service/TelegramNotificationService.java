package com.exchange.crypto.service;

import com.exchange.crypto.config.TelegramProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;

@Service
@Slf4j
public class TelegramNotificationService {

    private final TelegramProperties properties;
    private final RestTemplate restTemplate;

    public TelegramNotificationService(TelegramProperties properties) {
        this.properties = properties;
        this.restTemplate = new RestTemplate();
    }

    public void sendMessage(String message) {
        String url = String.format(
                "https://api.telegram.org/bot%s/sendMessage?chat_id=%s&text=%s",
                properties.getBotToken(),
                properties.getChatId(),
                UriUtils.encode(message, StandardCharsets.UTF_8)
        );

        try {
            restTemplate.getForObject(url, String.class);
        } catch (Exception e) {
            log.error("Failed to send Telegram message: {}", e.getMessage());
        }
    }
}
