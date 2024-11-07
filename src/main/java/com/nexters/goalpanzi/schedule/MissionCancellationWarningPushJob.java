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

import static com.nexters.goalpanzi.domain.firebase.PushNotificationMessage.MISSION_CANCELLATION_WARNING;

@Slf4j
@RequiredArgsConstructor
@DisallowConcurrentExecution
@Component
public class MissionCancellationWarningPushJob extends AbstractJob<CronTrigger> implements CustomAutomationJob {

    private final MissionRepository missionRepository;
    private final MissionMemberRepository missionMemberRepository;

    private final PushNotificationSender pushNotificationSender;

    @Override
    protected ScheduleBuilder<CronTrigger> getScheduleBuilder() {
        // 11:30, 23:30 마다 실행
        return CronScheduleBuilder.cronSchedule("0 30 11,23 * * ?")
                .withMisfireHandlingInstructionDoNothing();
    }

    @Override
    @Transactional
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        List<Mission> missions = missionRepository.getReadyMissions();
        missions.forEach(mission -> {
            List<MissionMember> missionMembers = missionMemberRepository.findAllByMissionId(mission.getId());
            int memberCount = missionMembers.size();

            if (mission.isReady() && memberCount < MissionMemberCount.MIN.getCount()) {
                String topic = TopicGenerator.getTopic(mission.getId());
                pushNotificationSender.sendGroupMessage(
                        MISSION_CANCELLATION_WARNING.getTitle(),
                        MISSION_CANCELLATION_WARNING.getBody(),
                        topic
                );
            }
        });
    }
}
