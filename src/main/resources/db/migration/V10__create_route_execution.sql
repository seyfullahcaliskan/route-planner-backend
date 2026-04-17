create table if not exists route_execution (
    id uuid primary key,
    etag uuid not null,
    status varchar(30) not null,
    date_of_recorded timestamp not null,
    user_who_recorded varchar(50) not null,
    date_of_last_updated timestamp not null,
    user_who_last_updated varchar(50) not null,
    counter_of_unique_data bigint not null,

    route_plan_id uuid not null,
    started_at timestamp,
    paused_at timestamp,
    resumed_at timestamp,
    finished_at timestamp,

    current_stop_id uuid,
    completed_stop_count integer not null default 0,
    failed_stop_count integer not null default 0,
    skipped_stop_count integer not null default 0,

    actual_distance_meters bigint,
    actual_duration_seconds bigint,

    constraint fk_route_execution_plan
        foreign key (route_plan_id) references route_plan(id),

    constraint fk_route_execution_current_stop
        foreign key (current_stop_id) references route_stop(id)
);

create index if not exists idx_route_execution_route_plan_id on route_execution(route_plan_id);
create index if not exists idx_route_execution_base_status on route_execution(status);