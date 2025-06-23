package com.exchange.crypto.dto;

import java.util.Map;

public class DirectEmailNotificationRequest {
    private String email;
    private String type;
    private Map<String, Object> details;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Map<String, Object> getDetails() { return details; }
    public void setDetails(Map<String, Object> details) { this.details = details; }
} 