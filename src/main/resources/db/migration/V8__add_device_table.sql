create table if not exists device
(
    device_id               bigint auto_increment
        primary key,
    created_at              datetime(6)     null,
    deleted_at              datetime(6)     null,
    updated_at              datetime(6)     null,
    device_identifier       varchar(255)    not null,
    device_token            varchar(152)    not null,
    os_type                 varchar(3)      not null,
    push_activation_status  boolean         not null    default false,
    member_id               bigint          not null
);