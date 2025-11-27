package com.bestapp.com.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

import java.util.Arrays;

@Aspect
public class AuditAspect {

    @Pointcut("execution(* com.bestapp.com.service.impl.ProductServiceImpl.*(..))")
    public void productServiceMethods() {}

    @Before("productServiceMethods()")
    public void logBefore(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        System.out.println("Audit log: User performed action - " + methodName + " with parameters: " + Arrays.toString(args));
    }

    @After("productServiceMethods()")
    public void logAfter(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        System.out.println("Audit log: Action " + methodName + " completed successfully.");
    }

}
