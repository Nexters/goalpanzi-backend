package com.nexters.goalpanzi.schedule;

import com.nexters.goalpanzi.application.mission.MissionMemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@DisallowConcurrentExecution
@Component
public class MissionReadyPushJob extends AbstractJob<CronTrigger> implements CustomAutomationJob {

    private final MissionMemberService missionMemberService;

    @Override
    protected ScheduleBuilder<CronTrigger> getScheduleBuilder() {
        // 11:00, 23:00 마다 실행
        return CronScheduleBuilder.cronSchedule("0 0 11,23 * * ?")
                .withMisfireHandlingInstructionDoNothing();
    }

    @Override
    protected void executeInternal(final JobExecutionContext context) throws JobExecutionException {
        missionMemberService.sendReadyPushMessage();
    }
}
