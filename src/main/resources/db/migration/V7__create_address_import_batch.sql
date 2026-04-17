create table if not exists address_import_batch (
    id uuid primary key,
    etag uuid not null,
    status varchar(30) not null,
    date_of_recorded timestamp not null,
    user_who_recorded varchar(50) not null,
    date_of_last_updated timestamp not null,
    user_who_last_updated varchar(50) not null,
    counter_of_unique_data bigint not null,

    route_plan_id uuid not null,
    file_name varchar(255),
    import_type varchar(30) not null,
    total_row_count integer not null default 0,
    success_row_count integer not null default 0,
    failed_row_count integer not null default 0,
    raw_payload jsonb,

    constraint fk_import_batch_plan
        foreign key (route_plan_id) references route_plan(id)
);

create index if not exists idx_import_batch_route_plan_id on address_import_batch(route_plan_id);
create index if not exists idx_import_batch_base_status on address_import_batch(status);