create table if not exists route_stop (
    id uuid primary key,
    etag uuid not null,
    status varchar(30) not null,
    date_of_recorded timestamp not null,
    user_who_recorded varchar(50) not null,
    date_of_last_updated timestamp not null,
    user_who_last_updated varchar(50) not null,
    counter_of_unique_data bigint not null,

    route_plan_id uuid not null,
    external_reference varchar(100),

    customer_name varchar(150),
    customer_phone varchar(30),

    raw_address varchar(1000) not null,
    normalized_address varchar(1000),
    latitude numeric(10,7),
    longitude numeric(10,7),

    sequence_no integer not null,
    previous_sequence_no integer,
    optimization_round integer not null default 1,
    priority_no integer not null default 0,

    delivery_note varchar(1000),
    estimated_arrival_time timestamp,
    actual_arrival_time timestamp,
    delivered_at timestamp,

    stop_status varchar(30) not null,
    is_locked boolean not null default false,
    is_cancelled boolean not null default false,

    navigation_url varchar(1000),
    last_navigation_opened_at timestamp,

    constraint fk_route_stop_plan
        foreign key (route_plan_id) references route_plan(id)
);

create index if not exists idx_route_stop_route_plan_id on route_stop(route_plan_id);
create index if not exists idx_route_stop_route_plan_sequence on route_stop(route_plan_id, sequence_no);
create index if not exists idx_route_stop_status on route_stop(stop_status);
create index if not exists idx_route_stop_base_status on route_stop(status);