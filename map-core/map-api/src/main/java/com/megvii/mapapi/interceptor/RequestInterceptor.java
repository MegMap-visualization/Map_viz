package com.megvii.mapapi.interceptor;

import com.megvii.exception.StopException;
import com.megvii.mapapi.properties.SystemOtherProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class RequestInterceptor implements HandlerInterceptor {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Resource
    private SystemOtherProperties systemOtherProperties;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = request.getRequestURI();
        
        // 健康检查接口直接放行
        if (path.contains("/map/api/v1/health")) {
            return true;
        }
        String securitycode = request.getHeader("securitycode");
        String securityCode = redisTemplate.opsForValue().get("securityCode");
//        log.info("allow: "+systemOtherProperties.isAllow());
        if (securitycode != null && securitycode.equals(securityCode))
            return true;
        if (systemOtherProperties.isAllow()) {
            return true;
        } else {
            throw new StopException("版本升级，暂停使用！！！");
        }
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {

    }
}
