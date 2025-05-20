## **QA Documentation – Notification Microservice**

### 1. Introduction
The Notification microservice is a component of the centralized crypto exchange system. Its purpose is to inform users about various events through multiple communication channels: email, in-app notifications, and Telegram. It operates on a REST API model, receiving requests from other microservices and delivering the appropriate messages based on user preferences.
The main goal of this QA documentation is to thoroughly describe:
- The expected behavior of the microservice
- Data structure and API interfaces
- Retry mechanisms for failed delivery
- Validation and testing methodology
This document serves as a foundation for validating the developed functionality and ensuring the stability and correctness of the notification microservice.

### 2. Microservice Functionalities
- **Supported Notification Channels:**
    - Email
    - In-app (visible within the application)
    - Telegram
- **Notification Types:**
    1. `RATE_ALERT` – significant cryptocurrency price movement
    2. `TRANSACTION_SUCCESS` – successful send/receive transaction
    3. `TRANSACTION_FAILED` – failed transaction
    4. `SECURITY_ALERT` – login from an unknown device, password change
    5. `NEW_BLOG_POST` – new post from a subscribed blogger
    6. `SYSTEM_MESSAGE` – system messages (downtime, updates)
    7. `TWO_FACTOR_CODE` – 2FA verification code
- **Personalized Marketing Emails** – with custom subject, description, images, and other content
- **Retry Mechanism** – on failed delivery, messages are saved in a queue and retried later
- **User Settings Management** – users define which types of notifications they want to receive and through which channels

### 3. API Endpoints and Logic Mapping
#### `POST /notifications`
- **Description**: Receives a new request to send a notification
- **Function**: `createNotification()`
- **Input:**
```json
{
  "user_id": "1234",
  "type": "RATE_ALERT",
  "channel": ["email", "in_app"],
  "details": {
    "crypto": "BTC",
    "change": "-8.4%"
  }
}
```
- **Responses:**
    - 200 OK – successfully recorded
    - 400 Bad Request – invalid parameters (e.g., empty field, invalid JSON, unsupported type or channel)
    - 404 Not Found – user does not exist
    - 500 Internal Server Error – failed delivery (SMTP/Telegram error, etc.)
- **Edge Cases:**
    - Empty `details` field
    - User is deactivated
    - Nonexistent channel selected (e.g., "fax")
    - Mismatch between notification `type` and provided `details`
#### `GET /notifications/user/{id}`
- **Description**: Returns in-app notifications for a specific user
- **Function**: `getNotificationsForUser()`
- **Parameters**: User ID
- **Responses:**
    - 200 OK + list of notifications
    - 404 Not Found – user does not exist
#### `PATCH /notifications/{id}/seen`
- **Description**: Marks a notification as seen
- **Function**: `markAsSeen()`
- **Parameters**: Notification ID
- **Responses:**
    - 200 OK – successfully marked
    - 404 Not Found – invalid ID
#### `POST /marketing/send`
- **Description**: Sends a marketing email to a list of users
- **Function**: `sendMarketingEmail()`
- **Input:**
```json
{
  "subject": "New Features!",
  "body": "Check out our new crypto tools",
  "image_url": "https://...",
  "recipients": ["user1@site.com", "user2@site.com"]
}
```
- **Responses:**
    - 200 OK – successfully sent
    - 400 Bad Request – missing field
    - 500 Internal Server Error – SMTP connection error
#### `POST /retry/failed`
- **Description**: Triggers the retry mechanism for failed notifications
- **Function**: `retryFailed()`
- **Responses:**
    - 200 OK – retry process successfully triggered
    - 500 Internal Server Error – queue processing error

### 4. Notification Types and JSON Structures
#### `RATE_ALERT`
```json
{
  "type": "RATE_ALERT",
  "details": {
    "crypto": "ETH",
    "previous_rate": 2100,
    "new_rate": 1930,
    "change_percent": -8.1
  }
}
```
#### `TRANSACTION_SUCCESS`
```json
{
  "type": "TRANSACTION_SUCCESS",
  "details": {
    "amount": 0.25,
    "currency": "BTC",
    "direction": "sent",
    "to": "user123"
  }
}
```
#### `TRANSACTION_FAILED`
```json
{
  "type": "TRANSACTION_FAILED",
  "details": {
    "amount": 1.0,
    "currency": "ETH",
    "reason": "Insufficient balance",
    "timestamp": "2025-05-19T12:03:00Z"
  }
}
```
#### `SECURITY_ALERT`
```json
{
  "type": "SECURITY_ALERT",
  "details": {
    "event": "New login",
    "ip": "192.168.0.100",
    "location": "Sofia, Bulgaria",
    "time": "2025-05-19T21:45:00Z"
  }
}
```
#### `NEW_BLOG_POST`
```json
{
  "type": "NEW_BLOG_POST",
  "details": {
    "title": "Top 5 Crypto Wallets",
    "author": "CryptoGuru",
    "url": "https://site.com/blog/top-5-wallets"
  }
}
```
#### `SYSTEM_MESSAGE`
```json
{
  "type": "SYSTEM_MESSAGE",
  "details": {
    "message": "Scheduled maintenance at 02:00 UTC",
    "priority": "high"
  }
}
```
#### `TWO_FACTOR_CODE`
```json
{
  "type": "TWO_FACTOR_CODE",
  "details": {
    "code": "948375",
    "expires_in": 300
  }
}
```

### 5. Database Structure
#### 5.1 Table `Notifications`
- `id` (UUID)
- `user_id` (UUID)
- `type` (ENUM: RATE_ALERT, TRANSACTION_SUCCESS, TRANSACTION_FAILED, SECURITY_ALERT, NEW_BLOG_POST, SYSTEM_MESSAGE, TWO_FACTOR_CODE)
- `channel` (ENUM\[\]: EMAIL, IN_APP, TELEGRAM)
- `details` (JSON)
- `seen` (BOOLEAN)
- `created_at` (TIMESTAMP)
#### 5.2 Table `NotificationQueue`
- `id` (UUID)
- `user_id`
- `channel` (ENUM: EMAIL, TELEGRAM)
- `content` (JSON)
- `attempts` (INTEGER)
- `next_retry_time` (TIMESTAMP)
- `status` (ENUM: PENDING, FAILED, SUCCESS)
- `last_error` (TEXT)

### 6. Retry Mechanism
- On delivery failure (e.g., SMTP server error), the record is saved in the `NotificationQueue`
- `POST /retry/failed` or a scheduler triggers the retry logic
- Limited number of attempts (e.g., 3)
- After each failure – update `attempts`, `last_error`, and calculate new `next_retry_time`
- On success – status is updated to `success`

### 7. Validation and Errors
- Validation of `type` and `channel`
- Validation of user ID – existence in the database
- Validation of `details` based on `type` (e.g., `RATE_ALERT` must include `crypto`, `previous_rate`, etc.)
- Check if the user is subscribed to the corresponding notification type and channel
- For marketing emails – validate email format and user consent
**Possible Errors:**
- 400 – missing or invalid field
- 404 – non-existent user or notification
- 500 – internal error (e.g., failed connection to sending server)
**Edge Cases:**
- Missing or empty `details`
- User is deactivated or has no enabled channels
- Nonexistent channel selected (e.g., "sms")
- Attempt to send a 2FA code via in-app instead of email

### 8. QA Testing
#### 8.1 Unit Tests
- `createNotification()` – with valid and invalid inputs
- `sendEmail()` – mocked email service
- `retryFailed()` – check queue and retry logic
#### 8.2 Integration Tests
- Full flow: POST request → DB save → email/in-app delivery
- With user settings (enabled/disabled channels)
#### 8.3 Manual Testing / Postman
- POST /notifications – with valid/invalid JSON
- PATCH /notifications/{id}/seen – mark as seen
- POST /retry/failed – trigger retry logic
#### 8.4 Retry Testing
- Artificially trigger error (e.g., SMTP down)
- Check entry in `NotificationQueue`
- Confirm resend attempt after delay