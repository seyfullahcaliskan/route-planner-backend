create table if not exists users (
    id uuid primary key,
    etag uuid not null,
    status varchar(30) not null,
    date_of_recorded timestamp not null,
    user_who_recorded varchar(50) not null,
    date_of_last_updated timestamp not null,
    user_who_last_updated varchar(50) not null,
    counter_of_unique_data bigint not null,

    name varchar(100) not null,
    surname varchar(100) not null,
    username varchar(100) not null unique,
    password varchar(255) not null,
    email varchar(150) not null unique,
    phone_number varchar(30),
    role varchar(30) not null,
    company_name varchar(150)
);