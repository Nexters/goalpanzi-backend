create table if not exists device_subscription
(
    device_subscription_id  bigint auto_increment
        primary key,
    created_at              datetime(6)     null,
    deleted_at              datetime(6)     null,
    updated_at              datetime(6)     null,
    device_id               bigint          not null,
    mission_id              bigint          not null
);