package com.megvii.apollo;

import com.megvii.entity.CenterLaneEntity;
import com.megvii.service.CenterLaneService;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Element;
import org.dom4j.Node;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Component
@Slf4j
public class CenterLaneXmlAttr extends XmlAttrBase<List<CenterLaneEntity>> {
    @Resource
    private CenterLaneService centerLaneService;

    @Override
    public XmlAttrBase<List<CenterLaneEntity>> getAttrEntity(List<Node> nodeList) {

        List<Node> collect = new CopyOnWriteArrayList<>(nodeList).parallelStream()
                .filter(node -> {
                    Element element = (Element) node;
                    return !element.attributeValue("uid").endsWith("_0");
                }).collect(Collectors.toList());
        log.info(xmlUid + ": Start center lane data extraction and save poins to es");
        log.info("数据行：" + collect.size());
        nodeNum = collect.size();
        entityList = new CopyOnWriteArrayList<>(collect).stream()
                .map(node -> {
                    CenterLaneEntity centerLaneEntity = new CenterLaneEntity();
                    Element ele = (Element) node;
                    String laneUid = ele.attributeValue("uid");
                    List<Element> elements = ele.element("centerLine").element("geometry").element("pointSet").elements("point");
                    getGcjPointListAndSaveToEs(elements, 10, laneUid, "centerLane");
                    centerLaneEntity.setXmlUid(xmlUid);
                    centerLaneEntity.setLaneUid(laneUid);
                    return centerLaneEntity;
                }).collect(Collectors.toList());
        return this;
    }

    @Override
    @Transactional
    public void saveToDb() throws IOException {
        log.info(xmlUid + ": Complete center lane data extraction -> Start save to db");
        centerLaneService.saveBatch(entityList, 50);
        overOpt();
    }
}
