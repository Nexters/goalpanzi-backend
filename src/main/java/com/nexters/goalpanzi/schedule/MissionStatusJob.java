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
public class MissionStatusJob extends AbstractJob<CronTrigger> implements CustomAutomationJob {

    private final MissionMemberService missionMemberService;

    @Override
    protected ScheduleBuilder<CronTrigger> getScheduleBuilder() {
        // 00:00, 06:00, 12:00, 18:00 마다 실행
        return CronScheduleBuilder.cronSchedule("0 0 */6 * * ?")
                .withMisfireHandlingInstructionDoNothing();
    }

    @Override
    protected void executeInternal(final JobExecutionContext context) {
        missionMemberService.batchUpdateStatus();
    }
}
