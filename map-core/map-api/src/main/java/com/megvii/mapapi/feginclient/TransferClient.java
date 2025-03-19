package com.megvii.mapapi.feginclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name="map-python-services",path = "/coordinate-transformation")
public interface TransferClient {
    @GetMapping("/wgs2utm")
    String wgs2utm(@RequestParam("lon") String lon, @RequestParam("lat") String lat);

    @GetMapping("/utm2wgs")
    String utm2wgs(@RequestParam("x") String x, @RequestParam("y") String y, @RequestParam("utm_id") Integer utm_id);
}
