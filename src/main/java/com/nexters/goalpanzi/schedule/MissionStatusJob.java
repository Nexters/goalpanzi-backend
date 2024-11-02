package com.nexters.goalpanzi.schedule;

import com.nexters.goalpanzi.application.mission.MissionMemberService;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.ScheduleBuilder;
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
        return CronScheduleBuilder.cronSchedule("0 45 16 * * ?")
                .withMisfireHandlingInstructionDoNothing();
    }

    @Override
    protected void executeInternal(final JobExecutionContext context) {
        log.info("MissionStatusJob started.");

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        try {
            missionMemberService.batchUpdateStatus();
        } catch (Exception e) {
            log.error("Error occurred while executing MissionStatusJob", e);
        }

        stopWatch.stop();  // 타이머 종료
        log.info("MissionStatusJob finished. Elapsed time: {} ms", stopWatch.getTime());
    }
}
