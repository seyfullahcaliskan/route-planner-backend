create table if not exists route_plan (
    id uuid primary key,
    etag uuid not null,
    status varchar(30) not null,
    date_of_recorded timestamp not null,
    user_who_recorded varchar(50) not null,
    date_of_last_updated timestamp not null,
    user_who_last_updated varchar(50) not null,
    counter_of_unique_data bigint not null,

    user_id uuid not null,
    title varchar(200) not null,
    description varchar(500),

    route_date date,

    start_latitude numeric(10,7),
    start_longitude numeric(10,7),
    start_address varchar(500),

    end_latitude numeric(10,7),
    end_longitude numeric(10,7),
    end_address varchar(500),

    use_tolls boolean not null default false,
    use_highways boolean not null default true,
    use_traffic boolean not null default true,

    optimization_type varchar(30) not null,
    navigation_provider varchar(30) not null,

    total_stop_count integer not null default 0,
    completed_stop_count integer not null default 0,
    failed_stop_count integer not null default 0,
    skipped_stop_count integer not null default 0,

    estimated_total_distance_meters bigint,
    estimated_total_duration_seconds bigint,
    actual_total_distance_meters bigint,
    actual_total_duration_seconds bigint,

    plan_status varchar(30) not null,
    last_optimized_at timestamp,
    last_started_at timestamp,
    completed_at timestamp,

    constraint fk_route_plan_user
        foreign key (user_id) references users(id)
);

create index if not exists idx_route_plan_user_id on route_plan(user_id);
create index if not exists idx_route_plan_status on route_plan(plan_status);
create index if not exists idx_route_plan_base_status on route_plan(status);