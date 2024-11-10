package com.nexters.goalpanzi.schedule;

import com.nexters.goalpanzi.application.mission.MissionVerificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@DisallowConcurrentExecution
@Component
public class MissionVerificationPushJob extends AbstractJob<CronTrigger> implements CustomAutomationJob {

    private final MissionVerificationService missionVerificationService;

    @Override
    protected ScheduleBuilder<CronTrigger> getScheduleBuilder() {
        // 09:00, 15:00 마다 실행
        return CronScheduleBuilder.cronSchedule("0 0 9,15 * * ?")
                .withMisfireHandlingInstructionDoNothing();
    }

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        missionVerificationService.sendVerificationPushMessage();
    }
}
