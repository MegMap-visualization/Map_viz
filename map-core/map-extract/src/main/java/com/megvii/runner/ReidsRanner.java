package com.megvii.runner;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class ReidsRanner implements ApplicationRunner {
    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public void run(ApplicationArguments args) {
        try {
            redisTemplate.delete("uuidSet");
            log.info("Initializing Redis uuidSet");
        }catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
