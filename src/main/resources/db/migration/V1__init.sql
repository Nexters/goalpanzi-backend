create table if not exists member
(
    member_id      bigint auto_increment
        primary key,
    created_at     datetime(6)  null,
    deleted_at     datetime(6)  null,
    updated_at     datetime(6)  null,
    character_type varchar(255) null,
    email          varchar(255) not null,
    nickname       varchar(255) null,
    social_id      varchar(255) null,
    social_type    varchar(255) null,
    constraint uk_member__social_id
        unique (social_id)
);

create table if not exists mission
(
    mission_id         bigint auto_increment
        primary key,
    created_at         datetime(6)  null,
    deleted_at         datetime(6)  null,
    updated_at         datetime(6)  null,
    board_count        int          not null,
    description        varchar(255) not null,
    host_member_id     bigint       not null,
    invitation_code    varchar(255) not null,
    mission_day        varchar(255) not null,
    mission_end_date   datetime(6)  not null,
    mission_start_date datetime(6)  not null,
    upload_end_time    varchar(255) not null,
    upload_start_time  varchar(255) not null
);