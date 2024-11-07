package com.nexters.goalpanzi.schedule;

import com.nexters.goalpanzi.domain.member.Member;
import com.nexters.goalpanzi.domain.mission.Mission;
import com.nexters.goalpanzi.domain.mission.MissionMember;
import com.nexters.goalpanzi.domain.mission.MissionVerification;
import com.nexters.goalpanzi.domain.mission.repository.MissionMemberRepository;
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
import java.util.Optional;

import static com.nexters.goalpanzi.domain.firebase.PushNotificationMessage.MISSION_VERIFICATION_WARNING;

@Slf4j
@RequiredArgsConstructor
@DisallowConcurrentExecution
@Component
public class MissionVerificationWarningPushJob extends AbstractJob<CronTrigger> implements CustomAutomationJob {

    private final MissionRepository missionRepository;
    private final MissionMemberRepository missionMemberRepository;
    private final MissionVerificationRepository missionVerificationRepository;

    private final PushNotificationSender pushNotificationSender;

    @Override
    protected ScheduleBuilder<CronTrigger> getScheduleBuilder() {
        // 11:00, 23:00 마다 실행
        return CronScheduleBuilder.cronSchedule("0 0 11,23 * * ?")
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
                List<MissionMember> missionMembers = missionMemberRepository.findAllByMissionId(mission.getId());

                missionMembers.forEach(missionMember -> {
                    Member member = missionMember.getMember();
                    Optional<MissionVerification> verification = missionVerificationRepository.findByMemberIdAndMissionIdAndDate(member.getId(), mission.getId(), today);
                    if (verification.isEmpty() && member.getDeviceToken() != null) {
                        sendMessage(member);
                    }
                });
            }
        });
    }

    private void sendMessage(final Member member) {
        pushNotificationSender.sendIndividualMessage(
                MISSION_VERIFICATION_WARNING.getTitle(),
                MISSION_VERIFICATION_WARNING.getBody(),
                member.getDeviceToken()
        );
    }
}
