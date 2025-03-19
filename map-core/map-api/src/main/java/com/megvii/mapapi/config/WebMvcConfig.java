package com.megvii.mapapi.config;

import com.megvii.mapapi.interceptor.RequestInterceptor;
import com.megvii.mapapi.properties.SystemOtherProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private SystemOtherProperties systemOtherProperties;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(getRequestInterceptor())
                .excludePathPatterns("/admin/**")
                .excludePathPatterns("/**/*.js")
                .excludePathPatterns("/**/*.html")
                .excludePathPatterns("/**/*.css");
//                .excludePathPatterns("/PTypeOper/getByTypeId")
//                .excludePathPatterns("/ProductOper/Sku/skuListByProductIdent")
//                .excludePathPatterns("/applet/**");
    }

    @Bean
    public RequestInterceptor getRequestInterceptor() {
        return new RequestInterceptor();
    }

}



