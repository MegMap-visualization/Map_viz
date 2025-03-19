package com.megvii.mapapi.service;

import com.megvii.mapapi.entity.LaneEntity;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * @author LingYifeng
 * @email lingyifeng@megvii.com
 */


public interface LaneService extends BaseService<LaneEntity> {
    List<LaneEntity> GetEntityList(String column, String val);

    HashMap<String, List<?>> getLaneListByCoordinate(List<Double> ltLonLat, List<Double> rbLonlat, String remark) throws IOException;

    HashMap<String, List<?>> getAllLane(String remark);

    HashMap<String, List<?>> getLaneListByXml(String xml);

    HashMap<String, List<?>> getAllRefLane(String remark);

    HashMap<String, Object> getPartLaneByUid(String uid, String remark);

    LaneEntity getOtherInfoById(Long id);

    HashMap<String, List<?>> getAllCenterRefLaneAndOther(String remark);
}