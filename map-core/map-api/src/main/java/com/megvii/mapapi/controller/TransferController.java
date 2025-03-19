package com.megvii.mapapi.controller;

import com.megvii.utils.R;
import com.megvii.mapapi.annotation.RecordVisitNum;
import com.megvii.mapapi.feginclient.TransferClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 转换接口
 */
@RestController
@RequestMapping("/map/api/v1/transfer")
@RecordVisitNum
public class TransferController {

    @Autowired
    TransferClient transferClient;

    /**
     * wgs转utm
     * @param lon 经度
     * @param lat 纬度
     * @return
     */
    @GetMapping("/wgs2utm")
    public R wgs2utm(@RequestParam("lon") String lon, @RequestParam("lat") String lat) {
        String[] utmXy = transferClient.wgs2utm(lon, lat).split(",");
        return R.ok().put("data", utmXy);
    }

    /**
     * utm转wgs
     * @param x utm_x
     * @param y utm_y
     * @param utm_id utm分区id
     * @return
     */
    @GetMapping("/utm2wgs")
    public R utm2wgs(@RequestParam("x") String x, @RequestParam("y") String y, @RequestParam("utm_id") Integer utm_id) {
        String[] wgsLonLat = transferClient.utm2wgs(x, y, utm_id).split(",");
        return R.ok().put("data", wgsLonLat);
    }
}
