package com.megvii.mapapi.controller;

import com.megvii.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/map/api/v1/position")
public class RosController {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;


    @GetMapping("/send")
    public Integer sendPosition(@RequestParam("lonLat") String latLon, @RequestParam("name") String name) {
        redisTemplate.opsForValue().set("carPosition:" + name, name + ";" + latLon);
        return 200;
    }

    @GetMapping("/get")
    public R getPosition() {
        Set<String> keys = redisTemplate.keys("carPosition:*");
        if (keys == null) {
            return R.ok().put("data", null);
        }
        return R.ok().put("data", redisTemplate.opsForValue().multiGet(keys));
    }
}
