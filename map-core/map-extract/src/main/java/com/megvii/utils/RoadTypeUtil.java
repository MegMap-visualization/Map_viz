package com.megvii.utils;

import org.dom4j.Element;
import org.dom4j.Node;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RoadTypeUtil {
    public ConcurrentHashMap<String, String> getRoadType(List<Node> nodeList) {
        ConcurrentHashMap<String, String> wayIdRoadType = new ConcurrentHashMap<>();
        nodeList.parallelStream().forEach(road -> {
            Element ele = (Element) road;
            String id = ele.attributeValue("id");
            String roadType = ele.attributeValue("type");
            wayIdRoadType.put(id, roadType);
        });
        return wayIdRoadType;
    }

}
