package com.exchange.crypto.utils;

import com.exchange.crypto.dto.EmailContent;
import com.exchange.crypto.model.NotificationType;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class EmailFormatter {

    public EmailContent format(NotificationType type, Map<String, Object> details) {
        String subject = "Crypto Exchange Notification"; // Default subject
        String body = "You have a new notification."; // Default body

        switch (type) {
            case RATE_ALERT -> {
                subject = String.format("Rate Alert: %s Price Change", details.get("crypto"));
                body = String.format("""
                    <h2>Rate Alert!</h2>
                    <p>The price of <strong>%s</strong> has dropped from %s to %s.</p>
                    <p>This represents a change of <strong>%s%%</strong>.</p>
                    """, details.get("crypto"), details.get("previous_rate"), details.get("new_rate"), details.get("change_percent"));
            }
            case TRANSACTION_SUCCESS -> {
                subject = "Your Transaction was Successful";
                body = String.format("""
                    <h2>Transaction Successful!</h2>
                    <p>Your transaction of <strong>%s %s</strong> was processed successfully.</p>
                    """, details.get("amount"), details.get("currency"));
            }
            case TRANSACTION_FAILED -> {
                subject = "Transaction Failed";
                body = String.format("""
                    <h2>Transaction Failed</h2>
                    <p>We were unable to process your transaction of <strong>%s %s</strong>.</p>
                    <p>Reason: <strong>%s</strong></p>
                    <p>Timestamp: %s</p>
                    """, details.get("amount"), details.get("currency"), details.get("reason"), details.get("timestamp"));
            }
            case SECURITY_ALERT -> {
                subject = "Security Alert on Your Account";
                body = String.format("""
                    <h2>Security Alert</h2>
                    <p>We detected a new security event: <strong>%s</strong>.</p>
                    <p>Location details: IP %s from %s at %s.</p>
                    """, details.get("event"), details.get("ip"), details.get("location"), details.get("time"));
            }
            case TWO_FACTOR_CODE -> {
                subject = "Your Two-Factor Authentication Code";
                body = String.format("""
                    <h2>2FA Code</h2>
                    <p>Your two-factor authentication code is: <strong>%s</strong></p>
                    <p>This code expires in %s seconds.</p>
                    """, details.get("code"), details.get("expires_in"));
            }
            case NEW_BLOG_POST -> {
                subject = String.format("New Blog Post: %s", details.get("title"));
                body = String.format("""
                    <h2>New Blog Post</h2>
                    <p><strong>%s</strong> just published a new article: <a href="%s">%s</a></p>
                    """, details.get("author"), details.get("url"), details.get("title"));
            }
            case SYSTEM_MESSAGE -> {
                subject = "System Message";
                body = String.format("""
                    <h2>System Message</h2>
                    <p><strong>Priority:</strong> %s</p>
                    <p>%s</p>
                    """, details.get("priority"), details.get("message"));
            }
        }

        return new EmailContent(subject, body);
    }
}
