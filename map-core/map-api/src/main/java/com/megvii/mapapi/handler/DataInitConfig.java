package com.megvii.mapapi.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@Slf4j
public class DataInitConfig {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Value("${app.version}")
    private String version;

    @PostConstruct
    public void init() {
        try {
            log.info("初始化项目版本...");
            redisTemplate.opsForValue().set("securityCode", version);
        }catch (Exception e) {
            log.error(e.getMessage());
        }
    }

}
