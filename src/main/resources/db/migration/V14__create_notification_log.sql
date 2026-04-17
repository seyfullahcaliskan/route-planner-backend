create table if not exists notification_log (
    id uuid primary key,
    etag uuid not null,
    status varchar(30) not null,
    date_of_recorded timestamp not null,
    user_who_recorded varchar(50) not null,
    date_of_last_updated timestamp not null,
    user_who_last_updated varchar(50) not null,
    counter_of_unique_data bigint not null,

    user_id uuid,
    device_token_id uuid,
    event_type varchar(50) not null,
    title varchar(150) not null,
    body varchar(500) not null,
    payload jsonb,
    provider varchar(30) not null,
    provider_ticket_id varchar(255),
    delivery_status varchar(30) not null,
    error_message varchar(1000),

    constraint fk_notification_log_user
        foreign key (user_id) references users(id),

    constraint fk_notification_log_device_token
        foreign key (device_token_id) references device_token(id)
);

create index if not exists idx_notification_log_user_id on notification_log(user_id);
create index if not exists idx_notification_log_device_token_id on notification_log(device_token_id);
create index if not exists idx_notification_log_event_type on notification_log(event_type);
create index if not exists idx_notification_log_delivery_status on notification_log(delivery_status);
create index if not exists idx_notification_log_status on notification_log(status);