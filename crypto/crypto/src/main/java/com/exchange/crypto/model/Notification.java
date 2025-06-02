package com.exchange.crypto.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class Notification {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "notification_channels", joinColumns = @JoinColumn(name = "notification_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "channel", nullable = false)
    private List<Channel> channel;

    @Column(columnDefinition = "jsonb", nullable = false)
    private String details;

    @Column(nullable = false)
    private boolean seen = false;

    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt = Timestamp.from(Instant.now());

<<<<<<< HEAD
=======
    public Notification() {}

>>>>>>> HeNeedSomeMilk
    public Notification(UUID id, UUID userId, NotificationType type, List<Channel> channel, String details, boolean seen, Timestamp createdAt) {
        this.id = id;
        this.userId = userId;
        this.type = type;
        this.channel = channel;
        this.details = details;
        this.seen = seen;
        this.createdAt = createdAt;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public static NotificationBuilder builder() {
        return new NotificationBuilder();
    }

    public static class NotificationBuilder {
        private UUID id;
        private UUID userId;
        private NotificationType type;
        private List<Channel> channel;
        private String details;
        private boolean seen = false;
        private Timestamp createdAt = Timestamp.from(Instant.now());

        public NotificationBuilder id(UUID id) {
            this.id = id;
            return this;
        }
        public NotificationBuilder userId(UUID userId) {
            this.userId = userId;
            return this;
        }
        public NotificationBuilder type(NotificationType type) {
            this.type = type;
            return this;
        }
        public NotificationBuilder channel(List<Channel> channel) {
            this.channel = channel;
            return this;
        }
        public NotificationBuilder details(String details) {
            this.details = details;
            return this;
        }
        public NotificationBuilder seen(boolean seen) {
            this.seen = seen;
            return this;
        }
        public NotificationBuilder createdAt(Timestamp createdAt) {
            this.createdAt = createdAt;
            return this;
        }
        public Notification build() {
            return new Notification(id, userId, type, channel, details, seen, createdAt);
        }
    }
}
