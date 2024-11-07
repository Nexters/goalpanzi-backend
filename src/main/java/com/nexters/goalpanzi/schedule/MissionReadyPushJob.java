package com.nexters.goalpanzi.schedule;

import com.nexters.goalpanzi.application.firebase.TopicGenerator;
import com.nexters.goalpanzi.domain.mission.Mission;
import com.nexters.goalpanzi.domain.mission.MissionMember;
import com.nexters.goalpanzi.domain.mission.MissionMemberCount;
import com.nexters.goalpanzi.domain.mission.repository.MissionMemberRepository;
import com.nexters.goalpanzi.domain.mission.repository.MissionRepository;
import com.nexters.goalpanzi.infrastructure.firebase.PushNotificationSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.nexters.goalpanzi.domain.firebase.PushNotificationMessage.MISSION_READY;

@Slf4j
@RequiredArgsConstructor
@DisallowConcurrentExecution
@Component
public class MissionReadyPushJob extends AbstractJob<CronTrigger> implements CustomAutomationJob {

    private final MissionRepository missionRepository;
    private final MissionMemberRepository missionMemberRepository;

    private final PushNotificationSender pushNotificationSender;

    @Override
    protected ScheduleBuilder<CronTrigger> getScheduleBuilder() {
        // 11:00, 23:00 마다 실행
        return CronScheduleBuilder.cronSchedule("0 0 11,23 * * ?")
                .withMisfireHandlingInstructionDoNothing();
    }

    @Override
    @Transactional
    protected void executeInternal(final JobExecutionContext context) throws JobExecutionException {
        List<Mission> missions = missionRepository.getReadyMissions();
        missions.forEach(mission -> {
            if (mission.isReadyTime() && hasEnoughMember(mission.getId())) {
                String topic = TopicGenerator.getTopic(mission.getId());
                pushNotificationSender.sendGroupMessage(
                        MISSION_READY.getTitle(),
                        MISSION_READY.getBody(),
                        topic
                );
            }
        });
    }

    private boolean hasEnoughMember(final Long missionId) {
        List<MissionMember> missionMembers = missionMemberRepository.findAllByMissionId(missionId);
        int memberCount = missionMembers.size();

        return memberCount >= MissionMemberCount.MIN.getCount();
    }
}
