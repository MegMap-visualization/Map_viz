package com.megvii.mapapi.annotation.aspect;

import com.megvii.exception.InterfaceIdempotentException;
import com.megvii.mapapi.annotation.InterfaceIdempotent;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

@Aspect
@Component
@Slf4j
@Order(10)
public class InterfaceIdempotentAspect {
    private String uniqueStr;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Pointcut(value = "@annotation(interfaceIdempotent)||@within(interfaceIdempotent)")
    public void pointCut(InterfaceIdempotent interfaceIdempotent) {
    }

    @Before(value = "pointCut(interfaceIdempotent)", argNames = "interfaceIdempotent")
    public void doAround(InterfaceIdempotent interfaceIdempotent) {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        String xml = request.getParameter("remark");
        String localAddr = request.getLocalAddr();
        String requestURI = request.getRequestURI();
        uniqueStr = localAddr + ":" + requestURI;
        log.info("用户访问："+uniqueStr+" remark: "+xml);
        boolean uniqueRequestSet = Boolean.TRUE.equals(redisTemplate.opsForSet().isMember("uniqueRequestSet", uniqueStr));
//        log.info(String.valueOf(uniqueRequestSet));
        if (uniqueRequestSet) {
            log.info("用户操作频繁："+uniqueStr);
            throw new InterfaceIdempotentException(requestURI + "操作频繁");
        }
//        log.info("资源访问成功" + uniqueStr);
        redisTemplate.opsForSet().add("uniqueRequestSet", uniqueStr);
    }

    @AfterReturning(value = "pointCut(interfaceIdempotent)", argNames = "interfaceIdempotent")
    public void doAfterReturning(InterfaceIdempotent interfaceIdempotent) {
        log.info("访问结束：" + uniqueStr);
        redisTemplate.opsForSet().remove("uniqueRequestSet", uniqueStr);
    }

    @AfterThrowing(value = "pointCut(interfaceIdempotent)", argNames = "interfaceIdempotent")
    public void doAfterThrowing(InterfaceIdempotent interfaceIdempotent) {
//        log.info("方法异常后" + uniqueStr);
        redisTemplate.opsForSet().remove("uniqueRequestSet", uniqueStr);
    }
}
