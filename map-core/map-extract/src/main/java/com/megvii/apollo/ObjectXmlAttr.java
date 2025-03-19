package com.megvii.apollo;

import com.megvii.entity.ObjectEntity;
import com.megvii.service.ObjectService;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Element;
import org.dom4j.Node;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ObjectXmlAttr extends XmlAttrBase<Set<ObjectEntity>> {
    @Resource
    private ObjectService apolloObjectService;


    @Override
    public XmlAttrBase<Set<ObjectEntity>> getAttrEntity(List<Node> nodeList) {
        log.info(xmlUid+": Start object data extraction and save poins to es");
        log.info("数据行：" + nodeList.size());

        nodeNum = nodeList.size();
        entityList = new CopyOnWriteArrayList<>(nodeList).stream()
                .filter(node -> {
                    Element ele = (Element) node;
                    return ele.element("outline") != null || ele.element("geometry") != null;
                })
                .map(node -> {
                    Element ele = (Element) node;
                    ObjectEntity objectOutlineEntity = new ObjectEntity();
                    objectOutlineEntity.setXmlUid(xmlUid);
                    String objectId = ele.attributeValue("id");
                    objectOutlineEntity.setObjectId(objectId);
                    objectOutlineEntity.setType("junction".equals(ele.getName()) ? "junction" : ele.attributeValue("type"));
                    List<Element> elements;
                    if (ele.element("outline") != null)
                        elements = ele.element("outline").elements("cornerGlobal");
                    else
                        elements = ele.element("geometry").element("pointSet").elements("point");
                    getObjPointsAndSaveToEs(elements, 1, objectId,"object");
                    return objectOutlineEntity;
                }).collect(Collectors.toSet());
        return this;
    }

    @Override
    @Transactional
    public void saveToDb() throws IOException {
        log.info(xmlUid+": Complete object data extraction -> Start save to db");
        apolloObjectService.saveBatch(entityList, 20);
        overOpt();
    }
}
