package com.exchange.crypto.utils;

import com.exchange.crypto.model.NotificationType;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class InAppNotificationFormatter {

    @SuppressWarnings("unchecked")
    public String format(NotificationType type, Object detailsObj) {
        Map<String, Object> details = (Map<String, Object>) detailsObj;

        return switch (type) {
            case RATE_ALERT -> String.format(
                    "%s price changed by %s%%. The new rate is %s.",
                    details.get("crypto"), details.get("change_percent"), details.get("new_rate")
            );
            case TRANSACTION_SUCCESS -> String.format(
                    "Your transaction of %s %s was successful.",
                    details.get("amount"), details.get("currency")
            );
            case TRANSACTION_FAILED -> String.format(
                    "Transaction of %s %s failed. Reason: %s.",
                    details.get("amount"), details.get("currency"), details.get("reason")
            );
            case SECURITY_ALERT -> String.format(
                    "Security Alert: A new %s was detected on your account.",
                    details.get("event")
            );
            case TWO_FACTOR_CODE -> String.format(
                    "Your two-factor authentication code is %s.",
                    details.get("code")
            );
            case NEW_BLOG_POST -> String.format(
                    "New blog post: %s by %s.",
                    details.get("title"), details.get("author")
            );
            case SYSTEM_MESSAGE -> String.format(
                    "System Message (%s priority): %s",
                    details.get("priority"), details.get("message")
            );
        };
    }
}
