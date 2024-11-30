package com.nexters.goalpanzi.application.firebase.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.HashMap;
import java.util.Map;

public record Data(
        @Schema(description = "푸시 알림 제목", requiredMode = Schema.RequiredMode.REQUIRED)
        String title,
        @Schema(description = "푸시 알림 내용", requiredMode = Schema.RequiredMode.REQUIRED)
        String body,
        @Schema(description = "푸시 알림에 해당하는 미션 아이디", requiredMode = Schema.RequiredMode.REQUIRED)
        Long missionId
) {

    public Map<String, String> toMap() {
        Map<String, String> map = new HashMap<>();
        map.put("title", title);
        map.put("body", body);
        map.put("missionId", missionId.toString());
        return map;
    }
}
