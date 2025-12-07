package com.bestapp.com.audit.config;

import com.bestapp.com.audit.aspect.AuditAspect;
import com.bestapp.com.audit.service.AuditLogService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuditAutoConfiguration {

    @Bean
    public AuditAspect auditAspect(AuditLogService auditLogService) {
        return new AuditAspect(auditLogService);
    }
}
