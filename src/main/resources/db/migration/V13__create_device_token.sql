create table if not exists device_token (
    id uuid primary key,
    etag uuid not null,
    status varchar(30) not null,
    date_of_recorded timestamp not null,
    user_who_recorded varchar(50) not null,
    date_of_last_updated timestamp not null,
    user_who_last_updated varchar(50) not null,
    counter_of_unique_data bigint not null,

    user_id uuid not null,
    expo_push_token varchar(255) not null unique,
    platform varchar(20) not null,
    device_name varchar(100),
    app_version varchar(50),
    is_active boolean not null default true,

    constraint fk_device_token_user
        foreign key (user_id) references users(id)
);

create index if not exists idx_device_token_user_id on device_token(user_id);
create index if not exists idx_device_token_active on device_token(is_active);
create index if not exists idx_device_token_status on device_token(status);