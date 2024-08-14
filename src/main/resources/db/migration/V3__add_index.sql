create index idx_member__created_at on member (created_at);
create index idx_member__updated_at on member (updated_at);
create index idx_member__deleted_at on member (deleted_at);

create index idx_mission_member__created_at on mission_member (created_at);
create index idx_mission_member__updated_at on mission_member (updated_at);
create index idx_mission_member__deleted_at on mission_member (deleted_at);

create index idx_mission_verification__created_at on mission_verification (created_at);
create index idx_mission_verification__updated_at on mission_verification (updated_at);
create index idx_mission_verification__deleted_at on mission_verification (deleted_at);

create index idx_mission__created_at on mission (created_at);
create index idx_mission__updated_at on mission (updated_at);
create index idx_mission__deleted_at on mission (deleted_at);