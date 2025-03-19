package com.megvii.mapapi.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.megvii.mapapi.entity.ObjectEntity;
import com.megvii.mapapi.mapper.SignalMapper;
import org.springframework.stereotype.Service;

import com.megvii.mapapi.entity.SignalEntity;
import com.megvii.mapapi.service.SignalService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Service
public class SignalServiceImpl extends ServiceImpl<SignalMapper, SignalEntity> implements SignalService {

    @Override
    public List<SignalEntity> getEntitesWithPointsInfoFromMap(Map<String, JSONArray> entitys, String xmlUid) {
        ArrayList<SignalEntity> entities = new ArrayList<>();
        entitys.forEach((uid, v1) -> {
            SignalEntity entity = this.getInfoByXmlUidAndObjId("signal_id", uid, xmlUid);
            entity.setPoints(v1);
            entities.add(entity);
        });
        return entities;
    }
}