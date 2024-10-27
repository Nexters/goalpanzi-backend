package com.nexters.goalpanzi.schedule;

import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.ScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.scheduling.quartz.QuartzJobBean;

public abstract class AbstractJob<T extends Trigger> extends QuartzJobBean implements Job {

    private static final String JOB_PREFIX = "MissionMateService-Job-";
    private static final String TRIGGER_PREFIX = "MissionMate-Trigger-";

    public JobDetail getJobDetail() {
        return JobBuilder.newJob(this.getClass())
                .withIdentity(JOB_PREFIX + this.getClass().getSimpleName())
                .build();
    }

    public Trigger getTrigger() {
        return TriggerBuilder.newTrigger()
                .withSchedule(getScheduleBuilder())
                .withIdentity(TRIGGER_PREFIX + this.getClass().getSimpleName())
                .build();
    }

    protected abstract ScheduleBuilder<T> getScheduleBuilder();
}
