create table if not exists user_saved_place (
    id uuid primary key,
    etag uuid not null,
    status varchar(30) not null,
    date_of_recorded timestamp not null,
    user_who_recorded varchar(50) not null,
    date_of_last_updated timestamp not null,
    user_who_last_updated varchar(50) not null,
    counter_of_unique_data bigint not null,

    user_id uuid not null,
    place_name varchar(100) not null,
    place_type varchar(30) not null,
    address varchar(500) not null,
    latitude numeric(12,8) not null,
    longitude numeric(12,8) not null,
    is_default_start boolean not null default false,
    is_default_end boolean not null default false,

    constraint fk_user_saved_place_user
        foreign key (user_id) references users(id)
);
