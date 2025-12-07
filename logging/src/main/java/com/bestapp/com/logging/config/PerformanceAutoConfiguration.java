package com.bestapp.com.logging.config;

import com.bestapp.com.logging.aspect.PerformanceLoggingAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PerformanceAutoConfiguration {

    @Bean
    public PerformanceLoggingAspect performanceLoggingAspect() {
        return new PerformanceLoggingAspect();
    }
}
