package com.bestapp.com.logging.annotation;

import com.bestapp.com.logging.config.PerformanceAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(PerformanceAutoConfiguration.class)
public @interface EnablePerformanceLogging {
}
