package com.bestapp.com.audit.aspect;

import com.bestapp.com.audit.model.AuditLog;
import com.bestapp.com.audit.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;

@Aspect
@Component
@RequiredArgsConstructor
public class AuditAspect {

    private final AuditLogService auditLogService;

    @Pointcut("execution(* com.bestapp.com.service.impl.ProductServiceImpl.*(..))")
    public void productServiceMethods() {}

    @Before("productServiceMethods()")
    public void logBefore(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        String parameters = Arrays.toString(args);
        String username = getCurrentUsername();
        LocalDateTime timestamp = LocalDateTime.now();
        AuditLog auditLog = new AuditLog(username, methodName, parameters, timestamp);
        auditLogService.saveAuditLog(auditLog);
        System.out.println("Audit log saved: User " + username + " performed action - " + methodName + " with parameters: " + parameters);

    }

    @After("productServiceMethods()")
    public void logAfter(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        String username = getCurrentUsername();
        LocalDateTime timestamp = LocalDateTime.now();
        AuditLog auditLog = new AuditLog(username, methodName, "N/A", timestamp);
        auditLogService.saveAuditLog(auditLog);
        System.out.println("Audit log saved: Action " + methodName + " completed successfully.");
    }

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return "unknown_user";
    }

}
