package com.megvii.mapapi.annotation.aspect;

import com.megvii.mapapi.annotation.MyStopWatch;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Aspect
@Slf4j
@Order(1)
public class StopWatchAspect {
    @Pointcut(value = "@annotation(myStopWatch) || @within(myStopWatch)")
    public void doStopWatch(MyStopWatch myStopWatch) {
    }

    @Around(value = "doStopWatch(myStopWatch)", argNames = "pjp,myStopWatch")
    public Object doAround(ProceedingJoinPoint pjp, MyStopWatch myStopWatch) throws Throwable {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start(pjp.getSignature().getName());
        Object proceed = pjp.proceed();
        stopWatch.stop();
        List<String> argsList = Arrays.stream(pjp.getArgs()).map(String::valueOf).collect(Collectors.toList());
        log.info("InterfaceName: " + pjp.getSignature().getName() + "  Args: " + argsList + "  TotalTime: " + stopWatch.getTotalTimeSeconds());
        return proceed;
    }
}
