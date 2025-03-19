package com.megvii.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.megvii.elamapper.ObjPointsMapper;
import com.megvii.elamapper.PointDocMapper;
import com.megvii.entity.*;
import com.megvii.mapper.XmlMapper;
import com.megvii.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class XmlServiceImpl extends ServiceImpl<XmlMapper, XmlEntity> implements XmlService {
    @Resource
    public ObjectService objectService;
    @Resource
    protected PointService pointService;
    @Resource
    protected LaneService laneService;
    @Resource
    protected SignalService signalService;
    @Resource
    protected JunctionService junctionService;
    @Autowired
    private PointDocMapper pointDocMapper;
    @Resource
    private ObjPointsMapper objPointsMapper;

    @Override
    @Transactional
    public void delByRemark(String remark) {
        baseMapper.selectList(new QueryWrapper<XmlEntity>().eq("remark", remark)).forEach(xmlEntity -> {
            String xmlUid = xmlEntity.getXmlUid();
            log.info("Start Del MySql Data xmlUid:" + xmlUid);
            this.remove(new QueryWrapper<XmlEntity>().eq("xml_uid", xmlUid));
            laneService.remove(new QueryWrapper<LaneEntity>().eq("xml_uid", xmlUid));
            signalService.remove(new QueryWrapper<SignalEntity>().eq("xml_uid", xmlUid));
            junctionService.remove(new QueryWrapper<JunctionEntity>().eq("xml_uid", xmlUid));
            objectService.remove(new QueryWrapper<ObjectEntity>().eq("xml_uid", xmlUid));
            pointService.remove(new QueryWrapper<PointEntity>().eq("xml_uid", xmlUid));
            log.info("Start Del Es Data xmlUid:" + xmlUid);
            try {
                pointDocMapper.delByKey("xmlUid", xmlUid);
                objPointsMapper.delByKey("xmlUid", xmlUid);
            } catch (IOException e) {
                e.printStackTrace();
                log.info("Del Es Data Error !!!" + xmlUid);
            }
        });
    }
}