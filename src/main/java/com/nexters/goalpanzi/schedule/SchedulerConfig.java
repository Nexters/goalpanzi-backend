package com.nexters.goalpanzi.schedule;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class SchedulerConfig {
    private final Scheduler scheduler;
    private final List<CustomAutomationJob> jobList;

    @PostConstruct
    public void start() {
        log.info("Scheduler Job 등록");
        try {
            for (CustomAutomationJob job : jobList) {
                JobDetail jobDetail = job.getJobDetail();
                if (scheduler.checkExists(jobDetail.getKey())) {
                    scheduler.deleteJob(jobDetail.getKey());
                }
                scheduler.scheduleJob(jobDetail, job.getTrigger());
            }
            scheduler.start();
            log.info("Scheduler Start");
        } catch (SchedulerException e) {
            log.error("SchedulerException", e);
        }
    }
}
