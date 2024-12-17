create table if not exists device
(
    device_id               bigint auto_increment
        primary key,
    created_at              datetime(6)     null,
    deleted_at              datetime(6)     null,
    updated_at              datetime(6)     null,
    device_identifier       varchar(255)    not null,
    push_activation_status  boolean         not null    default true,
    device_token            varchar(152)    not null,
    member_id               bigint          not null
);