package com.bestapp.com.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class PerformanceLoggingAspect {

    // Pointcut for all the methods
    @Around("execution(* com.bestapp.com.*.*(..))")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {

        // Getting current time before method execution
        long startTime = System.nanoTime();

        // Method execution
        Object result = joinPoint.proceed();

        // Getting current time after method execution
        long endTime = System.nanoTime();

        // Calculating execution time in milliseconds
        long duration = (endTime - startTime) / 1_000_000; // converting to milliseconds

        // Execution time logging
        System.out.println("Performance log: Method " + joinPoint.getSignature().getName() + " executed in " + duration + " ms");

        return result;
    }

}
