CREATE TABLE IF NOT EXISTS route_plan (
    id UUID PRIMARY KEY,
    etag UUID NOT NULL,
    status VARCHAR(30) NOT NULL,
    date_of_recorded TIMESTAMP NOT NULL,
    user_who_recorded VARCHAR(50) NOT NULL,
    date_of_last_updated TIMESTAMP NOT NULL,
    user_who_last_updated VARCHAR(50) NOT NULL,
    counter_of_unique_data BIGINT NOT NULL,

    user_id UUID NOT NULL,
    title VARCHAR(200) NOT NULL,
    description VARCHAR(500),

    route_date DATE,

    start_latitude NUMERIC(10,7),
    start_longitude NUMERIC(10,7),
    start_address VARCHAR(500),

    end_latitude NUMERIC(10,7),
    end_longitude NUMERIC(10,7),
    end_address VARCHAR(500),

    use_tolls BOOLEAN NOT NULL DEFAULT FALSE,
    use_highways BOOLEAN NOT NULL DEFAULT TRUE,
    use_traffic BOOLEAN NOT NULL DEFAULT TRUE,

    optimization_type VARCHAR(30) NOT NULL,
    navigation_provider VARCHAR(30) NOT NULL,

    total_stop_count INTEGER NOT NULL DEFAULT 0,
    completed_stop_count INTEGER NOT NULL DEFAULT 0,
    failed_stop_count INTEGER NOT NULL DEFAULT 0,
    skipped_stop_count INTEGER NOT NULL DEFAULT 0,

    estimated_total_distance_meters BIGINT,
    estimated_total_duration_seconds BIGINT,
    actual_total_distance_meters BIGINT,
    actual_total_duration_seconds BIGINT,

    plan_status VARCHAR(30) NOT NULL,
    last_optimized_at TIMESTAMP,
    last_started_at TIMESTAMP,
    completed_at TIMESTAMP,

    CONSTRAINT fk_route_plan_user
        FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE INDEX IF NOT EXISTS idx_route_plan_user_id ON route_plan(user_id);
CREATE INDEX IF NOT EXISTS idx_route_plan_status ON route_plan(plan_status);
CREATE INDEX IF NOT EXISTS idx_route_plan_base_status ON route_plan(status);
CREATE INDEX IF NOT EXISTS idx_route_plan_route_date ON route_plan(route_date);