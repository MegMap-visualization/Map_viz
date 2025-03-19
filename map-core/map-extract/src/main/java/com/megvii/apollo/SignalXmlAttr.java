package com.megvii.apollo;

import com.alibaba.fastjson.JSONArray;
import com.megvii.entity.SignalEntity;
import com.megvii.service.SignalService;
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
public class SignalXmlAttr extends XmlAttrBase<Set<SignalEntity>> {
    @Resource
    SignalService signalService;

    @Override
    public XmlAttrBase<Set<SignalEntity>> getAttrEntity(List<Node> nodeList) {
        log.info(xmlUid + ": Start signal data extraction and save poins to es");
        log.info("数据行：" + nodeList.size());

        nodeNum = nodeList.size();
        entityList = new CopyOnWriteArrayList<>(nodeList).stream()
                .filter(node -> {
                    Element ele = (Element) node;
                    return ele.element("outline") != null;
                })
                .map(node -> {
                    Element ele = (Element) node;
                    SignalEntity signalEntity = new SignalEntity();
                    signalEntity.setXmlUid(xmlUid);
                    String signalId = ele.attributeValue("id");
                    signalEntity.setSignalId(signalId);
                    signalEntity.setType(ele.attributeValue("type"));
                    signalEntity.setLayoutType(ele.attributeValue("layoutType"));
                    List<Element> elements;
                    elements = ele.element("outline").elements("cornerGlobal");
                    getObjPointsAndSaveToEs(elements, 1, signalId,"signal");
                    getStoplineAttr(signalEntity, node);
                    return signalEntity;
                }).collect(Collectors.toSet());
        return this;
    }

    private void getStoplineAttr(SignalEntity signalEntity, Node node) {
        Element ele = (Element) node;
        if (ele.element("stopline") == null)
            return;
        JSONArray stoplineArr = new JSONArray();
        Set<String> collect = ele.element("stopline").elements("objectReference").stream().map(ref -> ref.attributeValue("id"))
                .collect(Collectors.toSet());
        stoplineArr.addAll(collect);
        signalEntity.setStoplineIds(stoplineArr.toString());
    }

    @Override
    @Transactional
    public void saveToDb() throws IOException {
        log.info(xmlUid + ": Complete signal data extraction -> Start save to db");
        signalService.saveBatch(entityList, 20);
        overOpt();
    }
}
