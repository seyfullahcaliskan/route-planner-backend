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
    surname VARCHAR(100) NOT NULL,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    phone_number VARCHAR(30),
    role VARCHAR(30) NOT NULL,
    company_name VARCHAR(150)
);

CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);