-- Delete duplicate preferences, keeping only the most recent one for each user and notification type
DELETE FROM user_notification_preferences a
USING user_notification_preferences b
WHERE a.id < b.id
AND a.user_id = b.user_id
AND a.notification_type = b.notification_type; 