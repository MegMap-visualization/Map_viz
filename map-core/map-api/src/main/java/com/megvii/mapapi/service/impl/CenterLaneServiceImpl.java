package com.megvii.mapapi.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.megvii.mapapi.entity.LaneEntity;
import com.megvii.mapapi.mapper.CenterLaneMapper;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.megvii.mapapi.entity.CenterLaneEntity;
import com.megvii.mapapi.service.CenterLaneService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class CenterLaneServiceImpl extends ServiceImpl<CenterLaneMapper, CenterLaneEntity> implements CenterLaneService {

    @Override
    public List<CenterLaneEntity> getEntitesWithPointsInfoFromMap(Map<String, JSONArray> entitys, String xmlUid) {
        ArrayList<CenterLaneEntity> entities = new ArrayList<>();
        entitys.forEach((uid, v1) -> {
            CenterLaneEntity entity = this.getInfoByXmlUidAndObjId("lane_uid", uid, xmlUid);
            entity.setPoints(v1);
            entities.add(entity);
        });
        return entities;
    }
}