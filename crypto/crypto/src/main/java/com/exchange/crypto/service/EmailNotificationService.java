package com.exchange.crypto.service;

import com.exchange.crypto.dto.EmailContent;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class EmailNotificationService {

    private final JavaMailSender javaMailSender;

    public void sendEmail(String to, EmailContent content) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(content.subject());
            helper.setText(content.body(), true);
            javaMailSender.send(message);
            System.out.println("Email sent successfully to " + to);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }
}