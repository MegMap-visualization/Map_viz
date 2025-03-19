package com.megvii.mapapi.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/map/api/v1")
public class HealthController {
    
    @GetMapping("/health")
    public String health() {
        log.info("健康检查接口被调用");
        return "OK";
    }
} 
