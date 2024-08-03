alter table mission_member
    add constraint uk_mission_member__mission_id_member_id
        unique (mission_id, member_id);
