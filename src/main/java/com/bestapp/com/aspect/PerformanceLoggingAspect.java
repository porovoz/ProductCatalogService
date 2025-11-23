package com.bestapp.com.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class PerformanceLoggingAspect {

    // Точка среза для всех методов
    @Around("execution(* com.bestapp.com.*.*(..))")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {

        // Получаем текущее время до выполнения метода
        long startTime = System.nanoTime();

        // Выполнение метода
        Object result = joinPoint.proceed();

        // Получаем текущее время после выполнения метода
        long endTime = System.nanoTime();

        // Вычисляем время выполнения в миллисекундах
        long duration = (endTime - startTime) / 1_000_000; // переводим в миллисекунды

        // Логируем время выполнения
        System.out.println("Performance log: Method " + joinPoint.getSignature().getName() + " executed in " + duration + " ms");

        return result;
    }

}
