package com.bestapp.com.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

import java.util.Arrays;

@Aspect
public class AuditAspect {

    // Определяем точку среза для всех методов, связанных с продуктами
    @Pointcut("execution(* com.bestapp.com.service.impl.ProductServiceImpl.*(..))")
    public void productServiceMethods() {}

    // Логируем действия пользователя до выполнения метода
    @Before("productServiceMethods()")
    public void logBefore(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        // Выводим информацию о том, какой метод был вызван и с какими параметрами
        System.out.println("Audit log: User performed action - " + methodName + " with parameters: " + Arrays.toString(args));
    }

    // Логируем выполнение метода
    @After("productServiceMethods()")
    public void logAfter(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        // Логируем завершение действия
        System.out.println("Audit log: Action " + methodName + " completed successfully.");
    }

}
