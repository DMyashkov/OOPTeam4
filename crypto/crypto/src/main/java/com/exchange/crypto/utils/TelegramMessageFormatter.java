package com.exchange.crypto.utils;

import com.exchange.crypto.model.NotificationType;

import java.util.Map;

public class TelegramMessageFormatter {

    @SuppressWarnings("unchecked")
    public static String format(NotificationType type, Object detailsObj) {
        Map<String, Object> details = (Map<String, Object>) detailsObj;

        return switch (type) {
            case RATE_ALERT -> details.get("crypto") + " dropped from " + details.get("previous_rate") + " to " +
                    details.get("new_rate") + " (" + details.get("change_percent") + "%)";
            case TRANSACTION_SUCCESS -> "Transaction successful: " + details.get("amount") + " " +
                    details.get("currency") + " " + details.get("direction") + " to " + details.get("to");
            case TRANSACTION_FAILED -> "Transaction failed: " + details.get("reason") + " at " + details.get("timestamp");
            case SECURITY_ALERT -> "Security alert: " + details.get("event") + " from IP " + details.get("ip") +
                    " (" + details.get("location") + ") at " + details.get("time");
            case NEW_BLOG_POST -> "New blog post: '" + details.get("title") + "' by " + details.get("author") +
                    " - " + details.get("url");
            case SYSTEM_MESSAGE -> "System message: " + details.get("message") +
                    " (Priority: " + details.get("priority") + ")";
            case TWO_FACTOR_CODE -> "Your 2FA code is " + details.get("code") +
                    ". Expires in " + details.get("expires_in") + " seconds.";
        };
    }
}
