package com.nexters.goalpanzi.schedule;

import com.nexters.goalpanzi.application.mission.MissionMemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@DisallowConcurrentExecution
@Component
public class MissionCancellationWarningPushJob extends AbstractJob<CronTrigger> implements CustomAutomationJob {

    private final MissionMemberService missionMemberService;

    @Override
    protected ScheduleBuilder<CronTrigger> getScheduleBuilder() {
        // 11:30, 23:30 마다 실행
        return CronScheduleBuilder.cronSchedule("0 30 11,23 * * ?")
                .withMisfireHandlingInstructionDoNothing();
    }

    @Override
    @Transactional
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        missionMemberService.sendCancellationWarningPushMessage();
    }
}
