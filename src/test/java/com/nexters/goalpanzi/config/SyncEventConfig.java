package com.nexters.goalpanzi.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.scheduling.annotation.AsyncConfigurerSupport;

import java.util.concurrent.Executor;

@TestConfiguration
public class SyncEventConfig extends AsyncConfigurerSupport {

    @Override
    public Executor getAsyncExecutor() {
        return new SyncTaskExecutor();
    }
}
