package com.megvii.mapapi.annotation.aspect;

import com.megvii.mapapi.annotation.AdminAuthVerify;
import com.megvii.mapapi.annotation.RecordVisitNum;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

@Component
@Aspect
@Slf4j
@Order(20)
public class RecordVisitNumAspect {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Pointcut(value = "@annotation(recordVisitNum) || @within(recordVisitNum)")
    public void doRecordVisitNum(RecordVisitNum recordVisitNum) {
    }

    @Before(value = "doRecordVisitNum(recordVisitNum)", argNames = "recordVisitNum")
    public void doBefore(RecordVisitNum recordVisitNum) {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        String requestURI = request.getRequestURI();
//        log.info("记录："+requestURI);
        if (!redisTemplate.opsForHash().hasKey("visitNum", requestURI))
            redisTemplate.opsForHash().put("visitNum", requestURI, "1");
        redisTemplate.opsForHash().increment("visitNum", requestURI, 1);
    }
}
