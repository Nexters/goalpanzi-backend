package com.nexters.goalpanzi.domain.mission;

import com.nexters.goalpanzi.acceptance.AcceptanceStep;
import com.nexters.goalpanzi.acceptance.AcceptanceTest;
import com.nexters.goalpanzi.application.auth.dto.request.GoogleLoginCommand;
import com.nexters.goalpanzi.application.auth.dto.response.LoginResponse;
import com.nexters.goalpanzi.application.mission.dto.response.MissionDetailResponse;
import com.nexters.goalpanzi.domain.member.Member;
import com.nexters.goalpanzi.domain.member.repository.MemberRepository;
import com.nexters.goalpanzi.domain.mission.repository.MissionRepository;
import com.nexters.goalpanzi.fixture.MemberFixture;
import com.nexters.goalpanzi.presentation.mission.dto.CreateMissionRequest;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

import static com.nexters.goalpanzi.acceptance.AcceptanceStep.미션_생성;
import static com.nexters.goalpanzi.fixture.MissionFixture.DESCRIPTION;
import static org.junit.jupiter.api.Assertions.*;

class MissionMemberTest extends AcceptanceTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    MissionRepository missionRepository;

    @Test
    void testMissionMember() {

        LoginResponse login = AcceptanceStep.구글_로그인(new GoogleLoginCommand("testtest"))
                .as(LoginResponse.class);

        CreateMissionRequest request = new CreateMissionRequest(DESCRIPTION, LocalDateTime.now(),
                LocalDateTime.now().plusDays(5), TimeOfDay.EVERYDAY,
                List.of(DayOfWeek.MONDAY, DayOfWeek.FRIDAY), 20);

        MissionDetailResponse missionD = 미션_생성(request, login.accessToken()).as(MissionDetailResponse.class);

        Member member = memberRepository.getMember(login.memberId());
        Mission mission = missionRepository.getMission(missionD.missionId());
        MissionMember missionMember = new MissionMember(member, mission, 3);
        System.out.println(missionMember);
    }
}