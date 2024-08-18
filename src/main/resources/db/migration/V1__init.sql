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

create table if not exists mission_verification
(
    mission_verification_id bigint auto_increment
        primary key,
    created_at              datetime(6),
    deleted_at              datetime(6),
    updated_at              datetime(6),
    board_number            int,
    image_url               varchar(255),
    member_id               bigint not null,
    mission_id              bigint not null
);

create table if not exists mission_member
(
    mission_member_id  bigint auto_increment
        primary key,
    created_at         datetime(6),
    deleted_at         datetime(6),
    updated_at         datetime(6),
    member_id          bigint not null,
    mission_id         bigint not null,
    verification_count int
);

create table if not exists mission_verification_view
(
    mission_verification_view_id  bigint auto_increment
        primary key,
    created_at              datetime(6),
    deleted_at              datetime(6),
    updated_at              datetime(6),
    mission_verification_id bigint not null,
    member_id               bigint not null
);
