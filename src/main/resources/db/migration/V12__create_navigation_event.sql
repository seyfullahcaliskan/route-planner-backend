create table if not exists navigation_event (
    id uuid primary key,
    etag uuid not null,
    status varchar(30) not null,
    date_of_recorded timestamp not null,
    user_who_recorded varchar(50) not null,
    date_of_last_updated timestamp not null,
    user_who_last_updated varchar(50) not null,
    counter_of_unique_data bigint not null,

    route_plan_id uuid not null,
    route_stop_id uuid not null,
    provider varchar(30) not null,
    navigation_url varchar(1000) not null,
    opened_at timestamp not null,

    constraint fk_navigation_event_plan
        foreign key (route_plan_id) references route_plan(id),

    constraint fk_navigation_event_stop
        foreign key (route_stop_id) references route_stop(id)
);

create index if not exists idx_navigation_event_route_plan_id on navigation_event(route_plan_id);
create index if not exists idx_navigation_event_route_stop_id on navigation_event(route_stop_id);
create index if not exists idx_navigation_event_base_status on navigation_event(status);