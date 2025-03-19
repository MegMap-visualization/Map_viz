package com.megvii.mapapi.annotation.aspect;

import com.alibaba.fastjson.JSON;
import com.megvii.mapapi.annotation.CacheResult;
import com.megvii.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.HashMap;

@Component
@Aspect
@Slf4j
public class CacheResultAspect {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Around(value = "@annotation(cacheResult)", argNames = "pjp,cacheResult")

    public Object doCacheResult(ProceedingJoinPoint pjp, CacheResult cacheResult) throws Throwable {
        String hashkey = cacheResult.redisHashkey();
        String key = Arrays.toString(pjp.getArgs());
        log.info("Try Get From Cache hashKey： " + hashkey + " key: " + key);
        Object cacheRes = redisTemplate.opsForHash().get(hashkey, key);
        if (cacheRes != null) {
            log.info("Finish Get From Cache");
            HashMap<String, Object> resmap = new HashMap<>();
            resmap.put("data", cacheRes);
            return resmap;
        }
        log.info("Failed Get From Cache");
        log.info("Start Exec Method");
        Object proceed = pjp.proceed();
        log.info(JSON.parseObject(proceed.toString()).getString("signals"));
        log.info("Start Cache Result");
        redisTemplate.opsForHash().put(hashkey, key, proceed);
        log.info("Finish Cache hashKey： " + hashkey + " key: " + key);
        log.info("Return Result");
        return proceed;
    }
}
