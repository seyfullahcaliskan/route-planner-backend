create table if not exists route_reoptimization_history (
    id uuid primary key,
    etag uuid not null,
    status varchar(30) not null,
    date_of_recorded timestamp not null,
    user_who_recorded varchar(50) not null,
    date_of_last_updated timestamp not null,
    user_who_last_updated varchar(50) not null,
    counter_of_unique_data bigint not null,

    route_plan_id uuid not null,
    optimization_round integer not null,
    reason varchar(50) not null,

    previous_route_snapshot jsonb not null,
    new_route_snapshot jsonb not null,

    triggered_by_user_id uuid,
    note varchar(1000),

    constraint fk_reopt_history_plan
        foreign key (route_plan_id) references route_plan(id),

    constraint fk_reopt_history_user
        foreign key (triggered_by_user_id) references users(id)
);

create index if not exists idx_reopt_history_route_plan_id on route_reoptimization_history(route_plan_id);
create index if not exists idx_reopt_history_base_status on route_reoptimization_history(status);