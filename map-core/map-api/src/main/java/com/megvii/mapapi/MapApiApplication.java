package com.megvii.mapapi;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataAutoConfiguration;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchRestClientAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(exclude = {
    ElasticsearchRestClientAutoConfiguration.class,
    ElasticsearchDataAutoConfiguration.class
})
@EnableFeignClients(basePackages = "com.megvii.mapapi")
@MapperScan("com.megvii.mapapi.mapper")
@ComponentScan(basePackages = "com.megvii")
public class MapApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(MapApiApplication.class, args);
    }
}
