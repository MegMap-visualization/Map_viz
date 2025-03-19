package com.megvii;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class MapGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(MapGatewayApplication.class, args);
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Gateway健康检查完成");  // 这将返回 200 状态码
    }
}