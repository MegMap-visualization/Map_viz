package com.megvii.mapapi.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.megvii.mapapi.entity.CenterLaneEntity;
import com.megvii.mapapi.entity.ObjectEntity;
import com.megvii.mapapi.mapper.ObjectMapper;
import com.megvii.mapapi.service.ObjectService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Service
public class ObjectServiceImpl extends ServiceImpl<ObjectMapper, ObjectEntity> implements ObjectService {

    @Override
    public List<ObjectEntity> getEntitesWithPointsInfoFromMap(Map<String, JSONArray> entitys, String xmlUid) {
        ArrayList<ObjectEntity> entities = new ArrayList<>();
        entitys.forEach((uid, v1) -> {
            ObjectEntity entity = this.getInfoByXmlUidAndObjId("object_id", uid, xmlUid);
            entity.setPoints(v1);
            entities.add(entity);
        });
        return entities;
    }
}