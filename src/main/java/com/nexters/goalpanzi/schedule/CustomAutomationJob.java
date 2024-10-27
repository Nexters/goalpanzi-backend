package com.nexters.goalpanzi.schedule;

import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.Trigger;

public interface CustomAutomationJob extends Job {
    Trigger getTrigger();
    JobDetail getJobDetail();
}
