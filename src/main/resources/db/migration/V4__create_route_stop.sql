CREATE TABLE IF NOT EXISTS route_stop (
    id UUID PRIMARY KEY,
    etag UUID NOT NULL,
    status VARCHAR(30) NOT NULL,
    date_of_recorded TIMESTAMP NOT NULL,
    user_who_recorded VARCHAR(50) NOT NULL,
    date_of_last_updated TIMESTAMP NOT NULL,
    user_who_last_updated VARCHAR(50) NOT NULL,
    counter_of_unique_data BIGINT NOT NULL,

    route_plan_id UUID NOT NULL,
    external_reference VARCHAR(100),

    customer_name VARCHAR(150),
    customer_phone VARCHAR(30),

    raw_address VARCHAR(1000) NOT NULL,
    normalized_address VARCHAR(1000),
    latitude NUMERIC(10,7),
    longitude NUMERIC(10,7),

    sequence_no INTEGER NOT NULL,
    previous_sequence_no INTEGER,
    optimization_round INTEGER NOT NULL DEFAULT 1,
    priority_no INTEGER NOT NULL DEFAULT 0,

    delivery_note VARCHAR(1000),
    estimated_arrival_time TIMESTAMP,
    actual_arrival_time TIMESTAMP,
    delivered_at TIMESTAMP,

    stop_status VARCHAR(30) NOT NULL,
    is_locked BOOLEAN NOT NULL DEFAULT FALSE,
    is_cancelled BOOLEAN NOT NULL DEFAULT FALSE,

    navigation_url VARCHAR(1000),
    last_navigation_opened_at TIMESTAMP,

    CONSTRAINT fk_route_stop_plan
        FOREIGN KEY (route_plan_id) REFERENCES route_plan(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_route_stop_route_plan_id ON route_stop(route_plan_id);
CREATE INDEX IF NOT EXISTS idx_route_stop_route_plan_sequence ON route_stop(route_plan_id, sequence_no);
CREATE INDEX IF NOT EXISTS idx_route_stop_status ON route_stop(stop_status);
CREATE INDEX IF NOT EXISTS idx_route_stop_base_status ON route_stop(status);