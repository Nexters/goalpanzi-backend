package com.nexters.goalpanzi.schedule;

import com.nexters.goalpanzi.application.device.DeviceSubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@DisallowConcurrentExecution
@Component
public class MissionSubscriptionJob extends AbstractJob<CronTrigger> implements CustomAutomationJob {

    private final DeviceSubscriptionService deviceSubscriptionService;

    @Override
    protected ScheduleBuilder<CronTrigger> getScheduleBuilder() {
        // 00:05, 06:05, 12:05, 18:05 마다 실행 (= MissionStatusJob 이벤트 실행 5분 뒤)
        return CronScheduleBuilder.cronSchedule("0 5 */6 * * ?")
                .withMisfireHandlingInstructionDoNothing();
    }

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        deviceSubscriptionService.unsubscribeFromUselessMissions();
    }
}
