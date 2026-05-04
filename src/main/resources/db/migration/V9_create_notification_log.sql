CREATE TABLE IF NOT EXISTS notification_log (
    id UUID PRIMARY KEY,
    etag UUID NOT NULL,
    status VARCHAR(30) NOT NULL,
    date_of_recorded TIMESTAMP NOT NULL,
    user_who_recorded VARCHAR(50) NOT NULL,
    date_of_last_updated TIMESTAMP NOT NULL,
    user_who_last_updated VARCHAR(50) NOT NULL,
    counter_of_unique_data BIGINT NOT NULL,

    user_id UUID,
    device_token_id UUID,
    event_type VARCHAR(50) NOT NULL,
    title VARCHAR(150) NOT NULL,
    body VARCHAR(500) NOT NULL,
    payload JSONB,
    provider VARCHAR(30) NOT NULL,
    provider_ticket_id VARCHAR(255),
    delivery_status VARCHAR(30) NOT NULL,
    error_message VARCHAR(1000),

    CONSTRAINT fk_notification_log_user
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,

    CONSTRAINT fk_notification_log_device_token
        FOREIGN KEY (device_token_id) REFERENCES device_token(id) ON DELETE SET NULL
);

CREATE INDEX IF NOT EXISTS idx_notification_log_user_id ON notification_log(user_id);
CREATE INDEX IF NOT EXISTS idx_notification_log_device_token_id ON notification_log(device_token_id);
CREATE INDEX IF NOT EXISTS idx_notification_log_event_type ON notification_log(event_type);
CREATE INDEX IF NOT EXISTS idx_notification_log_delivery_status ON notification_log(delivery_status);
CREATE INDEX IF NOT EXISTS idx_notification_log_status ON notification_log(status);