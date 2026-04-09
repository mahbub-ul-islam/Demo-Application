package com.example.demo.logging;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.UUID;


@Aspect
@Component
@Slf4j
public class LoggingAspect {

    private static final String TRACE_ID_KEY = "traceId";


    @Around("execution(* com.example.demo..controller..*(..))")
    public Object logRequestResponse(ProceedingJoinPoint joinPoint) throws Throwable {
        String traceId = MDC.get(TRACE_ID_KEY);
        if (traceId == null) {
            traceId = UUID.randomUUID().toString();
            MDC.put(TRACE_ID_KEY, traceId);
        }

        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        Object[] args = joinPoint.getArgs();

        log.info("[CONTROLLER_START] Class: {}, Method: {}, Args: {}, TraceId: {}", className, methodName, args, traceId);

        long start = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            long duration = System.currentTimeMillis() - start;
            log.info("[CONTROLLER_SUCCESS] Class: {}, Method: {}, Duration: {}ms, TraceId: {}", className, methodName, duration, traceId);
            return result;
        } catch (Exception e) {
            log.error("[CONTROLLER_ERROR] Method: {}, Error: {}, TraceId: {}", methodName, e.getMessage(), traceId);
            throw e;
        }
    }


    @Around("execution(* com.example.demo..service..*(..))")
    public Object logServiceOperations(ProceedingJoinPoint joinPoint) throws Throwable {
        String traceId = MDC.get(TRACE_ID_KEY);
        if (traceId == null) {
            traceId = UUID.randomUUID().toString();
            MDC.put(TRACE_ID_KEY, traceId);
        }

        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        Object[] args = joinPoint.getArgs();

        log.info("[SERVICE_START] Class: {}, Method: {}, Args: {}, TraceId: {}", className, methodName, args, traceId);

        long start = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            long duration = System.currentTimeMillis() - start;
            log.info("[SERVICE_SUCCESS] Class: {}, Method: {}, Duration: {}ms, TraceId: {}", className, methodName, duration, traceId);
            return result;
        } catch (Exception e) {
            log.error("[SERVICE_ERROR] Method: {}, Error: {}, TraceId: {}", methodName, e.getMessage(), traceId);
            throw e;
        }
    }


    @Around("execution(* com.example.demo..repository..*(..))")
    public Object logRepositoryOperations(ProceedingJoinPoint joinPoint) throws Throwable {
        String traceId = MDC.get(TRACE_ID_KEY);
        if (traceId == null) {
            traceId = UUID.randomUUID().toString();
            MDC.put(TRACE_ID_KEY, traceId);
        }

        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        log.debug("[REPOSITORY_START] Class: {}, Method: {}, TraceId: {}", className, methodName, traceId);

        long start = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            long duration = System.currentTimeMillis() - start;
            log.debug("[REPOSITORY_SUCCESS] Class: {}, Method: {}, Duration: {}ms, TraceId: {}", className, methodName, duration, traceId);
            return result;
        } catch (Exception e) {
            log.error("[REPOSITORY_ERROR] Method: {}, Error: {}, TraceId: {}", methodName, e.getMessage(), traceId);
            throw e;
        }
    }
}
