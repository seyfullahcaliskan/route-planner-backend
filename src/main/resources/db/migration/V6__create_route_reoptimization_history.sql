CREATE TABLE IF NOT EXISTS route_reoptimization_history (
    id UUID PRIMARY KEY,
    etag UUID NOT NULL,
    status VARCHAR(30) NOT NULL,
    date_of_recorded TIMESTAMP NOT NULL,
    user_who_recorded VARCHAR(50) NOT NULL,
    date_of_last_updated TIMESTAMP NOT NULL,
    user_who_last_updated VARCHAR(50) NOT NULL,
    counter_of_unique_data BIGINT NOT NULL,

    route_plan_id UUID NOT NULL,
    optimization_round INTEGER NOT NULL,
    reason VARCHAR(50) NOT NULL,

    previous_route_snapshot JSONB NOT NULL,
    new_route_snapshot JSONB NOT NULL,

    triggered_by_user_id UUID,
    note VARCHAR(1000),

    CONSTRAINT fk_reopt_history_plan
        FOREIGN KEY (route_plan_id) REFERENCES route_plan(id) ON DELETE CASCADE,

    CONSTRAINT fk_reopt_history_user
        FOREIGN KEY (triggered_by_user_id) REFERENCES users(id) ON DELETE SET NULL
);

CREATE INDEX IF NOT EXISTS idx_reopt_history_route_plan_id ON route_reoptimization_history(route_plan_id);
CREATE INDEX IF NOT EXISTS idx_reopt_history_base_status ON route_reoptimization_history(status);