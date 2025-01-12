package com.nexters.goalpanzi.presentation.record;

import com.nexters.goalpanzi.application.record.MyRecordService;
import com.nexters.goalpanzi.application.record.dto.response.MyRecordResponse;
import com.nexters.goalpanzi.common.argumentresolver.LoginMemberId;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MyRecordController implements MyRecordControllerDocs {

    private final MyRecordService myRecordService;

    @Override
    @GetMapping("/api/missions/records")
    public ResponseEntity<MyRecordResponse.MyRecordWrapper> getMyMissionRecords(
            @LoginMemberId final Long memberId,
            @RequestParam final Integer page,
            @RequestParam final Integer pageSize
    ) {
        var result = myRecordService.getMyRecordList(memberId, PageRequest.of(page, pageSize));

        return ResponseEntity.ok(result);
    }
}
