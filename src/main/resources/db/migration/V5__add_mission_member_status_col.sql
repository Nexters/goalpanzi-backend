alter table mission_member
    add mission_status varchar(50) null;

alter table mission_member
    add check_completed boolean default false;