package com.bfms.bfms_backend.config;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    /**
     * Pointcut cho tất cả các phương thức trong package controller
     */
    @Pointcut("within(com.bfms.bfms_backend.controller..*)")
    public void controllerPointcut() {}

    /**
     * Pointcut cho tất cả các phương thức trong package service
     */
    @Pointcut("within(com.bfms.bfms_backend.service.impl..*)")
    public void serviceImplPointcut() {}

    /**
     * Log trước khi xử lý Request tại Controller
     */
    @Before("controllerPointcut()")
    public void logBeforeController(JoinPoint joinPoint) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            log.info(">>> [REQUEST] {} {} | IP: {} | Handler: {}.{}() | Args: {}",
                    request.getMethod(),
                    request.getRequestURI(),
                    request.getRemoteAddr(),
                    joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName(),
                    Arrays.toString(joinPoint.getArgs()));
        }
    }

    /**
     * Log sau khi Controller trả về kết quả thành công
     */
    @AfterReturning(pointcut = "controllerPointcut()", returning = "result")
    public void logAfterController(JoinPoint joinPoint, Object result) {
        log.info("<<< [RESPONSE] {}.{}() | Result: {}",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(),
                result != null ? result.getClass().getSimpleName() : "void");
    }

    /**
     * Log và đo thời gian thực thi tại tầng Service
     */
    @Around("serviceImplPointcut()")
    public Object logServiceExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        
        Object proceed = joinPoint.proceed();
        
        long executionTime = System.currentTimeMillis() - start;
        
        if (executionTime > 500) {
            log.warn("!!! [PERFORMANCE] {}.{}() thực thi chậm: {}ms",
                    joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName(),
                    executionTime);
        } else {
            log.debug("=== [SERVICE] {}.{}() thực thi trong: {}ms",
                    joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName(),
                    executionTime);
        }
        
        return proceed;
    }

    /**
     * Log khi có ngoại lệ xảy ra tại tầng Controller hoặc Service
     */
    @AfterThrowing(pointcut = "controllerPointcut() || serviceImplPointcut()", throwing = "exception")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable exception) {
        log.error("XXX [EXCEPTION] tại {}.{}() | Message: {}",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(),
                exception.getMessage());
    }
}
