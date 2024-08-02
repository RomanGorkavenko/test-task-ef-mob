package ru.effectivemobile.testtask.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.event.Level;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Класс выводит информацию о методе, там где стоит аннотация {@link Loggable}.
 */
@Slf4j
@Aspect
@Component
public class LoggableAspect {

    @Pointcut("within(@ru.effectivemobile.testtask.aspect.Loggable *)")
    public void beansAnnotatedWith() {
    }

    @Pointcut("@annotation(ru.effectivemobile.testtask.aspect.Loggable)")
    public void methodsAnnotatedWith() {
    }

    @Around("beansAnnotatedWith() || methodsAnnotatedWith()")
    public Object loggableAspect(ProceedingJoinPoint joinPoint) throws Throwable {
        Level level = extractLevel(joinPoint);

        log.atLevel(level).log("target = {}", joinPoint.getTarget().getClass());
        log.atLevel(level).log("method = {}", joinPoint.getSignature().getName());
        log.atLevel(level).log("args = {}", Arrays.toString(joinPoint.getArgs()));
        try {
            Object returnValue = joinPoint.proceed();
            log.atLevel(level).log("result = {}", returnValue);
            return returnValue;
        } catch (Throwable e) {
            log.atLevel(level).log("exception = [{}, {}]", e.getClass(), e.getMessage());
            throw e;
        }
    }

    private Level extractLevel(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Loggable annotation = signature.getMethod().getAnnotation(Loggable.class);
        if (annotation != null) {
            return annotation.level();
        }

        return joinPoint.getTarget().getClass().getAnnotation(Loggable.class).level();
    }

}
