package com.alexnerd.excelloader.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class AppConfiguration {

    @Value("${app.integration.url}")
    private String url;

    @Value("${app.workers-number}")
    private int workersNumber;

    @Bean
    public WebClient getWebClient(WebClient.Builder builder) {
        return builder.baseUrl(url)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Bean
    public ThreadPoolTaskExecutor taskThreadPoolExecutor() {
        ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();
        pool.setCorePoolSize(workersNumber);
        pool.setWaitForTasksToCompleteOnShutdown(true);
        return pool;
    }
}
