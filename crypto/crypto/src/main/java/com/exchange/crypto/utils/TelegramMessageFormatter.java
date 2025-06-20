package com.exchange.crypto.utils;

import com.exchange.crypto.model.NotificationType;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class TelegramMessageFormatter {

    public String format(NotificationType type, Map<String, Object> details) {

        return switch (type) {
            case RATE_ALERT -> String.format(
                    "*Rate Alert*: %s dropped from %s to %s (*%s%%*)",
                    details.get("crypto"), details.get("previous_rate"), details.get("new_rate"), details.get("change_percent")
            );
            case TRANSACTION_SUCCESS -> String.format(
                    "*Transaction Success*: %s %s %s to %s.",
                    details.get("amount"), details.get("currency"), details.get("direction"), details.get("to")
            );
            case TRANSACTION_FAILED -> String.format(
                    "*Transaction Failed*: %s %s. Reason: %s. Timestamp: %s.",
                    details.get("amount"), details.get("currency"), details.get("reason"), details.get("timestamp")
            );
            case SECURITY_ALERT -> String.format(
                    "*Security Alert*: %s from IP %s (%s) at %s.",
                    details.get("event"), details.get("ip"), details.get("location"), details.get("time")
            );
            case TWO_FACTOR_CODE -> String.format(
                    "Your 2FA code is *%s*. Expires in %s seconds.",
                    details.get("code"), details.get("expires_in")
            );
            case NEW_BLOG_POST -> String.format(
                    "*New Blog Post*: _%s_ by %s\n[Read more](%s)",
                    details.get("title"), details.get("author"), details.get("url")
            );
            case SYSTEM_MESSAGE -> String.format(
                    "*System Message* _(Priority: %s)_: %s",
                    details.get("priority"), details.get("message")
            );
        };
    }
}
