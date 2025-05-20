package com.exchange.crypto;

import com.exchange.crypto.config.TelegramProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(TelegramProperties.class)
public class CryptoApplication {
	public static void main(String[] args) {
		SpringApplication.run(CryptoApplication.class, args);
	}
}
