package com.nexters.goalpanzi.schedule;

import com.nexters.goalpanzi.application.firebase.TopicGenerator;
import com.nexters.goalpanzi.domain.firebase.PushNotificationMessage;
import com.nexters.goalpanzi.domain.mission.Mission;
import com.nexters.goalpanzi.domain.mission.MissionVerification;
import com.nexters.goalpanzi.domain.mission.repository.MissionRepository;
import com.nexters.goalpanzi.domain.mission.repository.MissionVerificationRepository;
import com.nexters.goalpanzi.infrastructure.firebase.PushNotificationSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.nexters.goalpanzi.domain.firebase.PushNotificationMessage.MISSION_NO_ONE_VERIFIED;
import static com.nexters.goalpanzi.domain.firebase.PushNotificationMessage.MISSION_VERIFIED;

@Slf4j
@RequiredArgsConstructor
@DisallowConcurrentExecution
@Component
public class MissionVerificationPushJob extends AbstractJob<CronTrigger> implements CustomAutomationJob {

    private final MissionRepository missionRepository;
    private final MissionVerificationRepository missionVerificationRepository;

    private final PushNotificationSender pushNotificationSender;

    @Override
    protected ScheduleBuilder<CronTrigger> getScheduleBuilder() {
        // 09:00, 15:00 마다 실행
        return CronScheduleBuilder.cronSchedule("0 0 9,15 * * ?")
                .withMisfireHandlingInstructionDoNothing();
    }

    @Override
    @Transactional
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        LocalDate today = LocalDate.now();
        int hour = LocalDateTime.now().getHour();
        List<Mission> missions = missionRepository.getInProgressMissions();

        missions.forEach(mission -> {
            if (mission.isMissionDay() && mission.isPushTime(hour)) {
                List<MissionVerification> verifications = missionVerificationRepository.findAllByMissionIdAndDate(mission.getId(), today);
                int verificationCount = verifications.size();
                String topic = TopicGenerator.getTopic(mission.getId());

                if (verificationCount == 0) {
                    sendNoOneVerifiedMessage(MISSION_NO_ONE_VERIFIED, topic);
                } else {
                    sendVerifiedMessage(MISSION_VERIFIED, topic, verificationCount);
                }
            }
        });
    }

    private void sendVerifiedMessage(final PushNotificationMessage message, final String topic, final int verificationCount) {
        pushNotificationSender.sendGroupMessage(
                message.getTitle(verificationCount),
                message.getBody(),
                topic
        );
    }

    private void sendNoOneVerifiedMessage(final PushNotificationMessage message, final String topic) {
        pushNotificationSender.sendGroupMessage(
                message.getTitle(),
                message.getBody(),
                topic
        );
    }
}
