CREATE TABLE IF NOT EXISTS route_execution (
    id UUID PRIMARY KEY,
    etag UUID NOT NULL,
    status VARCHAR(30) NOT NULL,
    date_of_recorded TIMESTAMP NOT NULL,
    user_who_recorded VARCHAR(50) NOT NULL,
    date_of_last_updated TIMESTAMP NOT NULL,
    user_who_last_updated VARCHAR(50) NOT NULL,
    counter_of_unique_data BIGINT NOT NULL,

    route_plan_id UUID NOT NULL,
    started_at TIMESTAMP,
    paused_at TIMESTAMP,
    resumed_at TIMESTAMP,
    finished_at TIMESTAMP,

    current_stop_id UUID,
    completed_stop_count INTEGER NOT NULL DEFAULT 0,
    failed_stop_count INTEGER NOT NULL DEFAULT 0,
    skipped_stop_count INTEGER NOT NULL DEFAULT 0,

    actual_distance_meters BIGINT,
    actual_duration_seconds BIGINT,

    CONSTRAINT fk_route_execution_plan
        FOREIGN KEY (route_plan_id) REFERENCES route_plan(id) ON DELETE CASCADE,

    CONSTRAINT fk_route_execution_current_stop
        FOREIGN KEY (current_stop_id) REFERENCES route_stop(id) ON DELETE SET NULL
);

CREATE INDEX IF NOT EXISTS idx_route_execution_route_plan_id ON route_execution(route_plan_id);
CREATE INDEX IF NOT EXISTS idx_route_execution_base_status ON route_execution(status);