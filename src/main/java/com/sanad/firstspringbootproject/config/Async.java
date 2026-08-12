package com.sanad.firstspringbootproject.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class Async {

    @Bean("operationExecutor")
    public ThreadPoolTaskExecutor operationExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(50);
        executor.setMaxPoolSize(55);
        executor.setQueueCapacity(50);

        executor.setThreadNamePrefix("operation-worker-");

        return executor;
    }
}
