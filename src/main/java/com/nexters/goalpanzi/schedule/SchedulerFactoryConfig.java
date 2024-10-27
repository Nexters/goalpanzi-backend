package com.nexters.goalpanzi.schedule;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

@Configuration
public class SchedulerFactoryConfig {

    private static final String SCHEDULER_THREAD_POOL_EXECUTOR = "schedulerThreadPool";

    @Bean(SCHEDULER_THREAD_POOL_EXECUTOR)
    public ThreadPoolTaskExecutor executor() {
        final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(1);
        executor.setThreadNamePrefix("executor");
        executor.initialize();
        return executor;
    }

    @Bean
    public SchedulerFactoryBean schedulerFactory(
            @Qualifier(SCHEDULER_THREAD_POOL_EXECUTOR) ThreadPoolTaskExecutor threadPoolTaskExecutor
    ) {
        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        factory.setTaskExecutor(threadPoolTaskExecutor);
        return factory;
    }
}
