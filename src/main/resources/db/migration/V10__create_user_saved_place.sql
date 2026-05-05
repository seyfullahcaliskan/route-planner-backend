CREATE TABLE IF NOT EXISTS user_saved_place (
    id UUID PRIMARY KEY,
    etag UUID NOT NULL,
    status VARCHAR(30) NOT NULL,
    date_of_recorded TIMESTAMP NOT NULL,
    user_who_recorded VARCHAR(50) NOT NULL,
    date_of_last_updated TIMESTAMP NOT NULL,
    user_who_last_updated VARCHAR(50) NOT NULL,
    counter_of_unique_data BIGINT NOT NULL,

    user_id UUID NOT NULL,
    place_name VARCHAR(100) NOT NULL,
    place_type VARCHAR(30) NOT NULL,
    address VARCHAR(500) NOT NULL,
    latitude NUMERIC(12,8) NOT NULL,
    longitude NUMERIC(12,8) NOT NULL,
    is_default_start BOOLEAN NOT NULL DEFAULT FALSE,
    is_default_end BOOLEAN NOT NULL DEFAULT FALSE,

    CONSTRAINT fk_user_saved_place_user
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_user_saved_place_user_id ON user_saved_place(user_id);
CREATE INDEX IF NOT EXISTS idx_user_saved_place_type ON user_saved_place(place_type);