package com.megvii.apollo;

import com.alibaba.fastjson.JSONArray;
import com.megvii.exception.CommonException;
import com.megvii.entity.LaneEntity;
import com.megvii.service.LaneService;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Element;
import org.dom4j.Node;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Component
@Slf4j
public class LaneXmlAttr extends XmlAttrBase<List<LaneEntity>> {
    private final ConcurrentHashMap<String, String> methodMap = new ConcurrentHashMap<>();

    @Resource
    LaneService laneService;

    public LaneXmlAttr() {
        this.methodMap.put("right", "RightUid");
        this.methodMap.put("left", "LeftUid");
    }

    /**
     * @param nodeList 提取所有的lane
     * @return this用于save调用
     */
    @Override
    public XmlAttrBase<List<LaneEntity>> getAttrEntity(List<Node> nodeList) {
        log.info(xmlUid + ": Start lane data extraction and save poins to es");
        log.info("数据行：" + nodeList.size());

        nodeNum = nodeList.size();
        entityList = new CopyOnWriteArrayList<>(nodeList).stream()
                .map(node -> {
                    LaneEntity laneEntity = new LaneEntity();
                    Element ele = (Element) node;
                    String laneUid = ele.attributeValue("uid");
//                    System.out.println("start parse lane uid:"+laneUid);
                    String laneType = ele.attributeValue("type");
                    String turnType = ele.attributeValue("turnType");
                    String[] uidList = laneUid.split("_");
                    parseRoadType(uidList, laneEntity);
                    laneEntity.setXmlUid(xmlUid);
                    laneEntity.setUid(laneUid);
                    laneEntity.setLaneType(laneType);
                    laneEntity.setTurnType(turnType);
                    if (!laneUid.contains("~"))
                        laneEntity.setRoadSection(uidList[0] + "_" + uidList[1]);
                    laneEntity.setLaneId(ele.attributeValue("id"));

                    parseSpeed(ele, laneEntity);
                    parseObjectSignalOverlap(ele, laneEntity);
                    Map<String, String> otherInfo = new HashMap<>();
                    otherInfo.putAll(parseLink(ele));
                    otherInfo.putAll(parseBoder(ele, laneUid));
                    parseLeftBoder(ele, laneUid);
                    // 反射设置值
                    otherInfo.forEach((k, v) -> {
                        Method setAttr;
                        try {
                            setAttr = LaneEntity.class.getMethod("set" + k, String.class);
                            setAttr.invoke(laneEntity, v);
                        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                            throw new CommonException(e.getMessage());
                        }
                    });
                    return laneEntity;
                }).collect(Collectors.toList());
        return this;
    }

    private void parseRoadType(String[] uidList, LaneEntity laneEntity) {
        if ("0".equals(uidList[uidList.length - 1])) {
            List<String> uids = List.of(uidList);
            List<String> collect = uids.stream().limit(uids.size() - 2).collect(Collectors.toList());
            String wayId = String.join("_", collect);
            String roadType = this.roadType.get(wayId);
            laneEntity.setRoadType(roadType);
        }
    }

    private Map<String, String> parseBoder(Element ele, String laneUid) {
        Element border = ele.element("border");
        HashMap<String, String> map = new HashMap<>();
        if (border != null) {
            String virtual = border.attributeValue("virtual");
            Element borderType = border.element("borderType");
            if (borderType != null) {
                String type = borderType.attributeValue("type");
                String color = borderType.attributeValue("color");
                Element geometry = border.element("geometry");
                String length = geometry.attributeValue("length");
                List<Element> elements = geometry.element("pointSet").elements();
                getGcjPointListAndSaveToEs(elements, 10, laneUid, "lane", laneUid.endsWith("0"));
                map.put("Uid", laneUid);
                map.put("LaneBorderType", type);
                map.put("Color", color);
                map.put("IsVirtual", virtual);
                map.put("Length", length);
            }
        }
        return map;
    }

    private Map<String, String> parseLeftBoder(Element ele, String laneUid) {
        Element border = ele.element("leftBorder");
        HashMap<String, String> map = new HashMap<>();
        if (border != null) {
            String virtual = border.attributeValue("virtual");
            Element borderType = border.element("borderType");
            if (borderType != null) {
                String type = borderType.attributeValue("type");
                String color = borderType.attributeValue("color");
                Element geometry = border.element("geometry");
                String length = geometry.attributeValue("length");
                List<Element> elements = geometry.element("pointSet").elements();
                getGcjPointListAndSaveToEs(elements, 10, laneUid, "leftBorder");
            }
        }
        return map;
    }

    private void parseObjectSignalOverlap(Element ele, LaneEntity laneEntity) {
        laneEntity.setObjectOverlapIds(new JSONArray(ele.element("objectOverlapGroup").elements("objectReference").stream().map(ref -> ref.attributeValue("id")).distinct().collect(Collectors.toList())).toString());
        laneEntity.setSignalOverlapIds(new JSONArray(ele.element("signalOverlapGroup").elements("signalReference").stream().map(ref -> ref.attributeValue("id")).distinct().collect(Collectors.toList())).toString());
    }

    private void parseSpeed(Element ele, LaneEntity laneEntity) {
        Element speedEle = ele.element("speed");
        if (speedEle != null) {
            laneEntity.setSpeedLimit("0~" + speedEle.attributeValue("max"));
        } else {
            if ("0".equals(laneEntity.getLaneId()))
                laneEntity.setSpeedLimit("None");
            else
                laneEntity.setSpeedLimit("0~80");
        }
    }

    private Map<String, String> parseLink(Element ele) {
        Element link = ele.element("link");
        HashMap<String, String> map = new HashMap<>();
        if (link == null)
            return map;
        for (Element neighbor : link.elements("neighbor")) {
            map.put(this.methodMap.get(neighbor.attributeValue("side")), neighbor.attributeValue("id"));
        }
        StringBuilder preStr = new StringBuilder();
        StringBuilder sucStr = new StringBuilder();
        for (Element predecessor : link.elements("predecessor")) {
            preStr.append(";").append(predecessor.attributeValue("id"));
        }
        for (Element successor : link.elements("successor")) {
            sucStr.append(";").append(successor.attributeValue("id"));
        }
        map.put("Pre", preStr.toString().replaceFirst(";", ""));
        map.put("Suc", sucStr.toString().replaceFirst(";", ""));
        return map;
    }

    @Override
    @Transactional
    public void saveToDb() throws IOException {
        log.info(xmlUid + ": Complete lane data extraction -> Start save to db");
        laneService.saveBatch(entityList, 50);
        overOpt();
    }
}
