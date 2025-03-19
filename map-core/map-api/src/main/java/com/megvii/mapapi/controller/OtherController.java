package com.megvii.mapapi.controller;

import com.megvii.utils.R;
import com.megvii.mapapi.annotation.InterfaceIdempotent;
import com.megvii.mapapi.properties.SystemOtherProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 其他接口
 */
@RestController
@RequestMapping("/map/api/v1/other")
public class OtherController {


    @Resource
    private SystemOtherProperties systemOtherProperties;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 获取当前系统接口的访问信息
     * @return
     */
//    @InterfaceIdempotent
    @GetMapping("/getVisitNum")
    public R getVisitNum() {
        Map<Object, Object> visitNum = redisTemplate.opsForHash().entries("visitNum");
        return R.ok().put("data", visitNum);
    }

    /**
     * 获取访问接口的所有数量
     * @return
     */
    @GetMapping("/getVisitNumCount")
    public R getVisitNumCount() {
        long visitNumCount = redisTemplate.opsForHash().entries("visitNum").values().stream()
                .collect(Collectors.summarizingLong(num -> Long.parseLong(String.valueOf(num)))).getSum();
        return R.ok().put("data", visitNumCount);
    }

    /**
     * 获取前端绘制地图的密钥
     * @return
     */
    @GetMapping("/securityJsCode")
    public R securityJsCode() {
        return R.ok().put("data", systemOtherProperties.getSecurityJsCode());
    }
}
