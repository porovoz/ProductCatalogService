package com.bestapp.com.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

import java.util.Arrays;

@Aspect
public class AuditAspect {

    // Defining pointcut for all product-related methods
    @Pointcut("execution(* com.bestapp.com.service.impl.ProductServiceImpl.*(..))")
    public void productServiceMethods() {}

    // Logging user actions before method execution
    @Before("productServiceMethods()")
    public void logBefore(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        // Выводим информацию о том, какой метод был вызван и с какими параметрами
        System.out.println("Audit log: User performed action - " + methodName + " with parameters: " + Arrays.toString(args));
    }

    // Method execution logging
    @After("productServiceMethods()")
    public void logAfter(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        // Completing action logging
        System.out.println("Audit log: Action " + methodName + " completed successfully.");
    }

}
