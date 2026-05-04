CREATE TABLE IF NOT EXISTS device_token (
    id UUID PRIMARY KEY,
    etag UUID NOT NULL,
    status VARCHAR(30) NOT NULL,
    date_of_recorded TIMESTAMP NOT NULL,
    user_who_recorded VARCHAR(50) NOT NULL,
    date_of_last_updated TIMESTAMP NOT NULL,
    user_who_last_updated VARCHAR(50) NOT NULL,
    counter_of_unique_data BIGINT NOT NULL,

    user_id UUID NOT NULL,
    expo_push_token VARCHAR(255) NOT NULL UNIQUE,
    platform VARCHAR(20) NOT NULL,
    device_name VARCHAR(100),
    app_version VARCHAR(50),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,

    CONSTRAINT fk_device_token_user
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_device_token_user_id ON device_token(user_id);
CREATE INDEX IF NOT EXISTS idx_device_token_active ON device_token(is_active);
CREATE INDEX IF NOT EXISTS idx_device_token_status ON device_token(status);