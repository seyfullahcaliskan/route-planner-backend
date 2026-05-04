CREATE TABLE IF NOT EXISTS navigation_event (
    id UUID PRIMARY KEY,
    etag UUID NOT NULL,
    status VARCHAR(30) NOT NULL,
    date_of_recorded TIMESTAMP NOT NULL,
    user_who_recorded VARCHAR(50) NOT NULL,
    date_of_last_updated TIMESTAMP NOT NULL,
    user_who_last_updated VARCHAR(50) NOT NULL,
    counter_of_unique_data BIGINT NOT NULL,

    route_plan_id UUID NOT NULL,
    route_stop_id UUID NOT NULL,
    provider VARCHAR(30) NOT NULL,
    navigation_url VARCHAR(1000) NOT NULL,
    opened_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_navigation_event_plan
        FOREIGN KEY (route_plan_id) REFERENCES route_plan(id) ON DELETE CASCADE,

    CONSTRAINT fk_navigation_event_stop
        FOREIGN KEY (route_stop_id) REFERENCES route_stop(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_navigation_event_route_plan_id ON navigation_event(route_plan_id);
CREATE INDEX IF NOT EXISTS idx_navigation_event_route_stop_id ON navigation_event(route_stop_id);
CREATE INDEX IF NOT EXISTS idx_navigation_event_base_status ON navigation_event(status);