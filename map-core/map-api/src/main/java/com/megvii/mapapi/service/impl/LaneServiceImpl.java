package com.megvii.mapapi.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.megvii.doc.ObjPointsDoc;
import com.megvii.elamapper.ObjPointsMapper;
import com.megvii.mapapi.annotation.CacheResult;
import com.megvii.mapapi.entity.*;
import com.megvii.mapapi.mapper.LaneMapper;
import com.megvii.mapapi.properties.SystemOtherProperties;
import com.megvii.mapapi.service.*;
import com.megvii.mapapi.utils.SpringUtils;
import com.megvii.mapapi.vo.LeftBorder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;

@Service
public class LaneServiceImpl extends ServiceImpl<LaneMapper, LaneEntity> implements LaneService {

    @Resource
    private SystemOtherProperties systemOtherProperties;
    @Autowired
    private ObjectService objectService;
    @Autowired
    private XmlService xmlService;
//    @Autowired
//    private LaneService laneService;
    @Autowired
    private SignalService signalService;
    @Autowired
    private JunctionService junctionService;
    @Autowired
    private CenterLaneService centerLaneService;
    @Resource
    private EsPointDocService esPointDocService;

    @Override
    public List<LaneEntity> GetEntityList(String column, String val) {
        return baseMapper.selectList(new QueryWrapper<LaneEntity>().eq(column, val)
                .select("id", "uid", "left_uid", "right_uid", "suc", "pre", "signal_overlap_ids", "object_overlap_ids", "lane_id", "color", "lane_border_type", "is_virtual"));
    }

    @Override
    public HashMap<String, List<?>> getAllCenterRefLaneAndOther(String remark) {
        XmlEntity xmlEntity = xmlService.getOne(new QueryWrapper<XmlEntity>().eq("remark", remark));
        String xmlUid = xmlEntity.getXmlUid();
        HashMap<String, List<String>> reqMap = new HashMap<>();
        reqMap.put("xmlUid", List.of(xmlUid));
        reqMap.put("type", List.of("junction", "object", "signal"));
        HashMap<String, List<?>> resMap = new HashMap<>();
        try {
            Map<String, Map<String, JSONArray>> searchByKeysTerms = esPointDocService.searchByKeysTerms(reqMap);
            searchByKeysTerms.forEach((type, v) -> {
                if (type.equals("junction")) {
                    List<JunctionEntity> entites = junctionService.getEntitesWithPointsInfoFromMap(v, xmlUid);
                    resMap.put("junctions", entites);
                } else if (type.equals("object")) {
                    List<ObjectEntity> entites = objectService.getEntitesWithPointsInfoFromMap(v, xmlUid);
                    resMap.put("objects", entites);
                } else if (type.equals("signal")) {
                    List<SignalEntity> entites = signalService.getEntitesWithPointsInfoFromMap(v, xmlUid);
                    resMap.put("signals", entites);
                }
            });
            reqMap.put("type", List.of("lane"));
            reqMap.put("refLane", List.of("true"));

            Map<String, Map<String, JSONArray>> searchLaneByKeysTerms = esPointDocService.searchByKeysTerms(reqMap);
            Map<String, JSONArray> lanes = searchLaneByKeysTerms.get("lane");
            List<LaneEntity> entites = getEntitesWithPointsInfoFromMap(lanes, xmlUid);
            resMap.put("lanes", entites);
            resMap.put("xml", List.of(xmlEntity));
            return resMap;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    @Override
    public HashMap<String, List<?>> getLaneListByCoordinate(List<Double> ltLonLat, List<Double> rbLonlat, String remark) throws IOException {
        XmlEntity xmlEntity = xmlService.getOne(new QueryWrapper<XmlEntity>().eq("remark", remark));
        String xmlUid = xmlEntity.getXmlUid();
        HashMap<String, List<?>> resMap = new HashMap<>();

        HashMap<String, List<String>> reqMap = new HashMap<>();
        reqMap.put("xmlUid", List.of(xmlUid));
        reqMap.put("type", List.of("lane", "centerLane","leftBorder"));
        reqMap.put("refLane", List.of("false"));
        Map<String, Map<String, JSONArray>> searchLaneByKeysTerms = esPointDocService.searchByKeyAndBoundingBox(reqMap, ltLonLat, rbLonlat);
        Map<String, JSONArray> lanes = searchLaneByKeysTerms.get("lane");
        Map<String, JSONArray> centerLanes = searchLaneByKeysTerms.get("centerLane");
        List<LaneEntity> entites = getEntitesWithPointsInfoFromMap(lanes, xmlUid);
        List<CenterLaneEntity> centerLaneEntities = centerLaneService.getEntitesWithPointsInfoFromMap(centerLanes, xmlUid);
        Map<String, JSONArray> leftBorder = searchLaneByKeysTerms.get("leftBorder");
        List<LeftBorder> leftBorderEntities = getBorderEntitesWithPointsInfoFromMap(leftBorder);
        resMap.put("leftBorders", leftBorderEntities);
        resMap.put("lanes", entites);
        resMap.put("centerLanes", centerLaneEntities);
        return resMap;

    }

    @Override
    public HashMap<String, List<?>> getAllLane(String remark) {
        XmlEntity xmlEntity = xmlService.getOne(new QueryWrapper<XmlEntity>().eq("remark", remark));
        String xmlUid = xmlEntity.getXmlUid();
        HashMap<String, List<?>> resMap = new HashMap<>();
        try {
            HashMap<String, List<String>> reqMap = new HashMap<>();
            reqMap.put("xmlUid", List.of(xmlUid));
            reqMap.put("type", List.of("lane","leftBorder"));
            reqMap.put("refLane", List.of("false"));
            Map<String, Map<String, JSONArray>> searchLaneByKeysTerms = esPointDocService.searchByKeysTerms(reqMap);
            Map<String, JSONArray> lanes = searchLaneByKeysTerms.get("lane");
            List<LaneEntity> entites = getEntitesWithPointsInfoFromMap(lanes, xmlUid);

            Map<String, JSONArray> leftBorder = searchLaneByKeysTerms.get("leftBorder");
            List<LeftBorder> leftBorderEntities = getBorderEntitesWithPointsInfoFromMap(leftBorder);
            resMap.put("leftBorders", leftBorderEntities);
            resMap.put("lanes", entites);
            return resMap;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public HashMap<String, List<?>> getLaneListByXml(String remark) {
        XmlEntity xmlEntity = xmlService.getOne(new QueryWrapper<XmlEntity>().eq("remark", remark));
        String xmlUid = xmlEntity.getXmlUid();
        List<LaneEntity> xmlLanes = baseGetEntityList("xml_uid", xmlUid);
        HashMap<String, List<?>> resMap = new HashMap<>();
        HashMap<String, List<String>> reqMap = new HashMap<>();
        reqMap.put("xmlUid", List.of(xmlUid));
        reqMap.put("type", List.of("lane"));
        reqMap.put("refLane", List.of("false"));
        try {
            Map<String, Map<String, JSONArray>> searchLaneByKeysTerms = esPointDocService.searchByKeysTerms(reqMap);
            List<LaneEntity> entites = getEntitesWithPointsInfoFromMap(searchLaneByKeysTerms.get("lane"), xmlUid);
            resMap.put("lanes", entites);
            return resMap;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Resource
    private ObjPointsMapper objPointsMapper;

    private JSONArray getPointsFromEx(String xmlUid, String objUid) {
        HashMap<String, List<String>> reqMap = new HashMap<>();
        reqMap.put("objUid", List.of(objUid));
        reqMap.put("xmlUid", List.of(xmlUid));
        ArrayList<ObjPointsDoc> objPointsDocs = null;
        try {
            objPointsDocs = objPointsMapper.searchByKeysTerms(reqMap);
            return objPointsDocs.get(0).getPoints();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public HashMap<String, List<?>> getAllRefLane(String remark) {
        XmlEntity xmlEntity = xmlService.getOne(new QueryWrapper<XmlEntity>().eq("remark", remark));
        String xmlUid = xmlEntity.getXmlUid();
        HashMap<String, List<?>> resMap = new HashMap<>();
        List<SignalEntity> entities = signalService.getBaseMapper().selectList(new QueryWrapper<SignalEntity>()
                        .eq("xml_uid", xmlUid))
                .parallelStream().peek(signalEntity -> {
                    JSONArray pointsFromEx = getPointsFromEx(xmlUid, signalEntity.getSignalId());
                    signalEntity.setPoints(pointsFromEx);
                }).collect(Collectors.toList());
        resMap.put("signals", entities);
        List<JunctionEntity> junctionEntities = junctionService.getBaseMapper().selectList(new QueryWrapper<JunctionEntity>()
                        .eq("xml_uid", xmlUid))
                .parallelStream().peek(entity -> {
                    JSONArray pointsFromEx = getPointsFromEx(xmlUid, entity.getJunctionId());
                    entity.setPoints(pointsFromEx);
                }).collect(Collectors.toList());
        resMap.put("junctions", junctionEntities);

        List<ObjectEntity> objectEntities = objectService.getBaseMapper().selectList(new QueryWrapper<ObjectEntity>()
                        .eq("xml_uid", xmlUid))
                .parallelStream().peek(entity -> {
                    JSONArray pointsFromEx = getPointsFromEx(xmlUid, entity.getObjectId());
                    entity.setPoints(pointsFromEx);
                }).collect(Collectors.toList());
        resMap.put("objects", objectEntities);

        List<LaneEntity> laneEntities = getBaseMapper().selectList(new QueryWrapper<LaneEntity>()
                        .eq("xml_uid", xmlUid)
                        .eq("lane_id", 0))
                .stream().peek(entity -> {
                    JSONArray pointsFromEx = getPointsFromEx(xmlUid, entity.getUid());
                    entity.setPoints(pointsFromEx);
                }).collect(Collectors.toList());
        resMap.put("xml", List.of(xmlEntity));
        resMap.put("lanes", laneEntities);
        return resMap;
    }

    @Override
    public HashMap<String, Object> getPartLaneByUid(String uids, String remark) {
        XmlEntity xmlEntity = xmlService.getOne(new QueryWrapper<XmlEntity>().eq("remark", remark));
        HashMap<String, List<String>> reqMap = new HashMap<>();
        reqMap.put("xmlUid", List.of(xmlEntity.getXmlUid()));
        reqMap.put("type", List.of("lane"));
        reqMap.put("objUid", List.of(uids.split(";")));
        reqMap.put("refLane", List.of("false"));
        try {
            Map<String, Map<String, JSONArray>> searchLaneByKeysTerms = esPointDocService.searchByKeysTerms(reqMap);
            List<LaneEntity> entites = getEntitesWithPointsInfoFromMap(searchLaneByKeysTerms.get("lane"), xmlEntity.getXmlUid());
            HashMap<String, Object> resMap = new HashMap<>();
            resMap.put("lanes", entites);
            return resMap;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public LaneEntity getOtherInfoById(Long id) {
        return baseMapper.selectById(id);
    }


    @Override
    public List<LaneEntity> getEntitesWithPointsInfoFromMap(Map<String, JSONArray> entitys, String xmlUid) {
        ArrayList<LaneEntity> entities = new ArrayList<>();
        entitys.forEach((uid, v1) -> {
            LaneEntity entity = this.getInfoByXmlUidAndObjId("uid", uid, xmlUid);
            System.out.println(entity);
            entity.setPoints(v1);
            entities.add(entity);
        });
        return entities;
    }

    public List<LeftBorder> getBorderEntitesWithPointsInfoFromMap(Map<String, JSONArray> entitys) {
        if (entitys==null)
            return null;
        ArrayList<LeftBorder> entities = new ArrayList<>();
        entitys.forEach((uid, v1) -> {
            LeftBorder entity = new LeftBorder();
            entity.setUid(uid);
            System.out.println(entity);
            entity.setPoints(v1);
            entities.add(entity);
        });
        return entities;
    }
}
