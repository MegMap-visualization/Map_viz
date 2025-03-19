package com.megvii.mapapi.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.megvii.mapapi.annotation.CacheResult;
import com.megvii.mapapi.annotation.MyStopWatch;
import com.megvii.mapapi.entity.XmlEntity;
import com.megvii.utils.R;
import com.megvii.mapapi.annotation.InterfaceIdempotent;
import com.megvii.mapapi.annotation.RecordVisitNum;
import com.megvii.mapapi.entity.LaneEntity;
import com.megvii.mapapi.service.LaneService;
import com.megvii.mapapi.service.XmlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 车道相关接口
 */
@Slf4j
@RestController
@RequestMapping("/map/api/v1")
@RecordVisitNum
@MyStopWatch
public class LaneController {


    @Autowired
    private LaneService laneService;
    @Autowired
    private XmlService xmlService;

//    @Resource
//    private TransformUtil transformUtil;

    /**
     * 根据remark获取xml相关信息
     * @param remark
     * @return
     */
    @GetMapping("/getMd5ByRemark")
    public R getMd5Byremark(@RequestParam("remark") String remark) {
        XmlEntity xmlEntity = xmlService.getOne(new QueryWrapper<XmlEntity>().eq("remark", remark));
        return R.ok().put("data", xmlEntity);
    }

    /**
     * 通过指定坐标局部搜索
     *
     * @return
     */
    @GetMapping("/getInfoByCoordinate")
    public R getInfoByCoordinate(@RequestParam("ltLonLat") String ltLonLat, @RequestParam("rbLonLat") String rbLonlat, @RequestParam("remark") String remark) throws IOException {
        List<Double> ltLonLatList = Arrays.stream(ltLonLat.split(",")).map(Double::new).collect(Collectors.toList());
        List<Double> rbLonlatList = Arrays.stream(rbLonlat.split(",")).map(Double::new).collect(Collectors.toList());
        return R.ok().put("data", laneService.getLaneListByCoordinate(ltLonLatList, rbLonlatList, remark));
    }

    /**
     * 通过xml获取地图数据
     *
     * @param remark xml的别名
     * @return
     * @throws InterruptedException
     */
//    @InterfaceIdempotent
    @GetMapping("/getInfoByXml")
    public R getInfoByXml(@RequestParam("remark") String remark) throws IOException {
        return R.ok().put("data", laneService.getLaneListByXml(remark));
    }

    /**
     * 获取文件别名列表
     *
     * @return
     */
    @GetMapping("/getXmlList")
    public R getXmlList() {
        log.info("getXmlList 接口被调用");
        return R.ok().put("data", xmlService.getXmlList());
    }

    /**
     * 通过lane的id获取lane的其他信息
     *
     * @param id laneId
     * @return
     */
    @GetMapping("/getOtherInfo")
    public R getOtherInfo(@RequestParam Long id) {
        LaneEntity laneEntity = laneService.getOtherInfoById(id);
        return R.ok().put("data", laneEntity);
    }

//    @InterfaceIdempotent
    @GetMapping("/getAllCenterLaneByRemark")
    public R getAllCenterLane(@RequestParam("remark") String remark) {
        return R.ok().put("data", laneService.getAllRefLane(remark));
    }

    /**
     * 通过uid获取局部车道信息
     *
     * @param uids 多个uid分号隔开
     * @return
     */
    @GetMapping("/getPartLaneByUids")
    public R getPartLaneByUid(@RequestParam("uids") String uids, @RequestParam("remark") String remark) {
        return R.ok().put("data", laneService.getPartLaneByUid(uids, remark));
    }
}
