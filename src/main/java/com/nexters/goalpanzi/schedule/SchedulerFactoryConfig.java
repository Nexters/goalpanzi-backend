package com.nexters.goalpanzi.schedule;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

@Configuration
public class SchedulerFactoryConfig {

    private static final String SCHEDULER_THREAD_POOL_EXECUTOR = "schedulerThreadPool";

    @Bean(SCHEDULER_THREAD_POOL_EXECUTOR)
    public ThreadPoolTaskExecutor executor() {
        final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(3);
        executor.setMaxPoolSize(3);
        executor.setThreadNamePrefix("executor");
        executor.initialize();
        return executor;
    }

    @Bean
    public SpringBeanJobFactory jobFactory(ApplicationContext ctx) {
        SpringBeanJobFactory springBeanJobFactory = new SpringBeanJobFactory();
        springBeanJobFactory.setApplicationContext(ctx);
        return springBeanJobFactory;
    }

    @Bean
    public SchedulerFactoryBean schedulerFactory(
            @Qualifier(SCHEDULER_THREAD_POOL_EXECUTOR) ThreadPoolTaskExecutor threadPoolTaskExecutor,
            SpringBeanJobFactory jobFactory
    ) {
        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        factory.setTaskExecutor(threadPoolTaskExecutor);
        factory.setJobFactory(jobFactory);
        return factory;
    }
}
