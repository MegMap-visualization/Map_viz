package com.megvii.mapapi.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.megvii.mapapi.entity.CenterLaneEntity;
import com.megvii.mapapi.mapper.JunctionMapper;
import org.springframework.stereotype.Service;

import com.megvii.mapapi.entity.JunctionEntity;
import com.megvii.mapapi.service.JunctionService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Service
public class JunctionServiceImpl extends ServiceImpl<JunctionMapper, JunctionEntity> implements JunctionService {

    @Override
    public List<JunctionEntity> getEntitesWithPointsInfoFromMap(Map<String, JSONArray> entitys, String xmlUid) {
        ArrayList<JunctionEntity> entities = new ArrayList<>();
        entitys.forEach((uid, v1) -> {
            JunctionEntity entity = this.getInfoByXmlUidAndObjId("junction_id", uid, xmlUid);
            entity.setPoints(v1);
            entities.add(entity);
        });
        return entities;
    }
}