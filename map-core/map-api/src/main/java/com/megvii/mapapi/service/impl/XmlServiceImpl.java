package com.megvii.mapapi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.megvii.mapapi.mapper.XmlMapper;
import com.megvii.mapapi.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.megvii.mapapi.entity.XmlEntity;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class XmlServiceImpl extends ServiceImpl<XmlMapper, XmlEntity> implements XmlService {
    @Autowired
    public ObjectService objectService;
    @Autowired
    protected PointService pointService;
//    @Autowired
//    protected LaneService laneService;
    @Autowired
    protected SignalService signalService;
    @Autowired
    protected JunctionService junctionService;

    @Override
    public Set<String> getXmlList() {
        return baseMapper.selectList(null).stream().map(XmlEntity::getRemark).collect(Collectors.toSet());
    }


}