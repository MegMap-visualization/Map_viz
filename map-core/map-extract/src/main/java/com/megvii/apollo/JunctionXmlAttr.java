package com.megvii.apollo;

import com.megvii.entity.JunctionEntity;
import com.megvii.service.JunctionService;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Element;
import org.dom4j.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Component
@Slf4j
public class JunctionXmlAttr extends XmlAttrBase<List<JunctionEntity>> {

    @Autowired
    private JunctionService junctionService;

    @Override
    public XmlAttrBase<List<JunctionEntity>> getAttrEntity(List<Node> nodeList) {
        log.info(xmlUid + ": Start junction data extraction and save poins to es");
        log.info("数据行：" + nodeList.size());
        nodeNum = nodeList.size();
        entityList = new CopyOnWriteArrayList<>(nodeList).stream()
                .filter(node -> {
                    Element ele = (Element) node;
                    return ele.element("outline") != null;
                })
                .map(node -> {
                    Element ele = (Element) node;
                    JunctionEntity junctionEntity = new JunctionEntity();
                    junctionEntity.setXmlUid(xmlUid);
                    String junctionId = ele.attributeValue("id");
                    junctionEntity.setJunctionId(junctionId);
                    junctionEntity.setType("junction");
                    List<Element> elements = ele.element("outline").elements("cornerGlobal");
                    getObjPointsAndSaveToEs(elements, 1, junctionId,"junction");
                    junctionEntity.setConnection(parseConnection(ele));
                    return junctionEntity;
                }).collect(Collectors.toList());
        return this;
    }

    private String parseConnection(Element ele) {
        return ele.elements("connection").stream().map(con -> {
            String incomingRoad = con.attributeValue("incomingRoad");
            String connectingRoad = con.attributeValue("connectingRoad");
            String contactPoint = con.attributeValue("contactPoint");
            return incomingRoad + ";" + connectingRoad + ";" + contactPoint;
        }).collect(Collectors.toList()).toString();
    }

    @Override
    @Transactional
    public void saveToDb() throws IOException {
        log.info(xmlUid + ": Complete junction data extraction -> Start save to db");
        junctionService.saveBatch(entityList, 20);
        overOpt();
    }
}
