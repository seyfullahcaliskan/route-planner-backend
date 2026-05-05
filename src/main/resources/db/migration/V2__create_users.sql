CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY,
    etag UUID NOT NULL,
    status VARCHAR(30) NOT NULL,
    date_of_recorded TIMESTAMP NOT NULL,
    user_who_recorded VARCHAR(50) NOT NULL,
    date_of_last_updated TIMESTAMP NOT NULL,
    user_who_last_updated VARCHAR(50) NOT NULL,
    counter_of_unique_data BIGINT NOT NULL,

    name VARCHAR(100) NOT NULL,
    surname VARCHAR(100),

    username VARCHAR(100),
    password VARCHAR(255),

    email VARCHAR(150) NOT NULL UNIQUE,
    phone_number VARCHAR(30),

    role VARCHAR(30) NOT NULL,
    company_name VARCHAR(150),

    auth_provider VARCHAR(30) NOT NULL DEFAULT 'LOCAL',
    provider_id VARCHAR(255),

    email_verified BOOLEAN NOT NULL DEFAULT FALSE,
    avatar_url VARCHAR(500),

    -- OAuth + local unique identity constraint
    CONSTRAINT uk_users_provider_provider_id UNIQUE (auth_provider, provider_id)
);

-- Indexes
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_provider ON users(auth_provider, provider_id);