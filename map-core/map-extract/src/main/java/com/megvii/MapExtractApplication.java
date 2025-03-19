package com.megvii;

import com.megvii.config.ElasticSearchConfig;
import com.megvii.properties.AwsS3Properties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@SpringBootApplication
@EnableDiscoveryClient
@EnableConfigurationProperties({AwsS3Properties.class, ElasticSearchConfig.class})
@RestController
public class MapExtractApplication {
    public static void main(String[] args) {
        SpringApplication.run(MapExtractApplication.class, args);
    }

    @GetMapping("/map/extract/v1/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Extract健康检查完成");
    }
}
