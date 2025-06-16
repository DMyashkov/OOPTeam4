-- Create notifications table if it doesn't exist
CREATE TABLE IF NOT EXISTS notifications (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    type VARCHAR(255) NOT NULL,
    details JSONB NOT NULL,
    seen BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    retry_count INTEGER NOT NULL DEFAULT 0,
    status VARCHAR(255) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'SENT', 'FAILED'))
);

-- Create notification_channels table if it doesn't exist
CREATE TABLE IF NOT EXISTS notification_channels (
    notification_id UUID NOT NULL,
    channel VARCHAR(255) NOT NULL,
    FOREIGN KEY (notification_id) REFERENCES notifications(id)
);

-- Create user_notification_preferences table if it doesn't exist
CREATE TABLE IF NOT EXISTS user_notification_preferences (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    notification_type VARCHAR(255) NOT NULL,
    UNIQUE(user_id, notification_type)
);

-- Create user_preference_channels table if it doesn't exist
CREATE TABLE IF NOT EXISTS user_preference_channels (
    preference_id UUID NOT NULL,
    channel VARCHAR(255) NOT NULL,
    FOREIGN KEY (preference_id) REFERENCES user_notification_preferences(id)
); 